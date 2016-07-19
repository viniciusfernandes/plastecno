package br.com.plastecno.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
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
	private PedidoService pedidoService;

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
			p.setNCM(i.getNcm() != null ? i.getNcm().replaceAll("\\.", "")
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

	private void carregarIPI(TributosProdutoServico t, ItemPedido i) {
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

	private void carregarIdentificacaoDestinatario(NFe nFe, Integer idPedido) {
		Cliente destinatario = pedidoService
				.pesquisarClienteByIdPedido(idPedido);

		IdentificacaoDestinatarioNFe iDest = new IdentificacaoDestinatarioNFe();
		iDest.setEmail(destinatario.getEmail());
		iDest.setInscricaoEstadual(destinatario.getInscricaoEstadual());
		iDest.setInscricaoMunicipal(null);
		iDest.setInscricaoSUFRAMA(null);
		iDest.setNomeFantasia(destinatario.getNomeFantasia());
		iDest.setTipoContribuinte(null);
		iDest.setTipoDocumento(null);
		iDest.setNomeFantasia(destinatario.getNomeFantasia());

		EnderecoNFe endDest = new EnderecoNFe();
		Logradouro logradouroEmit = destinatario.getLogradouroFaturamento();
		endDest.setBairro(logradouroEmit.getBairro());
		endDest.setCep(logradouroEmit.getCep());
		endDest.setCodigoPais(String.valueOf(55));
		endDest.setComplemento(logradouroEmit.getComplemento());
		endDest.setLogradouro(logradouroEmit.getEndereco());
		endDest.setNomeMunicipio(logradouroEmit.getCidade());
		endDest.setNomePais(logradouroEmit.getPais());
		endDest.setNumero(logradouroEmit.getNumero() == null ? "" : String
				.valueOf(logradouroEmit.getNumero()));
		endDest.setUF(logradouroEmit.getUf());
		endDest.setNomePais(logradouroEmit.getPais());
		endDest.setTelefone(null);

		iDest.setEnderecoDestinatarioNFe(endDest);

		nFe.setIdentificacaoDestinatarioNFe(iDest);
	}

	private void carregarIdentificacaoEmitente(NFe nFe, Integer idPedido) {
		Representada emitente = pedidoService
				.pesquisarRepresentadaIdPedido(idPedido);

		IdentificacaoEmitenteNFe iEmit = new IdentificacaoEmitenteNFe();
		iEmit.setCNPJ(emitente.getCnpj());
		iEmit.setInscricaoEstadual(emitente.getInscricaoEstadual());
		iEmit.setNomeFantasia(emitente.getNomeFantasia());
		iEmit.setRazaoSocial(emitente.getRazaoSocial());

		Logradouro logradouroEmit = emitente.getLogradouro();
		EnderecoNFe endEmit = new EnderecoNFe();
		endEmit.setBairro(logradouroEmit.getBairro());
		endEmit.setCep(logradouroEmit.getCep());
		endEmit.setCodigoPais(String.valueOf(55));
		endEmit.setComplemento(logradouroEmit.getComplemento());
		endEmit.setLogradouro(logradouroEmit.getEndereco());
		endEmit.setNomeMunicipio(logradouroEmit.getCidade());
		endEmit.setNomePais(logradouroEmit.getPais());
		endEmit.setNumero(logradouroEmit.getNumero() == null ? "" : String
				.valueOf(logradouroEmit.getNumero()));
		endEmit.setUF(logradouroEmit.getUf());
		endEmit.setNomePais(logradouroEmit.getPais());
		endEmit.setTelefone(null);

		iEmit.setEnderecoEmitenteNFe(endEmit);

		nFe.setIdentificacaoEmitenteNFe(iEmit);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe gerarNfe(Integer idPedido) {
		NFe nFe = new NFe();

		carregarIdentificacaoEmitente(nFe, idPedido);
		carregarIdentificacaoDestinatario(nFe, idPedido);
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
			m.marshal(nFe, System.out);
		} catch (Exception e) {
			throw new BusinessException(
					"Falha na geracao do XML da NFe do pedido No. ", e);
		}
	}
}
