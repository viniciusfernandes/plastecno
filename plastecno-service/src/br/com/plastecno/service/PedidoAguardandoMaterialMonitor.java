package br.com.plastecno.service;

import javax.ejb.Local;

@Local
public interface PedidoAguardandoMaterialMonitor {
	void reservarItemPedidoAguardandoMaterial();
}
