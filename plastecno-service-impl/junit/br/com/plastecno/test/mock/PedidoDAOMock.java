package br.com.plastecno.test.mock;

import java.util.List;

import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.entity.Pedido;

public class PedidoDAOMock implements PedidoDAO {

	@Override
	public List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pedido pesquisarById(Integer idPedido) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Pedido> pesquisarByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void alterar(Object entidade) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inserir(Object entidade) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> boolean isEntidadeExistente(Class<T> classe, Object idEntidade, String nomeAtributo, Object valorAtributo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo,
			Object nomeIdEntidade, Object valorIdEntidade) {
		// TODO Auto-generated method stub
		return false;
	}

}
