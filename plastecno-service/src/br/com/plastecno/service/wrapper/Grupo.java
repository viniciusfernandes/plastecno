package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.List;

import br.com.plastecno.util.NumeroUtils;

class Grupo {

	protected final String nome;
	protected final Double valor;
	protected final String valorFormatado;
	protected List<Grupo> listaSubgrupo;

	Grupo(String nome) {
		this(nome, 0d);
	}

	Grupo(String nome, Double valor) {
		this.nome = nome;
		this.valor = valor;
		this.valorFormatado = NumeroUtils.formatarValorMonetario(valor);
	}

	void addSubgrupo(Grupo grupo) {
		if (this.listaSubgrupo == null) {
			this.listaSubgrupo = new ArrayList<Grupo>();
		}
		this.listaSubgrupo.add(grupo);
	}

	List<Grupo> getListaSubgrupo() {
		return listaSubgrupo;
	}

	void limparListaSubgrupo() {
		if (listaSubgrupo != null) {
			listaSubgrupo.clear();
		}
		;
	}

	public String getNome() {
		return nome;
	}

	public Double getValor() {
		return valor;
	}

	public Double getValorTotal() {
		if (this.listaSubgrupo == null || this.listaSubgrupo.isEmpty()) {
			return 0d;
		}
		
		Double t = 0d;
		for (Grupo g : this.listaSubgrupo) {
			t += g.valor;
		}

		return t;
	}

	int getNumeroSubgrupo() {
		return listaSubgrupo != null ? listaSubgrupo.size() : 0;
	}

	String getValorFormatado() {
		return valorFormatado;
	}
	
	String getValorTotalFormatado() {
		return NumeroUtils.formatarValorMonetario(this.getValorTotal());
	}
}
