package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RelatorioWrapper<T, K> {
	/*
	 * Essa lista foi criada para ser utilizada quando um relatorio necessita
	 * agrupar informacoes, por exemplo: um agrupamento de todos os itens do
	 * pedido para um determinado vendedor, ou seja, o agrupamento pode ser feito
	 * adotando como chave o nome do vendedor, ja a lista de elementos desse grupo
	 * seriam os itens do pediso, sendo assim, teriamos como:
	 * 
	 * GrupoWrapper<String, ItemPedido> grupo = ... ;
	 * grupo.addElemento("Vinicius", item1); grupo.addElemento("Vinicius", item2);
	 * grupo.addElemento("Vinicius", item3);
	 * 
	 * 
	 * listaGrupo.addGrupo(grupo);
	 * 
	 * Teremos uma lista de grupos que sera percorrida para montar o agrupamento
	 * das informacoes.
	 */
	private final List<GrupoWrapper<T, K>> listaGrupo = new ArrayList<GrupoWrapper<T, K>>();
	/*
	 * Essa lista foi criada para ser utilizada quando um relatorio contem apenas
	 * linhas, por exemplo: o nome do vendedor na primeira coluna e a quantidade
	 * de vendas na segunda coluna, ou seja, teremos algo como:
	 * 
	 * addElemento("Vinicius", 10);
	 * addElemento("Regina", 43);
	 * addElemento("Marcos", 22);
	 * 
	 * Teremos uma lista de elementos que sera percorrida para exibir a listagem
	 * das informacoes.
	 */
	private final List<K> listaElemento = new ArrayList<K>();

	private final HashMap<Object, GrupoWrapper<T, K>> mapaGrupo = new HashMap<Object, GrupoWrapper<T, K>>();
	private final HashMap<T, K> mapaElemento = new HashMap<T, K>();

	private final String titulo;
	private Object valorTotal;

	public RelatorioWrapper(String titulo) {
		this.titulo = titulo;
	}

	public void addElemento(T idElemento, K elemento) {
		listaElemento.add(elemento);
		mapaElemento.put(idElemento, elemento);
	}

	public GrupoWrapper<T, K> addGrupo(T idGrupo, K elemento) {
		GrupoWrapper<T, K> grupo = mapaGrupo.get(idGrupo);
		if (grupo != null) {
			grupo.addElemento(elemento);
			return grupo;
		}

		grupo = new GrupoWrapper<T, K>(idGrupo, new ArrayList<K>());
		grupo.addElemento(elemento);
		listaGrupo.add(grupo);
		mapaGrupo.put(idGrupo, grupo);
		return grupo;
	}

	public K getElemento(T idGrupo) {
		return mapaElemento.get(idGrupo);
	}

	public GrupoWrapper<T, K> getGrupo(T idGrupo) {
		return mapaGrupo.get(idGrupo);
	}

	public List<K> getListaElemento() {
		return listaElemento;
	}

	public List<GrupoWrapper<T, K>> getListaGrupo() {
		return listaGrupo;
	}

	public String getTitulo() {
		return titulo;
	}

	public Object getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Object valorTotal) {
		this.valorTotal = valorTotal;
	}

}
