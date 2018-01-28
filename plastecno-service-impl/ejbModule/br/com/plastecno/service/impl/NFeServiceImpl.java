package br.com.plastecno.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import br.com.plastecno.service.DuplicataService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.dao.NFeItemFracionadoDAO;
import br.com.plastecno.service.dao.NFePedidoDAO;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroRepresentada;
import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.entity.NFeItemFracionado;
import br.com.plastecno.service.entity.NFePedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.anotation.TODO;
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
import br.com.plastecno.service.nfe.constante.TipoFormaPagamento;
import static br.com.plastecno.service.nfe.constante.TipoNFe.*;
import br.com.plastecno.service.nfe.constante.TipoNFe;
import br.com.plastecno.service.nfe.constante.TipoOperacaoNFe;
import br.com.plastecno.service.nfe.constante.TipoSituacaoDuplicata;
import br.com.plastecno.service.nfe.constante.TipoSituacaoNFe;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.DateUtils;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class NFeServiceImpl implements NFeService {

	@EJB
	private ClienteService clienteService;;

	@EJB
	private ConfiguracaoSistemaService configuracaoSistemaService;

	@EJB
	private DuplicataService duplicataService;

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	@EJB
	private EstoqueService estoqueService;

	private Logger log = Logger.getLogger(this.getClass().getName());

	@EJB
	private LogradouroService logradouroService;

	private NFeItemFracionadoDAO nFeItemFracionadoDAO = null;

	private NFePedidoDAO nFePedidoDAO = null;

	@EJB
	private PedidoService pedidoService;

	@EJB
	private RepresentadaService representadaService;

	private void alterandoQuantidadeFracionada(List<Integer[]> listaQtdDevolvida, Integer numeroNFe)
			throws BusinessException {

		if (listaQtdDevolvida == null || listaQtdDevolvida.isEmpty()) {
			return;
		}

		Integer qDevolvida;
		Integer qFracionada;
		List<Integer> listaNumeroItem = new ArrayList<Integer>();
		for (Integer[] q : listaQtdDevolvida) {
			listaNumeroItem.add(q[0]);
		}
		List<Integer[]> listaFrac = nFeItemFracionadoDAO.pesquisarQuantidadeFracionadaByNumeroItem(listaNumeroItem,
				numeroNFe);
		for (Integer[] qFrac : listaFrac) {
			for (Integer[] q : listaQtdDevolvida) {
				// Aqui estamos verificando se os itens das duas listas sao os
				// mesmos, isto eh, possuem o mesmo numero
				if (!q[0].equals(qFrac[0])) {
					continue;
				}
				qFracionada = qFrac[1];
				qDevolvida = q[1];
				if (qDevolvida > qFracionada) {
					throw new BusinessException("Não é possível devolver a quantidade " + qDevolvida + " do item No. "
							+ q[0] + "da NFe " + numeroNFe + " pois é maior do que a quantidade fracionada de "
							+ qFracionada);
				}
				qFrac[1] = qFracionada - qDevolvida;
			}
		}

		nFeItemFracionadoDAO.alterarQuantidadeFracionadaByNumeroItem(listaFrac, numeroNFe);
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

		// No caso de revenda a representada eh a propria revendedora do produto
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

		LogradouroRepresentada logradouro = representadaService.pesquisarLogradorouro(emitente.getId());
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

	public void carregarNumeroNFe(NFe nFe) throws BusinessException {
		DadosNFe nf = nFe.getDadosNFe();
		IdentificacaoNFe ide = nf.getIdentificacaoNFe();
		if (!ide.contemNumeroSerieModelo()) {
			Integer[] numNFe = gerarNumeroSerieModeloNFe();
			ide.setNumero(String.valueOf(numNFe[0]));
			ide.setSerie(String.valueOf(numNFe[1]));
			ide.setModelo(String.valueOf(numNFe[2]));
		}
	}

	@TODO(descricao = "Esse metodo sera removido quando o sistema passar a emitir NFe")
	private void carregarOutrasConfiguracoes(NFe nFe) throws BusinessException {
		DadosNFe nf = nFe.getDadosNFe();
		IdentificacaoNFe ide = nf.getIdentificacaoNFe();

		// Essas informacoes estao sendo configuradas "hard coded" pois estao
		// anotadas como obrigatorias para a geracao da NFe, mas serao removidas
		// quando o sistema emitr as NFes automaticamente
		nf.setId("12345678901234567890123456789012345678901234567");
		ide.setChaveAcesso("12345678");
		ide.setDataHoraEmissao(StringUtils.formatarDataHoraTimezone(new Date()));
		ide.setDigitoVerificador("4");
		ide.setTipoAmbiente("2");
		ide.setTipoImpressao("2");
		ide.setProcessoEmissao("0");
		ide.setVersaoProcessoEmissao("43214321");

		// remover essa linha
		nf.getIdentificacaoDestinatarioNFe().setIndicadorIEDestinatario("1");

		if (nf.getListaDetalhamentoProdutoServicoNFe() == null) {
			return;
		}

		for (DetalhamentoProdutoServicoNFe d : nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe()) {
			d.getProdutoServicoNFe().setIndicadorValorTotal(1);
		}
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

		for (DetalhamentoProdutoServicoNFe det : listaItem) {
			tributo = det.getTributosProdutoServico();
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

			produto = det.getProdutoServicoNFe();

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

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void carregarValoresTotaisNFePedido(NFePedido nFePedido, NFe nFe) {
		List<DetalhamentoProdutoServicoNFe> lDet = nFe.getDadosNFe().getListaItem();
		if (lDet == null || lDet.isEmpty()) {
			return;
		}
		double vTotal = 0;
		double vTotalICMS = 0;
		double vlBruto;
		double aliq = 0;
		double aliqInter = 0;
		double aliqST = 0;
		for (DetalhamentoProdutoServicoNFe d : lDet) {
			vlBruto = d.getValorBruto();
			aliq = d.getAliquotaICMS();
			aliqInter = d.getAliquotaICMSInterestadual();
			aliqST = d.getAliquotaICMSST();

			vTotal += vlBruto;
			vTotalICMS += vlBruto * (aliq + aliqInter + aliqST);
		}
		nFePedido.setValor(vTotal);
		nFePedido.setValorICMS(vTotalICMS);
	}

	private void carregarValoresTransporte(NFe nFe) {
		TransporteNFe t = nFe.getDadosNFe().getTransporteNFe();
		if (t != null && t.getRetencaoICMS() != null) {
			t.getRetencaoICMS().carregarValorRetido();
		}
	}

	private void configurarNumeroParcela(List<NFeDuplicata> lDuplicata) {
		if (lDuplicata == null) {
			return;
		}
		// Ordenando a lista de duplicadas pois o numero da parcela sera dado
		// pela ordem do elemento na lista.
		Collections.sort(lDuplicata, new Comparator<NFeDuplicata>() {
			@Override
			public int compare(NFeDuplicata o1, NFeDuplicata o2) {
				Date d1 = o1.getDataVencimento();
				Date d2 = o2.getDataVencimento();
				return d1 == null || d2 == null ? -1 : d1.compareTo(d2);
			}
		});
		final int totParc = lDuplicata.size();
		int parc = 0;
		for (NFeDuplicata d : lDuplicata) {
			d.setTotalParcelas(totParc);
			d.setParcela(++parc);
		}
	}

	private void configurarSubstituicaoTributariaPosValidacao(NFe nFe) {
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
	public String emitirNFe(NFe nFe, TipoNFe tipoNFe, Integer idPedido) throws BusinessException {
		String numeroNFe = null;
		if (tipoNFe == null) {
			throw new BusinessException("O tipo da NFe é obrigatório para a emissão.");
		}
		// REFATORAR ESSA IMPLEMENTACAO POIS AQUI EH UM PONTO DE
		// COMPLEXIDADE ACICLOMATIA. UTILIZAR LAMBDA EXPESSIONS.
		if (DEVOLUCAO.equals(tipoNFe)) {
			numeroNFe = emitirNFeDevolucao(nFe, idPedido);
		} else if (ENTRADA.equals(tipoNFe)) {
			numeroNFe = emitirNFeEntrada(nFe, idPedido);
		} else if (SAIDA.equals(tipoNFe)) {
			numeroNFe = emitirNFeSaida(nFe, idPedido);
		} else if (TRIANGULARIZACAO.equals(tipoNFe)) {
			numeroNFe = emitirNFeTriangularizacao(nFe, idPedido);
		}
		return numeroNFe;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String emitirNFeDevolucao(NFe nFe, Integer idPedido) throws BusinessException {
		String numEntrada = null;
		if (nFe == null || nFe.getDadosNFe() == null || nFe.getDadosNFe().getIdentificacaoNFe() == null
				|| (numEntrada = nFe.getDadosNFe().getIdentificacaoNFe().getNumero()) == null) {
			return null;
		}

		validarEmissaoNFePedido(idPedido);

		Integer numeroDevolvido = Integer.parseInt(nFe.getDadosNFe().getIdentificacaoNFe().getNumero());
		nFe.getDadosNFe().getIdentificacaoNFe().setNumero(gerarNumeroSerieModeloNFe()[0].toString());
		String numDevol = gerarNFePedido(nFe, DEVOLUCAO, numeroDevolvido, idPedido);

		List<Integer[]> listaDevolucao = new ArrayList<Integer[]>();
		for (DetalhamentoProdutoServicoNFe d : nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe()) {
			if (d.getProduto().getQuantidadeComercial() == null) {
				continue;
			}
			listaDevolucao.add(new Integer[] { d.getNumeroItem(), d.getProduto().getQuantidadeComercial().intValue() });
		}

		alterandoQuantidadeFracionada(listaDevolucao, Integer.parseInt(numEntrada));
		estoqueService.devolverEstoqueItemPedido(listaDevolucao, idPedido);
		return numDevol;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String emitirNFeEntrada(NFe nFe, Integer idPedido) throws BusinessException {
		return gerarNFeItemFracionado(nFe, ENTRADA, idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String emitirNFeSaida(NFe nFe, Integer idPedido) throws BusinessException {
		String num = gerarNFeItemFracionado(nFe, SAIDA, idPedido);
		geraDuplicataNFeSaida(nFe, idPedido);
		return num;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String emitirNFeTriangularizacao(NFe nFe, Integer idPedido) throws BusinessException {
		IdentificacaoNFe ide = nFe.getDadosNFe().getIdentificacaoNFe();

		// Armazenando o numero da nfe que foi triangularizada
		Integer numeroTriangularizado = ide.getNumero() != null ? Integer.parseInt(ide.getNumero()) : null;

		// Gerando um novo numero para a nfe triangular
		ide.setNumero(String.valueOf(gerarNumeroSerieModeloNFe()[0]));

		String num = gerarNFePedido(nFe, TRIANGULARIZACAO, numeroTriangularizado, idPedido);
		return num;
	}

	private void escreverXMLNFe(String xml, Date dataEmissao, String nome) throws BusinessException {
		if (xml == null) {
			return;
		}

		BufferedWriter bw = null;
		try {
			/*
			 * MUITO IMPORTANTE: utilizamos aqui um OutputStreamWriter pois o
			 * FileWriter utiliza o encoding do sistema operacional e nao
			 * permite alteracoes do mesmo.
			 */
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(nome)), "UTF-8"));
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

	private void geraDuplicataNFeSaida(NFe nFe, Integer idPedido) throws BusinessException {
		// Incluindo as duplicatas apenas das notas fiscais de saida
		DadosNFe dados = nFe.getDadosNFe();
		List<DuplicataNFe> lDuplicata = dados.getListaDuplicata();

		String formaPag = String.valueOf(dados.getIdentificacaoNFe().getIndicadorFormaPagamento());
		boolean isAvista = TipoFormaPagamento.VISTA.getCodigo().equals(formaPag);
		boolean isAPrazo = TipoFormaPagamento.PRAZO.getCodigo().equals(formaPag);

		boolean contemDupl = lDuplicata != null && !lDuplicata.isEmpty();
		if (isAPrazo && !contemDupl) {
			throw new BusinessException("O pagamento da NFe foi efetuado à prazo e deve conter duplicatas");
		}
		if (isAvista && contemDupl) {
			throw new BusinessException("O pagamento da NFe foi efetuado à vista e não pode ter duplicatas");
		}

		if (!contemDupl) {
			return;
		}

		Integer idCliente = pedidoService.pesquisarIdClienteByIdPedido(idPedido);
		if (idCliente == null) {
			throw new BusinessException("O ID do cliente do pedido No. " + idPedido
					+ " não foi encontrado pelo sistema. Verifique se o cliente existe.");
		}

		Integer numeroNFe = Integer.parseInt(dados.getIdentificacaoNFe().getNumero());

		List<NFeDuplicata> lista = new ArrayList<NFeDuplicata>();
		Date dtVenc = null;
		TipoSituacaoDuplicata tpSit = null;
		for (DuplicataNFe dNFe : lDuplicata) {
			try {
				dtVenc = StringUtils.parsearDataAmericano(dNFe.getDataVencimento());
				tpSit = !DateUtils.isAnteriorDataAtual(dtVenc) ? TipoSituacaoDuplicata.A_VENCER
						: TipoSituacaoDuplicata.VENCIDO;
				lista.add(new NFeDuplicata(dtVenc, null, idCliente, dados.getIdentificacaoDestinatarioNFe()
						.getRazaoSocial(), numeroNFe, tpSit, dNFe.getValor()));
			} catch (ParseException e) {
				throw new BusinessException(
						"O campo de data de vendimento de uma das duplicatas não esta no formato correto. O valor enviado foi "
								+ dNFe.getDataVencimento());
			}
		}

		configurarNumeroParcela(lista);
		duplicataService.inserirDuplicata(numeroNFe, lista);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private List<DuplicataNFe> gerarDuplicataByIdPedido(Integer idPedido, boolean isDataAmericana) {
		List<Date> listaData = pedidoService.calcularDataPagamento(idPedido);
		double totalParcelas = listaData.size();
		// EStamos retornando uma lista vazia de duplicatas pois para indicar
		// que o pagamento da NF foi realizado a vista.
		if (totalParcelas <= 0) {
			return new ArrayList<DuplicataNFe>();
		}
		Double valTotPed = pedidoService.pesquisarValorPedidoIPI(idPedido);

		Double valorDuplicata = valTotPed != null ? valTotPed / totalParcelas : 0;
		List<DuplicataNFe> listaDuplicata = new ArrayList<DuplicataNFe>();
		DuplicataNFe dup = null;
		for (Date d : listaData) {
			dup = new DuplicataNFe();
			dup.setDataVencimento(isDataAmericana ? StringUtils.formatarDataAmericana(d) : StringUtils.formatarData(d));

			// Valor padrao eh boleto pois eh o maior numero de ocorrencias
			dup.setNumero("BOLETO");
			dup.setValor(NumeroUtils.arredondarValorMonetario(valorDuplicata));

			listaDuplicata.add(dup);
		}
		return listaDuplicata;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<DuplicataNFe> gerarDuplicataDataAmericanaByIdPedido(Integer idPedido) {
		return gerarDuplicataByIdPedido(idPedido, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<DuplicataNFe> gerarDuplicataDataLatinaByIdPedido(Integer idPedido) {
		return gerarDuplicataByIdPedido(idPedido, false);
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
				: logradouroService.pesquisarCodigoMunicipioByCep(logradouro.getCep()));

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

	private String gerarNFeItemFracionado(NFe nFe, TipoNFe tipoNFe, Integer idPedido) throws BusinessException {
		String num = gerarNFePedido(nFe, tipoNFe, null, idPedido);
		// Inserindo os itens emitidos em cada nota para que possamos efetuar o
		// controle das quantidades fracionadas dos itens emitidos. No caso de
		// triangulacao nao devemos fracionar os itens da nfe
		inserirNFeItemFracionado(nFe, idPedido);
		return num;
	}

	private String gerarNFePedido(NFe nFe, TipoNFe tipoNFe, Integer numeroAssociado, Integer idPedido)
			throws BusinessException {
		if (idPedido == null) {
			throw new BusinessException("O número do pedido não pode estar em branco para emitir uma NFe");
		}

		if (tipoNFe == null) {
			throw new BusinessException("O tipo de NFe a ser emitida não pode estar em branco.");
		}

		IdentificacaoNFe ide = null;
		if (nFe == null || nFe.getDadosNFe() == null || (ide = nFe.getDadosNFe().getIdentificacaoNFe()) == null) {
			throw new BusinessException("A NFe emitida não pode estar em branco");
		}

		carregarNumeroNFe(nFe);
		carregarValoresTransporte(nFe);
		carregarValoresTotaisNFe(nFe);
		carregarIdentificacaoEmitente(nFe, idPedido);
		carregarDadosLocalRetiradaEntrega(nFe);
		carregarOutrasConfiguracoes(nFe);

		validarFormaPagamento(nFe);
		validarTipoOperacao(nFe, idPedido);
		validarEmissaoNFePedido(idPedido);
		validarNumeroNFePedido(idPedido, ide.getNumero() != null ? Integer.parseInt(ide.getNumero()) : null);

		ValidadorInformacao.validar(nFe);

		configurarSubstituicaoTributariaPosValidacao(nFe);

		final String xml = gerarXMLNfe(nFe, null);
		// Devemos inserir o registro no banco de dados antes de gravar o
		// arquivo no diretorio pois nao podemos ter um xml sem um registro na
		// base de dados
		NFePedido nFePedido = new NFePedido(new Date(), idPedido, Integer.parseInt(ide.getModelo()), nFe.getDadosNFe()
				.getIdentificacaoDestinatarioNFe().getRazaoSocial(), Integer.parseInt(ide.getNumero()),
				numeroAssociado, Integer.parseInt(ide.getSerie()), tipoNFe, TipoSituacaoNFe.EMITIDA, xml);

		// Devemos carregar os valores totais da NF pois sera utilizado no
		// relatorio de faturamento
		carregarValoresTotaisNFePedido(nFePedido, nFe);

		ValidadorInformacao.validar(nFePedido);
		nFePedidoDAO.inserirNFePedido(nFePedido);

		escreverXMLNFe(xml, new Date(), gerarNomeXMLNFe(String.valueOf(idPedido), ide.getNumero()));

		return ide.getNumero();
	}

	private String gerarNomeXMLNFe(String idPedido, String numeroNFe) {
		String path = configuracaoSistemaService.pesquisar(ParametroConfiguracaoSistema.DIRETORIO_XML_NFE);
		return path + "\\\\" + idPedido + "_" + numeroNFe + ".xml";
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer[] gerarNumeroSerieModeloNFe() throws BusinessException {
		Object[] o = nFePedidoDAO.pesquisarNumeroSerieModeloNFe();

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
	private void inserirNFeItemFracionado(NFe nFe, Integer idPedido) throws BusinessException {
		List<DetalhamentoProdutoServicoNFe> listaDet = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();
		List<Integer[]> listaItem = pedidoService.pesquisarQuantidadeItemPedidoByIdPedido(idPedido);
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
			for (Integer[] qtde : listaItem) {
				if (d.getNumeroItem() != null && d.getNumeroItem().equals(qtde[2])) {
					idItem = qtde[0];
					qtdeItem = qtde[1];
					numItem = qtde[2];
					idItemFrac = nFeItemFracionadoDAO.pesquisarIdItemFracionado(idItem, numeroNFe);
					break;
				}
			}

			totalFrac = pesqusisarQuantidadeTotalFracionadoByIdItemPedidoNFeExcluida(idItem, numeroNFe);
			if (totalFrac + qtdeFrac > qtdeItem) {
				throw new BusinessException(
						"Não é possível fracionar uma quantidade maior do que a quantidade vendida para o item no. "
								+ numItem + " do pedido no." + idPedido + ". Enviar no máximo "
								+ (qtdeItem - totalFrac) + " unidades.");
			}
			// Aqui estamos configurando o ID do item fracionado para casa
			// tenhamos uma edicao da NFe evitando a duplicacao de registro
			// que poderia surgir no relatorio de itens fracionados.
			nFeItemFracionadoDAO.alterar(new NFeItemFracionado(idItemFrac, idItem, idPedido, p.getDescricao(), numItem,
					numeroNFe, qtdeItem, qtdeFrac, p.getValorTotalBruto()));
		}
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

	@TODO(descricao = "ESSE METODO DEVE SER MODIFICADO PARA EFETUAR BUSCA POR PAGINACAO URGENTEMENTE")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeItemFracionado> pesquisarItemFracionado() {
		return nFeItemFracionadoDAO.pesquisarItemFracionado();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeItemFracionado> pesquisarItemFracionadoByNumeroNFe(Integer numeroNFe) {
		return nFeItemFracionadoDAO.pesquisarItemFracionadoByNumeroNFe(numeroNFe);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeItemFracionado> pesquisarNFeItemFracionadoQuantidades(Integer numeroNFe) {
		return nFeItemFracionadoDAO.pesquisarNFeItemFracionadoQuantidades(numeroNFe);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFePedido> pesquisarNFePedidoEntradaEmitidaByPeriodo(Periodo periodo) {
		return nFePedidoDAO.pesquisarNFePedidoByPeriodo(periodo.getInicio(), periodo.getFim(), ENTRADA,
				TipoSituacaoNFe.EMITIDA);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFePedido> pesquisarNFePedidoSaidaEmitidaByPeriodo(Periodo periodo) {
		return nFePedidoDAO.pesquisarNFePedidoByPeriodo(periodo.getInicio(), periodo.getFim(), SAIDA,
				TipoSituacaoNFe.EMITIDA);
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
	public List<ItemPedido> pesquisarQuantitadeItemRestanteByIdPedido(Integer idPedido) {
		List<ItemPedido> lRestante = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		if (lRestante.isEmpty()) {
			return lRestante;
		}

		// Pesquisando a quantidade total ja fracionada de cada item
		List<Integer[]> lTotal = pesquisarTotalItemFracionado(idPedido);

		// Aqui estamos criando uma nova lista pois o Java nao permite remover
		// um item da lista que esta sendo iterada.
		List<ItemPedido> lItem = new ArrayList<ItemPedido>(lRestante);
		Integer qRestante = null;
		for (ItemPedido i : lItem) {
			for (Integer[] val : lTotal) {
				if (i.getSequencial().equals(val[0])) {
					qRestante = i.getQuantidade() - val[1];
					// Aqui estamos removendo da lista todos os itens que ja
					// foram totalmente fracionados para que eles nao aparecam
					// para o usuario com quantidade zerada.
					if (qRestante == null || qRestante == 0) {
						lRestante.remove(i);
						break;
					}
					i.setQuantidade(qRestante);
					break;
				}
			}
		}
		return lRestante;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer[]> pesquisarTotalItemFracionado(Integer idPedido) {
		return nFeItemFracionadoDAO.pesquisarQuantidadeTotalItemFracionado(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesquisarTotalItemFracionadoByNumeroItemNumeroNFe(Integer numeroItem, Integer numeroNFe) {
		return nFeItemFracionadoDAO.pesquisarTotalItemFracionadoByNumeroItemNumeroNFe(numeroItem, numeroNFe);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer[]> pesquisarTotalItemFracionadoByNumeroNFe(Integer numeroNFe) {
		return nFeItemFracionadoDAO.pesquisarQuantidadeTotalItemFracionadoByNumeroNFe(numeroNFe);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesqusisarQuantidadeTotalFracionadoByIdItemPedidoNFeExcluida(Integer idItem,
			Integer numeroNFeExcluido) {
		return nFeItemFracionadoDAO.pesqusisarQuantidadeTotalFracionadoByIdItemPedidoNFeExcluida(idItem,
				numeroNFeExcluido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerItemFracionadoNFe(Integer idItemFracionado) {
		NFeItemFracionado item = new NFeItemFracionado();
		item.setId(idItemFracionado);
		nFeItemFracionadoDAO.remover(item);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerNFe(Integer numeroNFe) {
		if (numeroNFe == null) {
			return;
		}

		Integer idPedido = nFePedidoDAO.pesquisarIdPedidoByNumeroNFe(numeroNFe);
		if (idPedido == null) {
			return;
		}

		nFeItemFracionadoDAO.removerItemFracionadoByNumeroNFe(numeroNFe);
		duplicataService.removerDuplicataByNumeroNFe(numeroNFe);
		nFePedidoDAO.removerNFePedido(numeroNFe);
		removerXMLNFe(String.valueOf(idPedido), String.valueOf(numeroNFe));
	}

	private void removerXMLNFe(String idPedido, String numeroNFe) {
		File xml = new File(gerarNomeXMLNFe(idPedido, numeroNFe));
		if (xml.isFile()) {
			xml.delete();
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarEmissaoNFePedido(Integer idPedido) throws BusinessException {
		if (idPedido == null) {
			throw new BusinessException("O número do pedido esta em branco, portanto não existe no sistema");
		}

		if (!pedidoService.isPedidoExistente(idPedido)) {
			throw new BusinessException("O pedido No. " + idPedido + " não existe no sistema");
		}

		SituacaoPedido sPed = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		if (SituacaoPedido.CANCELADO.equals(sPed) || SituacaoPedido.DIGITACAO.equals(sPed)
				|| SituacaoPedido.ORCAMENTO.equals(sPed) || SituacaoPedido.ORCAMENTO_DIGITACAO.equals(sPed)) {
			throw new BusinessException("Não é possível emitir NFe para o pedido No. " + idPedido + " pois esta em "
					+ sPed.getDescricao());
		}

		Integer idRepresentada = pedidoService.pesquisarIdRepresentadaByIdPedido(idPedido);
		if (SituacaoPedido.isVendaEfetivada(sPed) && !representadaService.isRevendedor(idRepresentada)) {
			throw new BusinessException("O pedido de venda No. " + idPedido
					+ " não esta associado a um revendedor cadastrado no sistema");
		} else if (!SituacaoPedido.isVendaEfetivada(sPed) && !SituacaoPedido.isCompraEfetivada(sPed)) {
			throw new BusinessException("O pedido No. " + idPedido
					+ " não é uma compra e nem uma venda efetivada. Verifique no sistema a situação desse pedido.");
		}
	}

	private void validarFormaPagamento(NFe nfe) throws BusinessException {
		DadosNFe d = nfe.getDadosNFe();
		boolean isAvista = TipoFormaPagamento.VISTA.getCodigo().equals(
				d.getIdentificacaoNFe().getIndicadorFormaPagamento());
		boolean isAprazo = TipoFormaPagamento.PRAZO.getCodigo().equals(
				d.getIdentificacaoNFe().getIndicadorFormaPagamento());

		boolean contemDupl = d.getListaDuplicata() != null && !d.getListaDuplicata().isEmpty();
		if (isAvista && contemDupl) {
			throw new BusinessException("A forma de pagamento A VISTA não pode gerar duplicatas.");
		}

		if (isAprazo && !contemDupl) {
			throw new BusinessException("A forma de pagamento A PRAZO não contém duplicatas.");
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarNumeroNFePedido(Integer idPedido, Integer numeroNFe) throws BusinessException {
		if (idPedido == null || numeroNFe == null) {
			return;
		}

		Integer id = pesquisarIdPedidoByNumeroNFe(numeroNFe);
		if (id != null && !id.equals(idPedido)) {
			throw new BusinessException("O número " + numeroNFe
					+ " da NFe já exite no sistema e foi emitida para o pedido No. " + id);
		}
	}

	private void validarTipoOperacao(NFe nFe, Integer idPedido) throws BusinessException {
		TipoPedido tp = pedidoService.pesquisarTipoPedidoByIdPedido(idPedido);
		String codOper = nFe.getDadosNFe().getIdentificacaoNFe().getTipoOperacao();
		TipoOperacaoNFe tpOper = TipoOperacaoNFe.getTipo(codOper);

		if (TipoPedido.isCompra(tp) && !TipoOperacaoNFe.ENTRADA.equals(tpOper)) {
			throw new BusinessException("O pedido de No. " + idPedido
					+ " é um pedido de compra. Seu tipo de operação deve ser de ENTRADA");
		} else if (TipoPedido.isVenda(tp) && !TipoOperacaoNFe.SAIDA.equals(tpOper)) {
			throw new BusinessException("O pedido de No. " + idPedido
					+ " é um pedido de venda. Seu tipo de operação deve ser de SAIDA");
		}
	}
}
