package br.com.plastecno.service.impl;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

import br.com.plastecno.service.DuplicataService;
import br.com.plastecno.service.DuplicataVencidaMonitor;

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
