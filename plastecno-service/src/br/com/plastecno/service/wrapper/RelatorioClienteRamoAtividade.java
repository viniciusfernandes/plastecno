package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Classe que encapsula tota a informacao do relatorio de cliente por ramo de atividades.
 */
public class RelatorioClienteRamoAtividade { 

	private final String titulo; 
	private final Map<String, VendedorClienteWrapper> repositorioVendedorCliente = new HashMap<String, VendedorClienteWrapper>();
	
	public RelatorioClienteRamoAtividade(String titulo) {
		this.titulo = titulo;
	}
	
	public void addCliente(ClienteWrapper cliente) {
		VendedorClienteWrapper vendedor = this.repositorioVendedorCliente.get(cliente.getNomeVendedor());
		if (vendedor != null) {
			vendedor.addCliente(cliente);
		} else {
			vendedor = new VendedorClienteWrapper(cliente.getNomeVendedor());
			vendedor.addCliente(cliente);
			this.repositorioVendedorCliente.put(vendedor.getNome(), vendedor);
		}
	}
	
	public List<VendedorClienteWrapper> getListaVendedorCliente() {
		return new ArrayList<VendedorClienteWrapper>(repositorioVendedorCliente.values());
	}
	
	public String getTitulo () {
		return this.titulo;
	}
	
}