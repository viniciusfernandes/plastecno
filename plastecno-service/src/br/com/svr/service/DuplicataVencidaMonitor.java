package br.com.svr.service;

import javax.ejb.Local;

@Local
public interface DuplicataVencidaMonitor {

	void monitorarDuplicataVencida();

}
