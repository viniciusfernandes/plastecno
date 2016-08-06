package br.com.plastecno.service.impl;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.DadosNFe;
import br.com.plastecno.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.ICMSGeral;
import br.com.plastecno.service.nfe.IdentificacaoEmitenteNFe;
import br.com.plastecno.service.nfe.NFe;
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

	private void calcularValoresTotaisICMS(DadosNFe dadosNFe) {
		ValoresTotaisNFe valoresTotaisNFe = dadosNFe.getValoresTotaisNFe();
		ValorTotalICMS valorTotalICMS = new ValorTotalICMS();
		List<DetalhamentoProdutoServicoNFe> listaItem = dadosNFe
				.getListaDetalhamentoProdutoServicoNFe();

		if (listaItem == null || listaItem.isEmpty()) {
			return;
		}

		ICMSGeral tipoIcms = null;
		ProdutoServicoNFe produto = null;
		TributosProdutoServico tributo = null;
		double valorBC = 0;
		double valorBCST = 0;
		double valorSeguro = 0;
		double valorFrete = 0;
		double valorImportacao = 0;
		double valorIPI = 0;
		double valorPIS = 0;
		double valorCOFINS = 0;
		double valorICMS = 0;
		double valorProduto = 0;
		double valorTotalDesconto = 0;
		double valorDespAcessorias = 0;

		for (DetalhamentoProdutoServicoNFe item : listaItem) {
			tributo = item.getTributosProdutoServico();
			if (tributo != null && tributo.contemICMS()) {
				tipoIcms = item.getTributosProdutoServico().getTipoIcms();

				valorBC += tipoIcms.getValorBC();
				valorBCST += tipoIcms.getValorBCST();
				valorICMS += tipoIcms.getValor();

			}

			if (tributo != null && tributo.contemIPI()) {
				valorIPI += tributo.getTipoIpi().getValor();
			}

			if (tributo != null && tributo.contemPIS()) {
				valorPIS += tributo.getTipoPis().getValor();
			}

			if (tributo != null && tributo.contemCOFINS()) {
				valorCOFINS += tributo.getTipoCofins().getValor();
			}

			if (tributo != null && tributo.contemImpostoImportacao()) {
				valorImportacao += tributo.getImpostoImportacao().getValor();
			}

			produto = item.getProdutoServicoNFe();

			valorSeguro += produto.getValorTotalSeguro();
			valorFrete += produto.getValorTotalFrete();
			valorProduto += produto.getValorTotalBruto();
			valorTotalDesconto += produto.getValorDesconto();
			valorDespAcessorias += produto.getOutrasDespesasAcessorias();
		}

		valorTotalICMS.setValorBaseCalculo(valorBC);
		valorTotalICMS.setValorBaseCalculoST(valorBCST);
		valorTotalICMS.setValorTotalICMS(valorICMS);
		valorTotalICMS.setValorTotalFrete(valorFrete);
		valorTotalICMS.setValorTotalII(valorImportacao);
		valorTotalICMS.setValorTotalIPI(valorIPI);
		valorTotalICMS.setValorTotalSeguro(valorSeguro);
		valorTotalICMS.setValorTotalPIS(valorPIS);
		valorTotalICMS.setValorTotalCOFINS(valorCOFINS);
		valorTotalICMS.setValorTotalProdutosServicos(valorProduto);
		valorTotalICMS.setValorTotalDesconto(valorTotalDesconto);
		valorTotalICMS.setValorTotalDespAcessorias(valorDespAcessorias);
		valorTotalICMS.setValorTotalNF(valorProduto);

		valoresTotaisNFe.setValorTotalICMS(valorTotalICMS);
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

		EnderecoNFe endEmit = gerarEnderecoNFe(representadaService
				.pesquisarLogradorouro(emitente.getId()));
		endEmit.setTelefone(emitente.getTelefone());

		iEmit.setEnderecoEmitenteNFe(endEmit);
		nFe.getDadosNFe().setIdentificacaoEmitenteNFe(iEmit);
		return nFe;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String emitirNFe(NFe nFe, Integer idPedido) throws BusinessException {
		DadosNFe dadosNFe = nFe.getDadosNFe();
		dadosNFe.setValoresTotaisNFe(new ValoresTotaisNFe());
		calcularValoresTotaisICMS(dadosNFe);
		carregarIdentificacaoEmitente(nFe, idPedido);
		return gerarXMLNfe(nFe);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<DuplicataNFe> gerarDuplicataByIdPedido(Integer idPedido) {
		List<Date> listaData = pedidoService.calcularDataPagamento(idPedido);
		double totalParcelas = listaData.size();
		if (totalParcelas == 0) {
			return new ArrayList<DuplicataNFe>();
		}
		Double valorPedido = pedidoService.pesquisarValorPedido(idPedido);

		Double valorDuplicata = valorPedido != null ? valorPedido
				/ totalParcelas : 0;
		List<DuplicataNFe> listaDuplicata = new ArrayList<DuplicataNFe>();
		DuplicataNFe dup = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < totalParcelas; i++) {
			dup = new DuplicataNFe();
			dup.setDataVencimento(df.format(listaData.get(i)));
			dup.setNumero(String.valueOf(i + 1));
			dup.setValor(valorDuplicata);

			listaDuplicata.add(dup);
		}
		return listaDuplicata;
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
