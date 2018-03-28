package br.com.svr.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.svr.service.EstoqueService;
import br.com.svr.service.PagamentoService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.constante.TipoPagamento;
import br.com.svr.service.dao.PagamentoDAO;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.Pagamento;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.validacao.ValidadorInformacao;
import br.com.svr.service.wrapper.GrupoWrapper;
import br.com.svr.service.wrapper.Periodo;
import br.com.svr.service.wrapper.RelatorioWrapper;
import br.com.svr.util.NumeroUtils;
import br.com.svr.util.StringUtils;

@Stateless
public class PagamentoServiceImpl implements PagamentoService {

	@PersistenceContext(name = "svr")
	private EntityManager entityManager;

	@EJB
	private EstoqueService estoqueService;

	private PagamentoDAO pagamentoDAO;

	@EJB
	private PedidoService pedidoService;

	private Integer calcularTotalParcelas(Integer idPedido) {
		int tot = pedidoService.calcularDataPagamento(idPedido).size();
		// Estamos retornando 1 indicando o pagamento a vista.
		return tot == 0 ? 1 : tot;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pagamento gerarPagamentoItemCompra(Integer idItemPedido) {
		if (idItemPedido == null) {
			return null;
		}
		ItemPedido i = pedidoService.pesquisarItemPedidoPagamento(idItemPedido);
		double valFrete = pedidoService.calcularValorFretePorItemByIdItem(idItemPedido);

		if (i == null) {
			return null;
		}
		Pagamento p = new Pagamento();
		p.setDataRecebimento(new Date());
		p.setDescricao(i.getDescricaoSemFormatacao());
		p.setIdFornecedor(i.getIdRepresentada());
		p.setIdItemPedido(idItemPedido);
		p.setIdPedido(i.getIdPedido());
		p.setNomeFornecedor(i.getNomeRepresentada());
		// Configurando o pagamento gerado como sendo sempre a primeira parcela,
		// pois os pagamentos das outras parcelas serao geradas em outro
		// servico.
		p.setParcela(1);
		p.setQuantidadeItem(i.getQuantidadeRestanteRecepcionar());
		p.setSequencialItem(i.getSequencial());
		p.setTipoPagamento(TipoPagamento.INSUMO);
		p.setTotalParcelas(calcularTotalParcelas(i.getIdPedido()));
		// Nao devemos usar o valor total pois na compra o IPI nao eh destacado.
		p.setValor(i.calcularPrecoTotalIPI() + valFrete);
		p.setValorCreditoICMS(i.calcularValorICMS());
		return p;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<String, Pagamento> gerarRelatorioPagamento(List<Pagamento> lPagamento, Periodo periodo) {

		RelatorioWrapper<String, Pagamento> relatorio = new RelatorioWrapper<String, Pagamento>("Pagamentos de "
				+ StringUtils.formatarData(periodo.getInicio()) + " a " + StringUtils.formatarData(periodo.getFim()));

		if (lPagamento == null || lPagamento.isEmpty()) {
			return relatorio;
		}

		GrupoWrapper<String, Pagamento> gr = null;
		Map<Integer, Double> nfPag = new HashMap<>();

		double tot = 0d;
		double totCredICMS = 0d;
		Double val = 0d;
		Boolean liqud = false;
		final String VL_PARCELA = "valorParcela";
		final String VL_PARCELA_NF = "valorParcelaNF";
		final String GRUPO_LIQUIDADO = "liquidado";
		final String INSUMO_GRUPO = "insumo";
		for (Pagamento p : lPagamento) {
			tot += p.getValor() != null ? p.getValor() : 0d;
			totCredICMS += p.getValorCreditoICMS() != null ? p.getValorCreditoICMS() : 0d;

			// Agrupando os pagamentos de compra pelo numero da NF.
			if (p.isInsumo()) {
				// Aqui estamos concatenando u numero da NF com o ID do
				// fornecedor para criar o ID pois diferentes fornecedores podem
				// ter o mesmo numreo de NF, assim minimizamos conflitos.
				gr = relatorio.addGrupo(String.valueOf(p.getNumeroNF()) + p.getIdFornecedor() + p.getParcela(), p);
				gr.setPropriedade("numeroNF", p.getNumeroNF());
				liqud = (Boolean) gr.getPropriedade(GRUPO_LIQUIDADO);

				// Bloco de codigo para totalizar os valores dos itens da NF
				if ((val = nfPag.get(p.getNumeroNF())) == null) {
					val = 0d;
				}
				val += p.getValor() != null ? p.getValor() : 0d;
				// O valor da NF do relatorio deve ser a soma de todos valores
				// dos itens.
				nfPag.put(p.getNumeroNF(), val);

				if (liqud == null) {
					liqud = p.isLiquidado();
				} else {
					// Sera liquidado quando todos os itens forem liquidados.
					liqud &= p.isLiquidado();
				}
			} else {
				// Todos os outros tipos de pagamentos nao serao agrupados.
				// Usamos o ID do pagamento pois a estrategia eh tratar os
				// pagamentos que nao tem NF com um grupo com um unico elemento.
				gr = relatorio.addGrupo(p.getId().toString(), p);
				gr.setPropriedade(VL_PARCELA, p.getValor());
			}
			gr.setPropriedade(GRUPO_LIQUIDADO, liqud);
			gr.setPropriedade(INSUMO_GRUPO, p.isInsumo());
			gr.setPropriedade("dataVencimento", StringUtils.formatarData(p.getDataVencimento()));
			gr.setPropriedade("liquidado", p.isLiquidado());
			gr.setPropriedade("vencido", !p.isLiquidado() && p.isVencido());

			// Essa propriedade eh apenas para o ordenamento cronologico dos
			// grupos.
			gr.setPropriedade("dtVenc", p.getDataVencimento());
		}

		Map<Integer, Integer> totParcNf = new HashMap<>();
		Integer parc = 0;
		Integer nf = null;
		// Determinando a quantidade de parcelas de uma determinada NF
		for (GrupoWrapper<String, Pagamento> g : relatorio.getListaGrupo()) {
			nf = (Integer) g.getPropriedade("numeroNF");
			if (nf != null && (parc = totParcNf.get(nf)) != null) {
				totParcNf.put(nf, parc + 1);
			} else if (nf != null && parc == null) {
				totParcNf.put(nf, 1);
			}
		}

		Integer totParc = null;
		for (GrupoWrapper<String, Pagamento> g : relatorio.getListaGrupo()) {
			nf = (Integer) g.getPropriedade("numeroNF");
			if (nf == null) {
				continue;
			}
			totParc = totParcNf.get(nf);
			val = nfPag.get(nf) / totParc;
			g.setPropriedade(VL_PARCELA, val);
			
			// Aqui estamos pegando o primeiro item da lista pois o valor da NF
			// eh igual para todos os itens da nota.
			val = g.getListaElemento().get(0).getValorNF();
			if (val == null) {
				val = 0d;
			}
			g.setPropriedade(VL_PARCELA_NF, val / totParc);
		}

		relatorio.addPropriedade("qtde", lPagamento.size());
		relatorio.addPropriedade("tot", NumeroUtils.formatarValor2Decimais(tot));
		relatorio.addPropriedade("totCredICMS", NumeroUtils.formatarValor2Decimais(totCredICMS));

		// Arredondando o valor total das NFs no fim para evitar problemas de
		// arredondamentos
		List<GrupoWrapper<String, Pagamento>> lGRupo = relatorio.getListaGrupo();
		for (GrupoWrapper<String, Pagamento> g : lGRupo) {
			g.setPropriedade(VL_PARCELA, NumeroUtils.arredondarValor2Decimais((Double) g.getPropriedade(VL_PARCELA)));
			g.setPropriedade(VL_PARCELA_NF,
					NumeroUtils.arredondarValor2Decimais((Double) g.getPropriedade(VL_PARCELA_NF)));
		}

		Collections.sort(lGRupo, new Comparator<GrupoWrapper<String, Pagamento>>() {

			@Override
			public int compare(GrupoWrapper<String, Pagamento> o1, GrupoWrapper<String, Pagamento> o2) {
				Date dtVenc1 = (Date) o1.getPropriedade("dtVenc");
				Date dtVenc2 = (Date) o2.getPropriedade("dtVenc");
				return dtVenc1 != null && dtVenc2 != null ? dtVenc1.compareTo(dtVenc2) : -1;
			}
		});

		// Ordenando os elementos do grupo
		for (GrupoWrapper<String, Pagamento> g : lGRupo) {
			Collections.sort(g.getListaElemento(), new Comparator<Pagamento>() {
				@Override
				public int compare(Pagamento p1, Pagamento p2) {
					if (p1.getIdPedido() == null || p2.getIdPedido() == null) {
						return -1;
					}
					int h1 = p1.getIdPedido();
					int h2 = p2.getIdPedido();
					// Se o idPedido foi maior entao vai para o inicio da lista
					if (h1 > h2) {
						return 1;
					}
					// Se o idPedido foi menor entao vai para o fim da lista
					if (h1 < h2) {
						return -1;
					}

					// Aqui estao os casos em que od ids dos pedidos sao iguais.
					// Nesse caso estamos subtraindo os sequenciais do item do
					// id do pedido para colocar os maiores sequenciais no fim
					// da lista
					h1 -= p1.getSequencialItem() == null ? 0 : p1.getSequencialItem();
					h2 -= p2.getSequencialItem() == null ? 0 : p2.getSequencialItem();
					if (h1 > h2) {
						return -1;
					}
					if (h1 < h2) {
						return 1;
					}

					return 0;
				}
			});
		}

		return relatorio;
	}

	@PostConstruct
	public void init() {
		pagamentoDAO = new PagamentoDAO(entityManager);
	}

	private Integer inserir(Pagamento pagamento) throws BusinessException {
		if (pagamento == null) {
			throw new BusinessException("O pagamento não pode ser nulo para a inclusão.");
		}

		// Aqui estamos permitindo que o usuario cadastre os pagamentos de item
		// a item do pedido na tela de pagamentos.
		if (pagamento.isInsumo()
				&& (pagamento.getNumeroNF() == null || pagamento.getIdPedido() == null
						|| pagamento.getSequencialItem() == null || pagamento.getValorNF() == null)) {
			throw new BusinessException(
					"O pagamento de insumos deve conter o número da NF, o número do pedido e o número do item do pedido e valor da NF.");
		}

		if (pagamento.isInsumo()) {
			Integer idItem = pedidoService.pesquisarIdItemPedidoByIdPedidoSequencial(pagamento.getIdPedido(),
					pagamento.getSequencialItem());
			if (idItem == null) {
				throw new BusinessException("O pagamento não pode ser cadastrado pois não existe item No \""
						+ pagamento.getSequencialItem() + "\" do pedido No \"" + pagamento.getIdPedido() + "\"");
			}
			pagamento.setIdItemPedido(idItem);
		}

		ValidadorInformacao.validar(pagamento);
		if (pagamento.getParcela() != null && pagamento.getTotalParcelas() != null
				&& pagamento.getParcela() > pagamento.getTotalParcelas()) {
			throw new BusinessException("A parcela não pode ser maior do que o total de parcelas.");
		}

		if (pagamento.getParcela() == null && pagamento.getTotalParcelas() != null) {
			throw new BusinessException("A parcela deve ser preenchida.");
		}

		if (pagamento.getParcela() != null && pagamento.getTotalParcelas() == null) {
			throw new BusinessException("O total de parcelas devem ser preenchidos.");
		}

		if (pagamento.isInsumo()) {
			// Esse metodo eh necessario pois qualquer alteracao do valor da NF
			// deve ser refletido em todas as parcelas e mante-lo igual para
			// todos os itens eh necessario pois o relatorio supoe que o valor
			// da NF eh igual para todos os itens.
			pagamentoDAO.alterarValorNFPagamentoInsumo(pagamento.getNumeroNF(), pagamento.getIdFornecedor(),
					pagamento.getValorNF());
		}

		return pagamentoDAO.alterar(pagamento).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirPagamento(Pagamento pagamento) throws BusinessException {
		if (pagamento == null) {
			return null;
		}
		// Devemos dar baixa nos itens do pedido que estao aguardando a recepcao
		// para que eles sejam removidos do relatorio de compras aguardando
		// recepcao.
		if (pagamento.isInsumo()) {
			Integer idItem = pagamento.getIdItemPedido();
			int qtdeNova = pagamento.getQuantidadeItem() == null ? 0 : pagamento.getQuantidadeItem();
			int qtdeItem = pedidoService.pesquisarQuantidadeItemPedido(idItem);
			if (qtdeNova > qtdeItem) {
				throw new BusinessException("A quantidade máxima para o item No. " + pagamento.getSequencialItem()
						+ " do pedido No. " + pagamento.getIdPedido() + " é " + qtdeItem);
			}
			if (!pagamento.isNovo()) {
				int qtdePag = pagamentoDAO.pesquisarQuantidadeById(pagamento.getId());
				int qtdeRecep = 0;
				if (qtdeNova != qtdePag) {
					int qtdeTot = pesquisarQuantidadeTotalPagaByIdItem(idItem);
					qtdeRecep = qtdeTot + (qtdeNova - qtdePag);
					if (qtdeRecep > qtdeItem) {
						throw new BusinessException("A quantidade máxima do item No. " + pagamento.getSequencialItem()
								+ " do pedido No. " + pagamento.getIdPedido() + " é " + (qtdeItem - qtdeTot));
					}
					// Devolvendo os itens para a fila de pedidos de compras
					// para recepcao e deve se levar em consideracao todos
					// os outros pagamentos desse mesmo item.
					pedidoService.alterarQuantidadeRecepcionada(idItem, qtdeRecep);

					// Mantendo a corencia dos valores pagos pois todas as
					// parcelas de um mesmo item devem ter o mesmo preco.
					pagamentoDAO
							.alterarQuantidadeItemPagamentoByIdItemPedido(pagamento.getNumeroNF(), idItem, qtdeNova);
				}
				// Essa condicao eh para subtrair das quantidades do estoque
				// pois a reducao de quantidade do pagamaento indica devolucao
				// do item para a fila de recepcao de compras. No caso em que a
				// quantidade seja zero, nenhuma acao sera tomada.
				if ((qtdeItem = qtdePag - qtdeNova) > 0) {
					estoqueService.removerEstoqueItemCompra(idItem, qtdeItem);
				} else if (qtdeItem < 0) {
					// Essa condicao indica que o usuario esta acrescentando os
					// itens do pagamento, entao essa nova quantidade deve ser
					// recepcionada como uma compra nova.
					qtdeItem = qtdeNova - qtdePag;
					estoqueService.recalcularEstoqueItemCompra(idItem, qtdeItem);
				}
			} else {
				estoqueService.adicionarQuantidadeRecepcionadaItemCompra(idItem, qtdeNova, null);
			}
		}
		// Eh importante inserir o pagamento apenas apos a ercepcao de
		// compras
		// pois la vamos ate o banco recuperar as informacoes das
		// quantidades do
		// que ja foi pago.
		return inserir(pagamento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirPagamentoParceladoItemCompra(Integer numeroNF, Double valorNF, Date dataVencimento,
			Date dataEmissao, Integer modalidadeFrete, List<Integer> listaIdItem) throws BusinessException {

		if (listaIdItem == null || listaIdItem.isEmpty()) {
			throw new BusinessException(
					"A lista dos IDs dos item deve ser preenchida para gerar os pagamentos do pedido.");
		}

		if (pedidoService.contemFornecedorDistintoByIdItem(listaIdItem)) {
			throw new BusinessException(
					"Os itens a serem pagos não são do mesmo fornecedor. Verifique os pedidos enviados.");
		}

		// Aqui estamos validando se os itens enviados ja possuem os pagamentos
		// de todas as suas quantidades do pedido.
		// validarPagamentoTotalizadoByIdItem(listaIdItem);

		Pagamento p = null;
		for (Integer idItem : listaIdItem) {
			p = gerarPagamentoItemCompra(idItem);
			p.setNumeroNF(numeroNF);
			p.setValorNF(valorNF);
			p.setModalidadeFrete(modalidadeFrete);
			p.setDataEmissao(dataEmissao);
			p.setDataVencimento(dataVencimento);

			inserirPagamentoParceladoItemCompra(p);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirPagamentoParceladoItemCompra(Pagamento pagamento) throws BusinessException {
		if (pagamento == null) {
			return;
		}

		if (pagamento.getNumeroNF() == null) {
			throw new BusinessException(
					"O número da NF deve ser preenchido para a inclusão do pagamento do item do pedido.");
		}

		Integer idPedido = pagamento.getIdPedido();
		if (idPedido == null) {
			throw new BusinessException(
					"O número do pedido deve ser preenchido para a inclusão do pagamento do item do pedido.");
		}
		if (pagamento.getIdItemPedido() == null) {
			throw new BusinessException(
					"O número do item do pedido deve ser preenchido para a inclusão do pagamento do item do pedido.");
		}
		if (pagamento.getDataRecebimento() == null) {
			throw new BusinessException(
					"A data de recebimento deve ser preenchida para a inclusão do pagamento do item do pedido.");
		}

		if (pagamento.getDataEmissao() == null) {
			throw new BusinessException(
					"A data de emissão deve ser preenchida para a inclusão do pagamento do item do pedido.");
		}
		List<Date> listaData = pedidoService.calcularDataPagamento(idPedido, pagamento.getDataEmissao());
		// No caso de pagamento a vista a unica data que teremos eh a de
		// vencimento.
		if (listaData.isEmpty()) {
			listaData.add(pagamento.getDataEmissao());
		}

		// Aqui identificamos um pagamento a vista como um total de uma unica
		// parcela
		int totParc = listaData.size() <= 0 ? 1 : listaData.size();
		Pagamento clone = null;
		for (int i = 0; i < totParc; i++) {
			clone = pagamento.clone();
			clone.setDataVencimento(listaData.get(i));
			clone.setParcela(i + 1);
			clone.setTotalParcelas(totParc);
			clone.setValor(pagamento.getValor() / totParc);
			clone.setValorCreditoICMS(pagamento.getValorCreditoICMS() / totParc);
			inserir(clone);
		}
		// Devemos dar baixa nos itens do pedido que estao aguardando a recepcao
		// para que eles sejam removidos do relatorio de compras aguardando
		// recepcao.
		if (pagamento.isInsumo()) {
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(pagamento.getIdItemPedido(),
					pagamento.getQuantidadeItem(), null);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void liquidarPagamento(Integer idPagamento, boolean liquidado) {
		if (idPagamento == null) {
			return;
		}
		pagamentoDAO.liquidarPagamento(idPagamento, liquidado);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void liquidarPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela, boolean liquidado) {
		pagamentoDAO.liquidarPagamentoNFParcelada(numeroNF, idFornecedor, parcela, liquidado);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pagamento pesquisarById(Integer idPagamento) {
		return pagamentoDAO.pesquisarById(idPagamento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pagamento> pesquisarByIdPedido(Integer idPedido) {
		return pagamentoDAO.pesquisarByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesquisarIdItemPedidoByIdPagamento(Integer idPagamento) {
		return pagamentoDAO.pesquisarIdItemPedidoByIdPagamento(idPagamento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pagamento> pesquisarPagamentoByIdFornecedor(Integer idFornecedor, Periodo periodo) {
		if (idFornecedor == null || periodo == null) {
			return new ArrayList<Pagamento>();
		}
		return pagamentoDAO.pesquisarPagamentoByIdFornecedor(idFornecedor, periodo.getInicio(), periodo.getFim());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pagamento> pesquisarPagamentoByIdPedido(Integer idPedido) {
		if (idPedido == null) {
			return new ArrayList<Pagamento>();
		}
		return pagamentoDAO.pesquisarPagamentoByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pagamento> pesquisarPagamentoByNF(Integer numeroNF) {
		if (numeroNF == null) {
			return new ArrayList<Pagamento>();
		}
		return pagamentoDAO.pesquisarPagamentoByNF(numeroNF);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pagamento> pesquisarPagamentoByPeriodo(Periodo periodo) {
		return pagamentoDAO.pesquisarPagamentoByPeriodo(periodo.getInicio(), periodo.getFim(), null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pagamento> pesquisarPagamentoByPeriodo(Periodo periodo, boolean apenasInsumos) {
		if (apenasInsumos) {
			List<TipoPagamento> lTipo = new ArrayList<>();
			lTipo.add(TipoPagamento.INSUMO);
			return pagamentoDAO.pesquisarPagamentoByPeriodo(periodo.getInicio(), periodo.getFim(), lTipo);
		}
		return pagamentoDAO.pesquisarPagamentoByPeriodo(periodo.getInicio(), periodo.getFim(), null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int pesquisarQuantidadeTotalPagaByIdItem(Integer idItemPedido) {
		List<Integer> l = pagamentoDAO.pesquisarQuantidadePagaByIdItem(idItemPedido);
		int tot = 0;
		for (Integer q : l) {
			tot += q;
		}
		return tot;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remover(Integer idPagamento) throws BusinessException {
		if (idPagamento == null) {
			return;
		}
		pagamentoDAO.remover(new Pagamento(idPagamento));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerPagamentoPaceladoByIdPagamento(Integer idPagamento) throws BusinessException {
		if (idPagamento == null) {
			return;
		}

		Integer idItem = pesquisarIdItemPedidoByIdPagamento(idPagamento);
		// Condicao que so ocorre no caso de pagamentos de insumos.
		if (idItem != null) {
			Integer qtdeRecp = pedidoService.pesquisarQuantidadeRecepcionadaItemPedido(idItem);
			// A quantidade recepcionada deve ser zerada para que posteriormente
			// seja removida do estoque. Essa ordem eh importante pois a remocao
			// do item do estoque verifica se o pedido esta como
			// "compra recebida"
			pedidoService.alterarQuantidadeRecepcionada(idItem, 0);
			estoqueService.removerEstoqueItemCompra(idItem, qtdeRecp);
			pagamentoDAO.removerPagamentoPaceladoItemPedido(idItem);
		} else {
			remover(idPagamento);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void retornarLiquidacaoPagamento(Integer idPagamento) throws BusinessException {
		if (idPagamento == null) {
			throw new BusinessException("O ID do pagamento é obrigatório para retornar a liquidação.");
		}
		pagamentoDAO.retornarLiquidacaoPagamento(idPagamento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void retornarLiquidacaoPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela)
			throws BusinessException {
		if (numeroNF == null || idFornecedor == null || parcela == null) {
			throw new BusinessException(
					"O número da NF, ID do fornecedor e a parcela são obrigatórios para retornar a liquidação da NF.");
		}
		pagamentoDAO.retornarLiquidacaoPagamentoNFParcelada(numeroNF, idFornecedor, parcela);
	}
}
