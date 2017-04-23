package br.com.plastecno.service;

import javax.ejb.Local;

@Local
public interface DuplicataVencidaMonitor {

	void monitorarDuplicataVencida();

}
