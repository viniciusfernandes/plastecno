package br.com.plastecno.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.ICMS;
import br.com.plastecno.service.nfe.ICMSIntegral;
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.IdentificacaoEmitenteNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.ProdutoServicoNFe;
import br.com.plastecno.service.nfe.TributosProdutoServico;

@Stateless
public class NFeServiceImpl implements NFeService {

	@EJB
	private ClienteService clienteService;

	@EJB
	private PedidoService pedidoService;

	@EJB
	private RepresentadaService representadaService;

	private void carregarDetalhamentoProdutoServico(NFe nFe, Integer idPedido) {
		List<ItemPedido> listaItem = pedidoService
				.pesquisarItemPedidoByIdPedido(idPedido);
		DetalhamentoProdutoServicoNFe d = null;
		ProdutoServicoNFe p = null;
		String descricao;
		TributosProdutoServico t = null;

		for (ItemPedido i : listaItem) {

			d = new DetalhamentoProdutoServicoNFe();
			p = new ProdutoServicoNFe();
			t = new TributosProdutoServico();

			descricao = i.getDescricaoSemFormatacao();

			p.setNumeroPedidoCompra(idPedido.toString());
			p.setItemPedidoCompra(i.getSequencial());
			p.setCFOP(null);
			p.setCodigo(descricao);
			p.setDescricao(descricao);
			p.setNcm(i.getNcm() != null ? i.getNcm().replaceAll("\\.", "")
					: null);
			p.setQuantidadeComercial(i.getQuantidade());
			p.setQuantidadeTributavel(i.getQuantidade());
			p.setUnidadeComercial(i.getTipoVenda().toString());
			p.setUnidadeTributavel(i.getTipoVenda().toString());

			carregarICMS(t, i);
			carregarIPI(t, i);

			d.setProdutoServicoNFe(p);
			d.setTributosProdutoServico(t);

			nFe.addDetalhamentoProdutoServico(d);
		}
	}

	private void carregarICMS(TributosProdutoServico t, ItemPedido i) {
		ICMS icms = null;
		ICMSIntegral icms00 = new ICMSIntegral();
		icms = new ICMS();

		icms00.setAliquota(i.getAliquotaICMS());
		icms00.setValorBC(i.calcularPrecoTotal());
		icms00.setValor(i.calcularValorICMS());
		icms.setICMSIntegral(icms00);
		t.setICMS(icms);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe carregarIdentificacaoDestinatario(NFe nFe, Integer idPedido) {
		Cliente destinatario = pedidoService
				.pesquisarClienteByIdPedido(idPedido);

		destinatario.setListaLogradouro(clienteService
				.pesquisarLogradouro(destinatario.getId()));

		IdentificacaoDestinatarioNFe iDest = new IdentificacaoDestinatarioNFe();
		iDest.setEmail(destinatario.getEmail());
		iDest.setInscricaoEstadual(destinatario.getInscricaoEstadual());
		iDest.setInscricaoMunicipal(null);
		iDest.setInscricaoSUFRAMA(null);
		iDest.setNomeFantasia(destinatario.getNomeFantasia());
		iDest.setNomeFantasia(destinatario.getNomeFantasia());

		iDest.setEnderecoDestinatarioNFe(gerarEnderecoNFe(destinatario
				.getLogradouroFaturamento()));

		nFe.setIdentificacaoDestinatarioNFe(iDest);
		return nFe;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe carregarIdentificacaoEmitente(NFe nFe, Integer idPedido) {
		Representada emitente = pedidoService
				.pesquisarRepresentadaIdPedido(idPedido);

		IdentificacaoEmitenteNFe iEmit = new IdentificacaoEmitenteNFe();
		iEmit.setCNPJ(emitente.getCnpj());
		iEmit.setInscricaoEstadual(emitente.getInscricaoEstadual());
		iEmit.setNomeFantasia(emitente.getNomeFantasia());
		iEmit.setRazaoSocial(emitente.getRazaoSocial());

		iEmit.setEnderecoEmitenteNFe(gerarEnderecoNFe(representadaService
				.pesquisarLogradorouro(emitente.getId())));

		nFe.setIdentificacaoEmitenteNFe(iEmit);
		return nFe;
	}

	private void carregarIPI(TributosProdutoServico t, ItemPedido i) {
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void emitirNFe(NFe nFe, Integer idPedido) throws BusinessException {
		carregarIdentificacaoEmitente(nFe, idPedido);
		gerarXMLNfe(nFe);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public EnderecoNFe gerarEnderecoNFe(Logradouro logradouro) {
		if (logradouro == null) {
			return null;
		}
		EnderecoNFe endereco = new EnderecoNFe();

		endereco.setBairro(logradouro.getBairro());
		endereco.setCep(logradouro.getCep());
		endereco.setCodigoPais(String.valueOf(55));
		endereco.setComplemento(logradouro.getComplemento());
		endereco.setLogradouro(logradouro.getEndereco());
		endereco.setNomeMunicipio(logradouro.getCidade());
		endereco.setNomePais(logradouro.getPais());
		endereco.setNumero(logradouro.getNumero() == null ? "" : String
				.valueOf(logradouro.getNumero()));
		endereco.setUF(logradouro.getUf());
		endereco.setNomePais(logradouro.getPais());
		endereco.setTelefone(null);

		return endereco;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe gerarNfe(Integer idPedido) {
		NFe nFe = new NFe();

		carregarIdentificacaoEmitente(nFe, idPedido);
		carregarDetalhamentoProdutoServico(nFe, idPedido);
		return nFe;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void gerarXMLNfe(NFe nFe) throws BusinessException {
		try {
			JAXBContext context = JAXBContext.newInstance(NFe.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(nFe, System.out);
		} catch (Exception e) {
			throw new BusinessException(
					"Falha na geracao do XML da NFe do pedido No. ", e);
		}
	}
}
