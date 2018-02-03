package br.com.svr.service.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_cidade", schema = "enderecamento")
@InformacaoValidavel
public class Cidade implements Serializable {

	private static final long serialVersionUID = -5219769003137071691L;

	@Column(name = "cod_ibge")
	@InformacaoValidavel(intervaloComprimento = { 1, 10 }, nomeExibicao = "Código do município")
	private String codigoMunicipio;

	@Column(name = "cidade")
	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 50 }, nomeExibicao = "Cidade")
	private String descricao;

	@Id
	@SequenceGenerator(name = "cidadeSequence", sequenceName = "enderecamento.seq_cidade_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cidadeSequence")
	@Column(name = "id_cidade")
	private Integer id;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Pais")
	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "id_pais")
	private Pais pais;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "UF")
	private String uf;

	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public String getDescricao() {
		return descricao;
	}

	public Integer getId() {
		return id;
	}

	public Pais getPais() {
		return pais;
	}

	public String getUf() {
		return uf;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
}
