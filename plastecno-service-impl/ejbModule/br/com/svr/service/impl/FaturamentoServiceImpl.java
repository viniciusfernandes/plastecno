package br.com.svr.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.svr.service.DuplicataService;
import br.com.svr.service.FaturamentoService;
import br.com.svr.service.PagamentoService;
import br.com.svr.service.entity.NFeDuplicata;
import br.com.svr.service.entity.Pagamento;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.FluxoCaixa;
import br.com.svr.service.wrapper.Periodo;

@Stateless
public class FaturamentoServiceImpl implements FaturamentoService {
	@EJB
	private DuplicataService duplicataService;

	@EJB
	private PagamentoService pagamentoService;

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public FluxoCaixa gerarFluxoFaixaByPeriodo(Periodo periodo) throws BusinessException {
		if (periodo == null) {
			throw new BusinessException("O período é obrigatório para a geração do fluxo de caixa.");
		}
		List<Pagamento> lPag = pagamentoService.pesquisarPagamentoByPeriodo(periodo);
		List<NFeDuplicata> lDup = duplicataService.pesquisarDuplicataByPeriodo(periodo);
		FluxoCaixa f = new FluxoCaixa(periodo);
		for (NFeDuplicata dup : lDup) {
			f.addDuplicata(dup.getDataVencimento(), dup.getValor());
		}

		for (Pagamento pag : lPag) {
			f.addPagamento(pag.getDataVencimento(), pag.getValor() == null ? 0 : pag.getValor(),
					pag.getTipoPagamento(), pag.getValorCreditoICMS() == null ? 0 : pag.getValorCreditoICMS());
		}
		return f;
	}

}
