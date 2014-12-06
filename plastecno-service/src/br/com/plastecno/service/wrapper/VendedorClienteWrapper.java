package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.List;

public class VendedorClienteWrapper  {
	private final String nome;
	private final List<ClienteWrapper> listaCliente = new ArrayList<ClienteWrapper>();
	public VendedorClienteWrapper(String nome) {
		this.nome = nome;
	}
	public void  addCliente(ClienteWrapper cliente) {
		listaCliente.add(cliente);
	}
	public List<ClienteWrapper> getListaCliente() {
		return listaCliente;
	}
	public String getNome() {
		return nome;
	}
	
	public int getTotalClientes() {
		return this.listaCliente.size();
	}
}