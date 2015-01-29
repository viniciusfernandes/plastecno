package br.com.plastecno.service;

import javax.ejb.Local;

@Local
public interface EstoqueService {
	void inserirItemEstoque(Integer idItemPedido);
}
