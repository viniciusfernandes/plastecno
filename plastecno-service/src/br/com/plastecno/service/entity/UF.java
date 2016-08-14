package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_estado", schema="enderecamento")
@InformacaoValidavel
public class UF implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8857978914040973864L;
	@Id
	@Column(name="uf")
	@InformacaoValidavel(obrigatorio=true, intervaloComprimento={1,2}, nomeExibicao="Sigla da UF")
	private String sigla;
	@Column(name="estado")
	private String descricao;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_pais")
	private Pais pais;
	
	public UF() {}
	
	
	public UF(String sigla, Pais pais) {
		this.sigla = sigla;
		this.pais = pais;
	}


	public String getDescricao() {
		return descricao;
	}
	public Pais getPais() {
		return pais;
	}
	public String getSigla() {
		return sigla;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public void setPais(Pais pais) {
		this.pais = pais;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
}
