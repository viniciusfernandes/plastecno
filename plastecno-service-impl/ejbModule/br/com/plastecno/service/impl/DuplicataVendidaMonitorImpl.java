package br.com.plastecno.service.impl;

import javax.ejb.Schedule;
import javax.ejb.Stateless;

import br.com.plastecno.service.DuplicataService;
import br.com.plastecno.service.DuplicataVencidaMonitor;

@Stateless
public class DuplicataVendidaMonitorImpl implements DuplicataVencidaMonitor {

	private DuplicataService duplicataService;

	@Override
	@Schedule(minute = "*/1")
	public void monitorarDuplicataVencida() {
		System.out.println("ESTA DIS PARANDO O MONITOR DE DUPLICATA");
		duplicataService.atualizarSituacaoDuplicataVencida();
	}
}
