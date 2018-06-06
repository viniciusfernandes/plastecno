package br.com.svr.service.monitor;

import javax.ejb.Local;

@Local
public interface DuplicataVencidaMonitor {

	void monitorarDuplicataVencida();

}
