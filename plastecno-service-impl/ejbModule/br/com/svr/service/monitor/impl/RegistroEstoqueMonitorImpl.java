package br.com.svr.service.monitor.impl;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

import br.com.svr.service.RegistroEstoqueService;
import br.com.svr.service.monitor.RegistroEstoqueMonitor;

@Stateless
public class RegistroEstoqueMonitorImpl implements RegistroEstoqueMonitor {

	@EJB
	private RegistroEstoqueService registroEstoqueService;

	@Override
	@Schedule(hour = "23", minute = "59")
	public void removerRegistroExpirado() {
		registroEstoqueService.removerRegistroExpirado();
	}
}
