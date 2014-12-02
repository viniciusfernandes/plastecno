package br.com.plastecno.service.dao;

import java.util.List;

import br.com.plastecno.service.entity.Pedido;

public interface PedidoDAO extends GenericDAO {

	List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	Pedido pesquisarById(Integer idPedido);

	List<Pedido> pesquisarByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

}
