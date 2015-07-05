package br.com.plastecno.service.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_peca_catalogada", schema = "vendas")
@InformacaoValidavel
public class PecaCatalogada {
	private String descricao;

	public PecaCatalogada() {
	}

	public PecaCatalogada(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
