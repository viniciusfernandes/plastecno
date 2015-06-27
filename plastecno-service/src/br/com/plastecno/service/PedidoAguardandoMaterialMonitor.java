package br.com.plastecno.service;

import java.util.Collection;

import javax.ejb.Local;

@Local
public interface PedidoAguardandoMaterialMonitor {
	Collection<Integer> reservarItemPedidoAguardandoMaterial();
}
