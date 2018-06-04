package br.com.svr.service.monitor.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

import br.com.svr.service.RegistroEstoqueService;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.monitor.RegistroEstoqueMonitor;

@Stateless
public class RegistroEstoqueMonitorImpl implements RegistroEstoqueMonitor {
	private Logger logger = Logger.getLogger(RegistroEstoqueMonitorImpl.class.getName());

	@EJB
	private RegistroEstoqueService registroEstoqueService;

	@Override
	@Schedule(hour = "23", minute = "59")
	public void removerRegistroExpirado() {
		try {
			registroEstoqueService.removerRegistroExpirado();
		} catch (BusinessException e) {
			logger.log(
					Level.SEVERE,
					"Falha no monitoramento dos registros de estoque expirados. Possivel causa: "
							+ e.getMensagemConcatenada(), e);
		}
	}
}
