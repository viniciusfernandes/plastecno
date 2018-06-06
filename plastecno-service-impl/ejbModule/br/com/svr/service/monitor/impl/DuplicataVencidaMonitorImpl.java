package br.com.svr.service.monitor.impl;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

import br.com.svr.service.DuplicataService;
import br.com.svr.service.monitor.DuplicataVencidaMonitor;

@Stateless
public class DuplicataVencidaMonitorImpl implements DuplicataVencidaMonitor {

	@EJB
	private DuplicataService duplicataService;

	@Override
	@Schedule(hour = "23", minute = "59")
	public void monitorarDuplicataVencida() {
		duplicataService.atualizarSituacaoDuplicataVencida();
	}
}
