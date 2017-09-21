package br.com.plastecno.service.impl;

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

import br.com.plastecno.service.PagamentoService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.TipoPagamento;
import br.com.plastecno.service.dao.PagamentoDAO;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.GrupoWrapper;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class PagamentoServiceImpl implements PagamentoService {

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

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
	public Pagamento gerarPagamentoItemPedido(Integer idItemPedido) {
		if (idItemPedido == null) {
			return null;
		}
		ItemPedido i = pedidoService.pesquisarItemPedidoPagamento(idItemPedido);
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
		p.setQuantidadeItem(i.getQuantidade());
		p.setSequencialItem(i.getSequencial());
		p.setTipoPagamento(TipoPagamento.INSUMO);
		p.setTotalParcelas(calcularTotalParcelas(i.getIdPedido()));
		// Nao devemos usar o valor total pois na compra o IPI nao eh destacado.
		p.setValor(i.calcularPrecoItem());
		p.setValorCreditoICMS(i.getValorICMS());
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
		Double val = null;
		final String vlTotal = "valorTotal";
		for (Pagamento p : lPagamento) {
			// Agrupando os pagamentos de compra pelo numero da NF.
			if (p.getNumeroNF() != null) {
				// Aqui estamos concatenando u numero da NF com o ID do
				// fornecedor para criar o ID pois diferentes fornecedores podem
				// ter o mesmo numreo de NF, assim minimizamos conflitos.
				gr = relatorio.addGrupo(String.valueOf(p.getNumeroNF()) + p.getIdFornecedor() + p.getParcela(), p);
				gr.setPropriedade("numeroNF", p.getNumeroNF());
				gr.setPropriedade("dataVencimento", StringUtils.formatarData(p.getDataVencimento()));
				gr.setPropriedade("liquidado", p.isLiquidado());
				gr.setPropriedade("vencido", !p.isLiquidado() && p.isVencido());

				val = (Double) gr.getPropriedade(vlTotal);
				if (val == null) {
					val = 0d;
				}

				// O valor da NF do relatorio deve ser a soma de todos valores
				// dos itens.
				gr.setPropriedade(vlTotal, val + p.getValor());
			} else {
				// Todos os outros tipos de pagamentos nao serao agrupados.
				relatorio.addElemento(p.getId().toString(), p);
			}
		}

		// Arredondando o valor total das NFs no fim para evitar problemas de
		// arredondamentos
		List<GrupoWrapper<String, Pagamento>> lGRupo = relatorio.getListaGrupo();
		for (GrupoWrapper<String, Pagamento> g : lGRupo) {
			g.setPropriedade(vlTotal, NumeroUtils.arredondarValorMonetario((Double) g.getPropriedade(vlTotal)));
		}
		return relatorio;
	}

	@PostConstruct
	public void init() {
		pagamentoDAO = new PagamentoDAO(entityManager);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserir(Pagamento pagamento) throws BusinessException {
		if (pagamento == null) {
			return null;
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

		return pagamentoDAO.alterar(pagamento).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirPagamentoParceladoItemPedido(Integer numeroNF, Double valorNF, Date dataVencimento,
			Date dataEmissao, Integer modalidadeFrete, List<Integer> listaIdItem) throws BusinessException {

		if (pedidoService.contemFornecedorDistintoByIdItem(listaIdItem)) {
			throw new BusinessException(
					"Os itens a serem pagos não são do mesmo fornecedor. Verifique os pedidos enviados.");
		}

		// Aqui estamos validando se os itens enviados ja possuem os pagamentos
		// de todas as suas quantidades do pedido.
		validarPagamentoTotalizadoByIdItem(listaIdItem);

		Pagamento p = null;
		for (Integer idItem : listaIdItem) {
			p = gerarPagamentoItemPedido(idItem);
			p.setNumeroNF(numeroNF);
			p.setValorNF(valorNF);
			p.setModalidadeFrete(modalidadeFrete);
			p.setDataEmissao(dataEmissao);
			p.setDataVencimento(dataVencimento);

			inserirPagamentoParceladoItemPedido(p);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirPagamentoParceladoItemPedido(Pagamento pagamento) throws BusinessException {
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
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void liquidarPagamento(Integer idPagamento) {
		if (idPagamento == null) {
			return;
		}
		pagamentoDAO.liquidarPagamento(idPagamento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void liquidarPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela) {
		pagamentoDAO.liquidarPagamentoNFParcelada(numeroNF, idFornecedor, parcela);
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remover(Integer idPagamento) throws BusinessException {
		if (idPagamento == null) {
			return;
		}
		pagamentoDAO.remover(new Pagamento(idPagamento));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerPagamentoPaceladoItemPedido(Integer idItemPedido) throws BusinessException {
		if (idItemPedido == null) {
			return;
		}
		pagamentoDAO.removerPagamentoPaceladoItemPedido(idItemPedido);
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

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarPagamentoTotalizadoByIdItem(List<Integer> listaIdItem) throws BusinessException {
		List<Integer[]> lPago = verificarPagamentoTotalizadoByIdItem(listaIdItem);
		if (lPago.size() > 0) {
			BusinessException e = new BusinessException();
			for (Integer[] item : lPago) {
				e.addMensagem("O item " + item[1] + " do pedido No. " + item[2]
						+ " tem os pagamentos de todas as suas quantidades cadastradas.");
			}
			throw e;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer[]> verificarPagamentoTotalizadoByIdItem(List<Integer> listaIdItem) {
		List<Integer[]> lItem = pagamentoDAO.pesquisarQuantidadePagaByIdItem(listaIdItem);
		if (lItem.isEmpty()) {
			return new ArrayList<Integer[]>();
		}
		List<Integer[]> lPago = new ArrayList<Integer[]>();
		int qtde = 0;
		int qtdeTotal = 0;
		for (Integer[] item : lItem) {
			qtde = item[3] == null ? 0 : item[3].intValue();
			qtdeTotal = item[4] == null ? 0 : item[4].intValue();
			if (qtde > qtdeTotal) {
				lPago.add(new Integer[] { item[0], item[1], item[2] });
			}
		}
		return lPago;

	}

}
