package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioWrapper {
	private final String titulo;
	private final int detailColumns;
	private final List<MasterDetail> lista = new ArrayList<MasterDetail>();
	private final Map<Object, MasterDetail> map = new HashMap<Object, MasterDetail>();
	private MasterDetail currentMaster = null;

	public RelatorioWrapper(String titulo, int detailColumns) {
		this.titulo = titulo;
		this.detailColumns = detailColumns;
	}

	public void addDetail(int column, String value) {
		currentMaster.addDetail(column, value);
	}

	public void nextDetail() {
		currentMaster.nextDetail();
	}

	public List<MasterDetail> getLista() {
		return lista;
	}

	public String getTitulo() {
		return titulo;
	}

	public void toMaster(Object label) {
		MasterDetail master = map.get(label);
		if (master == null) {
			master = new MasterDetail(label, detailColumns);
			lista.add(master);
			map.put(label, master);
		}
		currentMaster = master;
	}

}
