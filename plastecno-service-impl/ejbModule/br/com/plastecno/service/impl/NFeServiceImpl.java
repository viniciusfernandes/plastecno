package br.com.plastecno.service.impl;

import java.io.StringWriter;
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
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.COFINSGeral;
import br.com.plastecno.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.ICMSGeral;
import br.com.plastecno.service.nfe.IPIGeral;
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.IdentificacaoEmitenteNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.PISGeral;
import br.com.plastecno.service.nfe.ProdutoServicoNFe;
import br.com.plastecno.service.nfe.TributosProdutoServico;
import br.com.plastecno.service.nfe.ValorTotalICMS;
import br.com.plastecno.service.nfe.ValoresTotaisNFe;

@Stateless
public class NFeServiceImpl implements NFeService {

	@EJB
	private ClienteService clienteService;

	@EJB
	private PedidoService pedidoService;

	@EJB
	private RepresentadaService representadaService;

	private void calcularValoresTotaisICMS(NFe nFe) {
		ValoresTotaisNFe valoresTotaisNFe = nFe.getValoresTotaisNFe();
		ValorTotalICMS valorTotalICMS = new ValorTotalICMS();
		List<DetalhamentoProdutoServicoNFe> listaItem = nFe
				.getListaDetalhamentoProdutoServicoNFe();

		ICMSGeral tipoIcms = null;
		IPIGeral tipoIpi = null;
		PISGeral tipoPis = null;
		COFINSGeral tipoCofins = null;
		ProdutoServicoNFe produto = null;
		TributosProdutoServico tributo = null;
		double valorBC = 0;
		double valorBCST = 0;
		double valor = 0;
		double valorSeguro = 0;
		double valorFrete = 0;
		double valorImportacao = 0;
		double valorIPI = 0;
		double valorPIS = 0;
		double valorCOFINS = 0;
		for (DetalhamentoProdutoServicoNFe item : listaItem) {
			tributo = item.getTributosProdutoServico();
			if (tributo != null && tributo.contemICMS()) {
				tipoIcms = item.getTributosProdutoServico().getTipoIcms();
				valorBC += tipoIcms.getValorBC() == null ? 0 : tipoIcms
						.getValorBC();
				valorBCST += tipoIcms.getValorBCST() == null ? 0 : tipoIcms
						.getValorBCST();
				valor += tipoIcms.getValor() == null ? 0 : tipoIcms.getValor();
			}

			if (tributo != null && tributo.contemIPI()) {
				tipoIpi = tributo.getTipoIpi();
				valorIPI += tipoIpi.getValor() == null ? 0 : tipoIpi.getValor();
			}

			if (tributo != null && tributo.contemPIS()) {
				tipoPis = tributo.getTipoPis();
				valorPIS += tipoPis.getValor() == null ? 0 : tipoPis.getValor();
			}

			if (tributo != null && tributo.contemCOFINS()) {
				tipoCofins = tributo.getTipoCofins();
				valorPIS += tipoCofins.getValor() == null ? 0 : tipoCofins
						.getValor();
			}

			produto = item.getProdutoServicoNFe();
			valorSeguro += produto.getValorTotalSeguro() == null ? 0 : produto
					.getValorTotalSeguro();

			valorFrete += produto.getValorTotalFrete() == null ? 0 : produto
					.getValorTotalFrete();
			if (tributo != null && tributo.contemImpostoImportacao()) {
				valorImportacao += tributo.getImpostoImportacao().getValor() == null ? 0
						: tributo.getImpostoImportacao().getValor();
			}
		}

		valorTotalICMS.setValorBaseCalculo(valorBC);
		valorTotalICMS.setValorBaseCalculoST(valorBCST);
		valorTotalICMS.setValorTotal(valor);
		valorTotalICMS.setValorTotalFrete(valorFrete);
		valorTotalICMS.setValorTotalII(valorImportacao);
		valorTotalICMS.setValorTotalIPI(valorIPI);
		valorTotalICMS.setValorTotalSeguro(valorSeguro);
		valorTotalICMS.setValorTotalPIS(valorPIS);
		valorTotalICMS.setValorTotalCOFINS(valorCOFINS);

		valoresTotaisNFe.setValorTotalICMS(valorTotalICMS);
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

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String emitirNFe(NFe nFe, Integer idPedido) throws BusinessException {
		nFe.setValoresTotaisNFe(new ValoresTotaisNFe());
		calcularValoresTotaisICMS(nFe);
		carregarIdentificacaoEmitente(nFe, idPedido);
		return gerarXMLNfe(nFe);
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
	public String gerarXMLNfe(NFe nFe) throws BusinessException {
		try {
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(NFe.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(nFe, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new BusinessException(
					"Falha na geracao do XML da NFe do pedido No. ", e);
		}
	}
}
