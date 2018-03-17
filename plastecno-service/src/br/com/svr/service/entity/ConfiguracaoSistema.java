package br.com.svr.service.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_configuracao_sistema", schema = "vendas")
public class ConfiguracaoSistema {
	@Id
	private String parametro;

	private String valor;

	public ConfiguracaoSistema() {
	}

	public ConfiguracaoSistema(String parametro, String valor) {
		this.parametro = parametro;
		this.valor = valor;
	}

	public String getParametro() {
		return parametro;
	}

	public String getValor() {
		return valor;
	}

	public void setParametro(String parametro) {
		this.parametro = parametro;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
