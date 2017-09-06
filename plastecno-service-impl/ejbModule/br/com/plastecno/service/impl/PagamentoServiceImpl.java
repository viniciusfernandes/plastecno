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
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class PagamentoServiceImpl implements PagamentoService {

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	private PagamentoDAO pagamentoDAO;

	@EJB
	private PedidoService pedidoService;

	private Integer calcularTotalParcelas(Integer idPedido) {
		return pedidoService.calcularDataPagamento(idPedido).size();
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
		p.setDescricao(i.getDescricao());
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
		p.setValor(i.getValorTotal());
		p.setValorCreditoICMS(i.getValorICMS());
		return p;
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
	public void inserirPagamentoItemPedido(Pagamento pagamento) throws BusinessException {
		if (pagamento == null) {
			return;
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
		List<Date> listaData = pedidoService.calcularDataPagamento(idPedido, pagamento.getDataRecebimento());

		// Aqui identificamos um pagamento a vista como um total de uma unica
		// parcela
		int totParc = listaData.size() <= 0 ? 1 : listaData.size();
		Pagamento clone = null;
		for (int i = 0; i < totParc; i++) {
			clone = pagamento.clone();
			clone.setDataVencimento(listaData.get(i));
			clone.setParcela(i + 1);
			clone.setTotalParcelas(totParc);
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
		return pagamentoDAO.pesquisarPagamentoByPeriodo(periodo.getInicio(), periodo.getFim());
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
	public void retornarLiquidacaoPagamento(Integer idPagamento) {
		if (idPagamento == null) {
			return;
		}
		pagamentoDAO.alterarPropriedade(Pagamento.class, idPagamento, "liquidado", false);
	}
}
