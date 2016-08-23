package br.com.plastecno.service.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ConfiguracaoSistemaService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.dao.PedidoNFeDAO;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.PedidoNFe;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.ICMSGeral;
import br.com.plastecno.service.nfe.IdentificacaoEmitenteNFe;
import br.com.plastecno.service.nfe.IdentificacaoLocalGeral;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.ProdutoServicoNFe;
import br.com.plastecno.service.nfe.TributosProdutoServico;
import br.com.plastecno.service.nfe.ValoresTotaisICMS;
import br.com.plastecno.service.nfe.ValoresTotaisISSQN;
import br.com.plastecno.service.nfe.ValoresTotaisNFe;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class NFeServiceImpl implements NFeService {

	@EJB
	private ClienteService clienteService;

	@EJB
	private ConfiguracaoSistemaService configuracaoSistemaService;

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	@EJB
	private LogradouroService logradouroService;

	private PedidoNFeDAO pedidoNFeDAO = null;

	@EJB
	private PedidoService pedidoService;

	@EJB
	private RepresentadaService representadaService;

	private void carregarConfiguracao(NFe nFe) {
		nFe.getDadosNFe().setId("12345678901234567890123456789012345678901234567");
		nFe.getDadosNFe().getIdentificacaoEmitenteNFe().setCNAEFiscal("1234567");
		ProdutoServicoNFe p = null;
		for (DetalhamentoProdutoServicoNFe d : nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe()) {
			p = d.getProdutoServicoNFe();
			p.setEAN("EanTEste");
			p.setEANTributavel("EanTEste");
			p.setIndicadorValorTotal(1);
		}
	}

	private void carregarDadosLocalRetiradaEntrega(NFe nFe) {
		IdentificacaoLocalGeral retirada = nFe.getDadosNFe().getIdentificacaoLocalRetirada();
		IdentificacaoLocalGeral entrega = nFe.getDadosNFe().getIdentificacaoLocalEntrega();

		if (retirada != null) {
			retirada.setCodigoMunicipio(logradouroService.pesquisarCodigoIBGEByCEP(retirada.getCep()));
		}
		if (entrega != null) {
			entrega.setCodigoMunicipio(logradouroService.pesquisarCodigoIBGEByCEP(entrega.getCep()));
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe carregarIdentificacaoEmitente(NFe nFe, Integer idPedido) {

		Representada emitente = pedidoService.pesquisarRepresentadaIdPedido(idPedido);

		if (emitente == null) {
			return nFe;
		}

		IdentificacaoEmitenteNFe iEmit = new IdentificacaoEmitenteNFe();
		iEmit.setCNPJ(emitente.getCnpj());
		iEmit.setInscricaoEstadual(emitente.getInscricaoEstadual());
		iEmit.setNomeFantasia(emitente.getNomeFantasia());
		iEmit.setRazaoSocial(emitente.getRazaoSocial());
		iEmit.setRegimeTributario(configuracaoSistemaService.pesquisar(ParametroConfiguracaoSistema.REGIME_TRIBUTACAO));

		EnderecoNFe endEmit = gerarEnderecoNFe(representadaService.pesquisarLogradorouro(emitente.getId()),
				emitente.getTelefone());

		iEmit.setEnderecoEmitenteNFe(endEmit);
		nFe.getDadosNFe().setIdentificacaoEmitenteNFe(iEmit);
		return nFe;
	}

	private void carregarValoresTotaisNFe(NFe nFe) {
		ValoresTotaisNFe valoresTotaisNFe = new ValoresTotaisNFe();
		ValoresTotaisICMS totaisICMS = new ValoresTotaisICMS();
		ValoresTotaisISSQN totaisISSQN = new ValoresTotaisISSQN();

		List<DetalhamentoProdutoServicoNFe> listaItem = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();

		if (listaItem == null || listaItem.isEmpty()) {
			return;
		}

		ICMSGeral tipoIcms = null;
		ProdutoServicoNFe produto = null;
		TributosProdutoServico tributo = null;
		double valorBC = 0;
		double valorBCST = 0;
		double valorST = 0;
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
		double valorBCISS = 0;
		double valorISS = 0;

		for (DetalhamentoProdutoServicoNFe item : listaItem) {
			tributo = item.getTributosProdutoServico();
			if (tributo != null && tributo.contemICMS()) {
				tipoIcms = item.getTributosProdutoServico().getTipoIcms();

				valorBC += tipoIcms.getValorBC();
				valorBCST += tipoIcms.getValorBCST();
				valorST += tipoIcms.getValorST();
				valorICMS += tipoIcms.carregarValoresAliquotas().getValor();
			}

			if (tributo != null && tributo.contemIPI()) {
				valorIPI += tributo.getTipoIpi().carregarValoresAliquotas().getValor();
			}

			if (tributo != null && tributo.contemPIS()) {
				valorPIS += tributo.getTipoPis().carregarValoresAliquotas().getValor();
			}

			if (tributo != null && tributo.contemCOFINS()) {
				valorCOFINS += tributo.getTipoCofins().carregarValoresAliquotas().getValor();
			}

			if (tributo != null && tributo.contemImpostoImportacao()) {
				valorImportacao += tributo.getImpostoImportacao().getValor();
			}

			if (tributo != null && tributo.contemISS()) {
				valorBCISS += tributo.getIssqn().getValorBC();
				valorISS += tributo.getIssqn().carregarValoresAliquotas().getValor();
			}

			produto = item.getProdutoServicoNFe();

			valorSeguro += produto.getValorTotalSeguro();
			valorFrete += produto.getValorTotalFrete();
			valorProduto += produto.getValorTotalBruto();
			valorTotalDesconto += produto.getValorDesconto();
			valorDespAcessorias += produto.getOutrasDespesasAcessorias();
		}

		totaisICMS.setValorBaseCalculo(valorBC);
		totaisICMS.setValorBaseCalculoST(valorBCST);
		totaisICMS.setValorTotalICMS(valorICMS);
		totaisICMS.setValorTotalFrete(valorFrete);
		totaisICMS.setValorTotalII(valorImportacao);
		totaisICMS.setValorTotalIPI(valorIPI);
		totaisICMS.setValorTotalSeguro(valorSeguro);
		totaisICMS.setValorTotalPIS(valorPIS);
		totaisICMS.setValorTotalCOFINS(valorCOFINS);
		totaisICMS.setValorTotalProdutosServicos(valorProduto);
		totaisICMS.setValorTotalDesconto(valorTotalDesconto);
		totaisICMS.setValorTotalDespAcessorias(valorDespAcessorias);
		totaisICMS.setValorTotalNF(valorProduto);
		totaisICMS.setValorTotalST(valorST);

		totaisISSQN.setValorBC(valorBCISS);
		totaisISSQN.setValorIss(valorISS);
		totaisISSQN.setValorPis(valorPIS);
		totaisISSQN.setValorCofins(valorCOFINS);

		valoresTotaisNFe.setValoresTotaisICMS(totaisICMS);
		valoresTotaisNFe.setValoresTotaisISSQN(totaisISSQN);

		nFe.getDadosNFe().setValoresTotaisNFe(valoresTotaisNFe);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String emitirNFe(NFe nFe, Integer idPedido) throws BusinessException {
		carregarValoresTotaisNFe(nFe);
		carregarIdentificacaoEmitente(nFe, idPedido);
		carregarDadosLocalRetiradaEntrega(nFe);
		carregarConfiguracao(nFe);

		ValidadorInformacao.validar(nFe);

		final String xml = gerarXMLNfe(nFe, null);
		pedidoNFeDAO.alterar(new PedidoNFe(idPedido, xml));

		return xml;
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

		Double valorDuplicata = valorPedido != null ? valorPedido / totalParcelas : 0;
		List<DuplicataNFe> listaDuplicata = new ArrayList<DuplicataNFe>();
		DuplicataNFe dup = null;
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
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
	public EnderecoNFe gerarEnderecoNFe(Logradouro logradouro, String telefone) {
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
		endereco.setNumero(logradouro.getNumero() == null ? "" : String.valueOf(logradouro.getNumero()));
		endereco.setUF(logradouro.getUf());
		endereco.setNomePais(logradouro.getPais());
		endereco.setTelefone(telefone);

		endereco.setCodigoMunicipio(logradouroService.pesquisarCodigoIBGEByIdCidade(logradouro.getIdCidade()));

		return endereco;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe gerarNFeByIdPedido(Integer idPedido) throws BusinessException {
		String xmlNFe = pedidoNFeDAO.pesquisarXMLNFeByIdPedido(idPedido);
		if (xmlNFe == null || xmlNFe.trim().isEmpty()) {
			return null;
		}

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(NFe.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xmlNFe);
			return (NFe) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new BusinessException("Não foi possível gerar a NFe a partir XML do pedido No. " + idPedido);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String gerarXMLNfe(NFe nFe, Integer idPedido) throws BusinessException {
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
			throw new BusinessException("Falha na geracao do XML da NFe do pedido No. " + idPedido, e);
		}
	}

	@PostConstruct
	public void init() {
		pedidoNFeDAO = new PedidoNFeDAO(entityManager);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Object[]> pesquisarCFOP() {
		return configuracaoSistemaService.pesquisarCFOP();
	}
}
