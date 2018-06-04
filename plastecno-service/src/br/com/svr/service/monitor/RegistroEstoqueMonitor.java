package br.com.svr.service.monitor;

import javax.ejb.Local;

@Local
public interface RegistroEstoqueMonitor {
	void removerRegistroExpirado();
}
