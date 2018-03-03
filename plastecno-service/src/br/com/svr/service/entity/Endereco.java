package br.com.svr.service.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.svr.service.validacao.InformacaoValidavel;

@Entity
@Table(name="tb_endereco", schema="enderecamento")
@InformacaoValidavel
public class Endereco implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6832281657528975828L;
	
	@Id
	@InformacaoValidavel(obrigatorio=true, tamanho=8, nomeExibicao="CEP")
	private String cep;
	
	@Column(name="endereco")
	@InformacaoValidavel(obrigatorio=true, intervaloComprimento={1, 150}, nomeExibicao="Endereco")
	private String descricao;
	
	@InformacaoValidavel(obrigatorio=true, cascata=true)
	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="id_bairro")
	private Bairro bairro;
	
	@InformacaoValidavel(obrigatorio=true, cascata=true)
	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="id_cidade")
	private Cidade cidade;
	
	public Endereco() {
		this(new Bairro(), new Cidade(), new Pais());
	}
	
	public Endereco(Bairro bairro, Cidade cidade, Pais pais) {
		this.bairro = bairro;
		this.cidade = cidade;
		this.cidade.setPais(pais);
		this.bairro.setCidade(cidade);
	}
	
	@Override
	public boolean equals(Object o ) {
		return o instanceof Endereco && this.cep != null && this.cep.equals(((Endereco)o).cep);
	}
	public Bairro getBairro() {
		return bairro;
	}
	public String getCep() {
		return cep;
	}
	public Cidade getCidade() {
		return cidade;
	}
	public String getDescricao() {
		return descricao;
	}
	public int hashCode() {
		return this.cep != null ? this.cep.hashCode() : -1;
	}
	public void setBairro(Bairro bairro) {
		this.bairro = bairro;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	
	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
