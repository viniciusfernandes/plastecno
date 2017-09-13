package br.com.plastecno.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.plastecno.service.DuplicataService;
import br.com.plastecno.service.FaturamentoService;
import br.com.plastecno.service.PagamentoService;
import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.FluxoCaixa;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.DateUtils;

@Stateless
public class FaturamentoServiceImpl implements FaturamentoService {
	@EJB
	private DuplicataService duplicataService;

	@EJB
	private PagamentoService pagamentoService;

	public FluxoCaixa gerarFluxoFaixaByPeriodo(Periodo periodo) throws BusinessException {
		if (periodo == null) {
			throw new BusinessException("O período é obrigatório para a geração do fluxo de caixa.");
		}
		List<Pagamento> lPag = pagamentoService.pesquisarPagamentoByPeriodo(periodo);
		List<NFeDuplicata> lDup = duplicataService.pesquisarDuplicataByPeriodo(periodo);
		FluxoCaixa f = new FluxoCaixa(periodo);
		for (NFeDuplicata dup : lDup) {
			f.addDuplicata(DateUtils.gerarDataSemHorario(dup.getDataVencimento()), dup.getValor());
		}

		Date dtVenc = null;
		for (Pagamento pag : lPag) {
			dtVenc = DateUtils.gerarDataSemHorario(pag.getDataVencimento());
			f.addPagamento(dtVenc, pag.getValor());
			f.addCreditoICMS(dtVenc, pag.getValorCreditoICMS());
		}
		return f;
	}
	
	
}
