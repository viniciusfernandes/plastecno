package br.com.plastecno.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import br.com.plastecno.service.dao.NFeItemFracionadoDAO;
import br.com.plastecno.service.dao.NFePedidoDAO;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.NFeItemFracionado;
import br.com.plastecno.service.entity.NFePedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.anotation.TODO;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.nfe.DadosNFe;
import br.com.plastecno.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.ICMSGeral;
import br.com.plastecno.service.nfe.ICMSInterestadual;
import br.com.plastecno.service.nfe.IdentificacaoEmitenteNFe;
import br.com.plastecno.service.nfe.IdentificacaoLocalGeral;
import br.com.plastecno.service.nfe.IdentificacaoNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.ProdutoServicoNFe;
import br.com.plastecno.service.nfe.TransporteNFe;
import br.com.plastecno.service.nfe.TributosProdutoServico;
import br.com.plastecno.service.nfe.ValoresTotaisICMS;
import br.com.plastecno.service.nfe.ValoresTotaisISSQN;
import br.com.plastecno.service.nfe.ValoresTotaisNFe;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class NFeServiceImpl implements NFeService {

	@EJB
	private ClienteService clienteService;

	@EJB
	private ConfiguracaoSistemaService configuracaoSistemaService;

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	private Logger log = Logger.getLogger(this.getClass().getName());

	@EJB
	private LogradouroService logradouroService;

	private NFeItemFracionadoDAO nFeItemFracionadoDAO = null;

	private NFePedidoDAO nFePedidoDAO = null;

	@EJB
	private PedidoService pedidoService;

	@EJB
	private RepresentadaService representadaService;

	@TODO
	private void carregarConfiguracao(NFe nFe) throws BusinessException {
		DadosNFe nf = nFe.getDadosNFe();
		IdentificacaoNFe ide = nf.getIdentificacaoNFe();

		nf.setId("12345678901234567890123456789012345678901234567");
		ide.setChaveAcesso("12345678");
		ide.setDataHoraEmissao(StringUtils.formatarDataHoraTimezone(new Date()));
		ide.setDigitoVerificador("4");
		ide.setTipoAmbiente("2");
		ide.setTipoImpressao("2");
		ide.setProcessoEmissao("0");
		ide.setVersaoProcessoEmissao("43214321");

		if (!ide.contemNumeroSerieModelo()) {
			Integer[] numNFe = gerarNumeroSerieModeloNFe();
			ide.setNumero(String.valueOf(numNFe[0]));
			ide.setSerie(String.valueOf(numNFe[1]));
			ide.setModelo(String.valueOf(numNFe[2]));
		}

		// remover essa linha
		nf.getIdentificacaoDestinatarioNFe().setIndicadorIEDestinatario("1");

		if (nf.getListaDetalhamentoProdutoServicoNFe() == null) {
			return;
		}

		for (DetalhamentoProdutoServicoNFe d : nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe()) {
			d.getProdutoServicoNFe().setIndicadorValorTotal(1);
		}
	}

	private void carregarDadosLocalRetiradaEntrega(NFe nFe) {
		IdentificacaoLocalGeral retirada = nFe.getDadosNFe().getIdentificacaoLocalRetirada();
		IdentificacaoLocalGeral entrega = nFe.getDadosNFe().getIdentificacaoLocalEntrega();

		if (retirada != null && retirada.getCodigoMunicipio() == null) {
			retirada.setCodigoMunicipio(logradouroService.pesquisarCodigoIBGEByCEP(retirada.getCep()));
		}
		if (entrega != null && entrega.getCodigoMunicipio() == null) {
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
		iEmit.setCNAEFiscal(configuracaoSistemaService.pesquisar(ParametroConfiguracaoSistema.CNAE));

		Logradouro logradouro = representadaService.pesquisarLogradorouro(emitente.getId());
		EnderecoNFe endEmit = gerarEnderecoNFe(logradouro, emitente.getTelefone());

		iEmit.setEnderecoEmitenteNFe(endEmit);
		nFe.getDadosNFe().setIdentificacaoEmitenteNFe(iEmit);
		nFe.getDadosNFe().getIdentificacaoNFe().setCodigoUFEmitente(endEmit.getUF());
		nFe.getDadosNFe()
				.getIdentificacaoNFe()
				.setMunicipioOcorrenciaFatorGerador(
						configuracaoSistemaService
								.pesquisar(ParametroConfiguracaoSistema.CODIGO_MUNICIPIO_GERADOR_ICMS));
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
		ICMSInterestadual icmsInter = null;
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
		double valorICMSFundoProbeza = 0;
		double valorICMSInterestadualDest = 0;
		double valorICMSInterestadualRemet = 0;
		double valorICMSDesonerado = 0;
		double valorTotalTributos = 0;

		for (DetalhamentoProdutoServicoNFe item : listaItem) {
			tributo = item.getTributosProdutoServico();
			if (tributo != null) {

				valorTotalTributos += tributo.getValorTotalTributos();
				if (tributo.contemICMS()) {
					tipoIcms = tributo.getTipoIcms();

					valorBC += tipoIcms.getValorBC();
					valorBCST += tipoIcms.getValorBCST() == null ? (Double) 0d : tipoIcms.getValorBCST();
					valorST += tipoIcms.getValorST();
					valorICMSDesonerado += tipoIcms.getValorDesonerado();
					valorICMS += tipoIcms.carregarValores().getValor();
				}

				if (tributo.contemICMSInterestadual()) {
					icmsInter = tributo.getIcmsInterestadual().carregarValores();

					valorICMSFundoProbeza += icmsInter.getValorFCPDestino();
					valorICMSInterestadualDest += icmsInter.getValorUFDestino();
					valorICMSInterestadualRemet += icmsInter.getValorUFRemetente();
				}

				if (tributo.contemIPI()) {
					valorIPI += tributo.getTipoIpi().carregarValores().getValor();
				}

				if (tributo.contemPIS()) {
					valorPIS += tributo.getTipoPis().carregarValores().getValor();
				}

				if (tributo.contemCOFINS()) {
					valorCOFINS += tributo.getTipoCofins().carregarValores().getValor();
				}

				if (tributo.contemISS()) {
					valorBCISS += tributo.getIssqn().getValorBC();
					valorISS += tributo.getIssqn().carregarValores().getValor();
				}

				if (tributo.contemImpostoImportacao()) {
					valorImportacao += tributo.getImpostoImportacao().getValor();
				}
			}

			produto = item.getProdutoServicoNFe();

			valorSeguro += produto.getValorTotalSeguro();
			valorFrete += produto.getValorTotalFrete();
			valorProduto += produto.getValorTotalBruto();
			valorTotalDesconto += produto.getValorDesconto();
			valorDespAcessorias += produto.getOutrasDespesasAcessorias();

		}

		valorProduto += valorFrete;

		totaisICMS.setValorBaseCalculo(valorBC);
		totaisICMS.setValorBaseCalculoST(valorBCST);
		totaisICMS.setValorTotalICMSDesonerado(valorICMSDesonerado);
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
		totaisICMS.setValorTotalICMSFundoPobreza(valorICMSFundoProbeza);
		totaisICMS.setValorTotalICMSInterestadualDestino(valorICMSInterestadualDest);
		totaisICMS.setValorTotalICMSInterestadualRemetente(valorICMSInterestadualRemet);
		totaisICMS.setValorTotalTributos(valorTotalTributos);

		totaisISSQN.setValorBC(valorBCISS);
		totaisISSQN.setValorIss(valorISS);
		totaisISSQN.setValorPis(valorPIS);
		totaisISSQN.setValorCofins(valorCOFINS);

		valoresTotaisNFe.setValoresTotaisICMS(totaisICMS);
		valoresTotaisNFe.setValoresTotaisISSQN(totaisISSQN);

		nFe.getDadosNFe().setValoresTotaisNFe(valoresTotaisNFe);
	}

	private void carregarValoresTransporte(NFe nFe) {
		TransporteNFe t = nFe.getDadosNFe().getTransporteNFe();
		if (t != null && t.getRetencaoICMS() != null) {
			t.getRetencaoICMS().carregarValorRetido();
		}
	}

	private void configurarSubistituicaoTributariaPosValidacao(NFe nFe) {
		List<DetalhamentoProdutoServicoNFe> l = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();
		TributosProdutoServico t = null;
		for (DetalhamentoProdutoServicoNFe d : l) {
			t = d.getTributos();
			if (t.contemCOFINS()) {
				t.getCofins().configurarSubstituicaoTributaria();
			}

			if (t.contemPIS()) {
				t.getPis().configurarSubstituicaoTributaria();
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String emitirNFe(NFe nFe, Integer idPedido, boolean isTriangularizacao) throws BusinessException {
		if (idPedido == null) {
			throw new BusinessException("O número do pedido não pode estar em branco para emitir uma NFe");
		}

		IdentificacaoNFe ide = null;
		if (nFe == null || nFe.getDadosNFe() == null || (ide = nFe.getDadosNFe().getIdentificacaoNFe()) == null) {
			throw new BusinessException("A NFe emitida não pode estar em branco");
		}

		validarEmissaoNFePedido(idPedido);
		validarNumeroNFePedido(idPedido, ide.getNumero() != null ? Integer.parseInt(ide.getNumero()) : null);

		Integer numeroTriangularizado = null;
		if (isTriangularizacao) {
			// Armazenando o numero da nfe que foi triangularizada
			numeroTriangularizado = Integer.parseInt(ide.getNumero());
			// Gerando um novo numero para a nfe triangular
			ide.setNumero(String.valueOf(gerarNumeroSerieModeloNFe()[0]));
		}

		carregarValoresTransporte(nFe);
		carregarValoresTotaisNFe(nFe);
		carregarIdentificacaoEmitente(nFe, idPedido);
		carregarDadosLocalRetiradaEntrega(nFe);
		carregarConfiguracao(nFe);

		ValidadorInformacao.validar(nFe);

		configurarSubistituicaoTributariaPosValidacao(nFe);

		final String xml = gerarXMLNfe(nFe, null);
		nFePedidoDAO.inserirNFePedido(new NFePedido(Integer.parseInt(ide.getNumero()),
				Integer.parseInt(ide.getSerie()), Integer.parseInt(ide.getModelo()), xml, idPedido,
				numeroTriangularizado));

		escreverXMLNFe(xml, new Date(), idPedido.toString() + "_" + ide.getNumero());

		// Inserindo os itens emitidos em cada nota para que possamos efetuar o
		// controle das quantidades fracionadas dos itens emitidos. No caso de
		// triangulacao nao devemos fracionar os itens da nfe
		if (!isTriangularizacao) {
			inserirNFeItemFracionado(nFe, idPedido);
			inserirDataEmissaoNFe(idPedido);
		}
		return ide.getNumero();
	}

	private void escreverXMLNFe(String xml, Date dataEmissao, String nome) throws BusinessException {
		if (xml == null) {
			return;
		}

		String path = configuracaoSistemaService.pesquisar(ParametroConfiguracaoSistema.DIRETORIO_XML_NFE);
		BufferedWriter bw = null;
		try {
			/*
			 * MUITO IMPORTANTE: utilizamos aqui um OutputStreamWriter pois o
			 * FileWriter utiliza o encoding do sistema operacional e nao
			 * permite alteracoes do mesmo.
			 */
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(path + "\\\\" + nome + ".xml")), "UTF-8"));
			bw.write(xml);
		} catch (IOException e) {
			log.log(Level.WARNING, "Falha na escrita do XML da NFe no diretorio do sistema", e);
			throw new BusinessException("Falha na escrita do XML da NFe no diretorio do sistema", e);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					log.log(Level.WARNING, "Falha na escrita do XML da NFe no diretorio do sistema", e);
					throw new BusinessException("Falha no fechamento do XML da NFe gravado no diretorio do sistema", e);
				}
			}
		}
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
		for (Date d : listaData) {
			dup = new DuplicataNFe();
			dup.setDataVencimento(df.format(d));
			// Valor padrao eh boleto pois eh o maior numero de ocorrencias
			dup.setNumero("BOLETO");
			dup.setValor(NumeroUtils.arredondarValorMonetario(valorDuplicata));

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

		endereco.setCodigoMunicipio(logradouro.getCodigoMunicipio() != null ? logradouro.getCodigoMunicipio()
				: logradouroService.pesquisarCodigoIBGEByIdCidade(logradouro.getIdCidade()));

		return endereco;
	}

	private NFe gerarNFe(String xmlNFe) throws BusinessException {
		if (xmlNFe == null || xmlNFe.trim().isEmpty()) {
			return null;
		}

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(NFe.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xmlNFe);
			return (NFe) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new BusinessException("Não foi possível gerar a NFe a partir XML", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe gerarNFeByIdPedido(Integer idPedido) throws BusinessException {
		return gerarNFe(nFePedidoDAO.pesquisarXMLNFeByIdPedido(idPedido));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFe gerarNFeByNumero(Integer numero) throws BusinessException {
		return gerarNFe(nFePedidoDAO.pesquisarXMLNFeByNumero(numero));

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer[] gerarNumeroSerieModeloNFe() throws BusinessException {
		Object[] o = QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery("select p.numero, p.serie, p.modelo, (select max(p2.numeroTriangularizado) from NFePedido p2) from NFePedido p where p.numero = (select max(p1.numero) from NFePedido p1 ) "),
						Object[].class, null);

		if (o == null || o.length < 3 || (o[0] == null && o[1] == null && o[2] == null)) {
			throw new BusinessException(
					"Não foi possível gerar o número, série e modelo da NFe. É necessário inseri-lo manualmente");
		}

		if (o[0] == null) {
			throw new BusinessException(
					"Não foi possível gerar o número sequencial da NFe. É necessário inseri-lo manualmente");
		}

		if (o[1] == null) {
			throw new BusinessException("Não foi possível gerar a série da NFe. É necessário inseri-la manualmente");
		}

		if (o[2] == null) {
			throw new BusinessException("Não foi possível gerar o modelo da NFe. É necessário inseri-lo manualmente");
		}

		// Comparando para verificar qual das NFe tem o maior numero
		if (o[3] != null && (Integer) o[0] < (Integer) o[3]) {
			o[0] = o[3];
		}
		// Gerando o proximo numero da NFe
		return new Integer[] { (Integer) o[0] + 1, (Integer) o[1], (Integer) o[2] };
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String gerarXMLNfe(NFe nFe, Integer idPedido) throws BusinessException {
		try {
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(NFe.class);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.marshal(nFe, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new BusinessException("Falha na geracao do XML da NFe do pedido No. " + idPedido, e);
		}
	}

	@PostConstruct
	public void init() {
		nFePedidoDAO = new NFePedidoDAO(entityManager);
		nFeItemFracionadoDAO = new NFeItemFracionadoDAO(entityManager);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void inserirDataEmissaoNFe(Integer idPedido) {
		Long num = nFeItemFracionadoDAO.pesquisarNumeroItemFracionado(idPedido);
		if (num <= 0) {
			pedidoService.inserirDataEmissaoNFe(idPedido, new Date());
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void inserirNFeItemFracionado(NFe nFe, Integer idPedido) throws BusinessException {
		List<DetalhamentoProdutoServicoNFe> listaDet = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();
		List<Integer[]> listaQtde = pedidoService.pesquisarQuantidadeItemPedidoByIdPedido(idPedido);
		Integer idItem = null;
		Integer idItemFrac = null;
		Integer numItem = 0;
		Integer qtdeItem = 0;
		Integer numeroNFe = Integer.parseInt(nFe.getDadosNFe().getIdentificacaoNFe().getNumero());
		Integer qtdeFrac = 0;
		Integer totalFrac = null;
		ProdutoServicoNFe p = null;

		for (DetalhamentoProdutoServicoNFe d : listaDet) {
			p = d.getProduto();
			idItem = null;
			qtdeItem = 0;
			numItem = 0;
			qtdeFrac = p.getQuantidadeComercial() != null ? p.getQuantidadeComercial().intValue() : 0;
			for (Integer[] qtde : listaQtde) {
				if (d.getNumeroItem() != null && d.getNumeroItem().equals(qtde[2])) {
					idItem = qtde[0];
					qtdeItem = qtde[1];
					numItem = qtde[2];
					idItemFrac = nFeItemFracionadoDAO.pesquisarIdItemFracionado(idItem, numeroNFe);
					break;
				}
			}

			totalFrac = nFeItemFracionadoDAO.pesqusisarSomaQuantidadeFracionada(idItem, numeroNFe);
			totalFrac += qtdeFrac;
			if (totalFrac > qtdeItem) {
				throw new BusinessException(
						"Não é possível fracionar uma quantidade maior do que a quantidade vendida para o item no. "
								+ numItem + " do pedido no." + idPedido + ". Esse item contém apenas " + qtdeItem
								+ " unidades.");
			}
			// Caso ainda existam itens do pedido para serem fracionado iremos
			// inserir o registro no banco, caso contrario, vamos remover todos
			// os registros de um determinado item do banco por questoes de
			// performance, ja que nao eh necessario manter essa informacao no
			// sistema.
			if (totalFrac < qtdeItem) {
				// Aqui estamos configurando o ID do item fracionado para casa
				// tenhamos uma edicao da NFe evitando a duplicacao de registro
				// que poderia surgir no relatorio de itens fracionados.
				nFeItemFracionadoDAO.alterar(new NFeItemFracionado(idItemFrac, idItem, idPedido, p.getDescricao(),
						numItem, numeroNFe, qtdeItem, qtdeFrac, p.getValorTotalBruto()));
			} else if (totalFrac.equals(qtdeItem)) {
				nFeItemFracionadoDAO.removerItemFracionadoNyIdItemPedido(idItem);
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isNFeEmissaoFinalizada(Integer idPedido) {
		return pedidoService.pesquisarDataEmissaoNFe(idPedido) != null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Object[]> pesquisarCFOP() {
		return configuracaoSistemaService.pesquisarCFOP();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Integer pesquisarIdPedidoByNumeroNFe(Integer numeroNFe) {
		return nFePedidoDAO.pesquisarIdPedidoByNumeroNFe(numeroNFe);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeItemFracionado> pesquisarItemFracionado() {
		return nFeItemFracionadoDAO.pesquisarItemFracionado();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> pesquisarNumeroNFeByIdPedido(Integer idPedido) {
		return entityManager
				.createQuery("select p.numero from NFePedido p where p.idPedido = :idPedido order by p.numero asc",
						Integer.class).setParameter("idPedido", idPedido).getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer[]> pesquisarTotalItemFracionado(Integer idPedido) {
		return nFeItemFracionadoDAO.pesquisarTotalFracionado(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerItemFracionadoNFe(Integer idItemFracionado) {
		NFeItemFracionado item = new NFeItemFracionado();
		item.setId(idItemFracionado);
		nFeItemFracionadoDAO.remover(item);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarEmissaoNFePedido(Integer idPedido) throws BusinessException {
		if (idPedido == null) {
			throw new BusinessException("O número do pedido esta em branco, portanto não existe no sistema");
		}

		if (!pedidoService.isPedidoVendaExistente(idPedido)) {
			throw new BusinessException("O pedido de venda No. " + idPedido + " não existe no sistema");
		}
		Integer idRepresentada = pedidoService.pesquisarIdRepresentadaByIdPedido(idPedido);
		if (!representadaService.isRevendedor(idRepresentada)) {
			throw new BusinessException("O pedido de venda No. " + idPedido
					+ " não esta associado a um revendedor cadastrado no sistema");
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarNumeroNFePedido(Integer idPedido, Integer numeroNFe) throws BusinessException {
		if (idPedido == null || numeroNFe == null) {
			return;
		}

		Integer id = QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.idPedido from NFePedido p where :numeroNFe = p.numero ")
						.setParameter("numeroNFe", numeroNFe), Integer.class, null);
		if (id != null && !id.equals(idPedido)) {
			throw new BusinessException("O número " + numeroNFe
					+ " da NFe já exite no sistema e foi emitida para o pedido No. " + id);
		}
	}
}
