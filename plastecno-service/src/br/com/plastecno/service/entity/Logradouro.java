package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_logradouro", schema="vendas")
@Inheritance(strategy=InheritanceType.JOINED)
@InformacaoValidavel
public class Logradouro implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9088499085002422043L;
	
	@Id
	@SequenceGenerator(name = "logradouroSequence", sequenceName = "vendas.seq_logradouro_id", initialValue=1, allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="logradouroSequence")
	private Integer id;
	
	private Integer numero;
	
	@InformacaoValidavel(intervalo={1, 250}, nomeExibicao="Complemento do logradouro")
	private String complemento;

	private Boolean codificado = true; 
	
	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name="id_endereco")
	@InformacaoValidavel(obrigatorio=true, cascata=true, nomeExibicao="Endereço do Logradouro")
	private Endereco endereco;

	@Enumerated(EnumType.ORDINAL)
	@Column(name="id_tipo_logradouro")
	@InformacaoValidavel(obrigatorio=true, nomeExibicao="Tipo do Logradouro")
	private TipoLogradouro tipoLogradouro;

	public Logradouro() {
		this(new Endereco());
	}
	
	public Logradouro(Endereco endereco) {
		this.endereco = endereco;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	
	public String getEndereco(){
		return this.endereco.getDescricao();
	}
	
	public void setEndereco(String endereco){
		this.endereco.setDescricao(endereco);
	}
	
	/*
	 * Esse metodo foi nao pode ser sobreecrito por setEndereco(Endereco)
	 * pois o preenchimento dos entities pelo vraptor nao compreende essa 
	 * sobreescricao 
	 */
	public void addEndereco(Endereco endereco){
		this.endereco = endereco;
	}
	
	public Endereco recuperarEndereco() {
		return this.endereco;
	}
	
	public String getCep(){
		return this.endereco.getCep();
	}
	
	public void setCep(String cep){
		this.endereco.setCep(cep);
	}
	
	public String getBairro(){
		return this.endereco.getBairro().getDescricao();
	}
	
	public void setBairro(String bairro){
		this.endereco.getBairro().setDescricao(bairro);
	}
	
	public String getCidade(){
		return this.endereco.getCidade().getDescricao();
	}
	
	public void setCidade(String cidade){
		this.endereco.getCidade().setDescricao(cidade);
	}
	
	public String getUf(){
		return this.endereco.getCidade().getUf();
	}
	
	public void setUf(String uf){
		this.endereco.getCidade().setUf(uf);
	}

	public TipoLogradouro getTipoLogradouro() {
		return this.tipoLogradouro;
	}
	
	public void setTipoLogradouro(TipoLogradouro tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}
	
	public String getPais() {
		return this.endereco.getCidade().getPais().getDescricao();
	}
	
	public void setPais(String pais) {
		this.endereco.getCidade().getPais().setDescricao(pais);
	}
	
	public Boolean getCodificado() {
		return codificado;
	}

	public void setCodificado(Boolean codificado) {
		this.codificado = codificado;
	}

	public String getDescricao() {
		return this.codificado ? this.gerarDescricaoLogradouroCodificado() 
				: this.gerarDescricaoLogradouroNaoCodificado();
	}
	
	@Override
	public boolean equals (Object o) {
		return o instanceof Logradouro && this.id != null && this.id.equals(((Logradouro)o).id);
	}
	@Override
	public int hashCode () {
		return this.id != null ? this.id.hashCode() : super.hashCode();
	}
	
	private String gerarDescricaoLogradouroCodificado() {
		
		StringBuilder logradouro = new StringBuilder()
		.append(" CEP: ").append(this.getCep())
		.append(" - ").append(this.endereco.getDescricao());
		if (this.numero != null ) {
			logradouro.append(", No. ").append(this.numero);	
		}
		
		logradouro.append(" - ").append(this.complemento != null ? this.complemento : "")
		.append(" - ").append(getBairro())
		.append(" - ").append(this.getCidade())
		.append(" - ").append(this.getUf())
		.append(" - ").append(this.getPais());
		
		return logradouro.toString();
	}
	
	private String gerarDescricaoLogradouroNaoCodificado() {
		
		StringBuilder logradouro = new StringBuilder()
		.append(" CEP: ").append(this.getCep())
		.append(" - ").append(this.complemento != null ? this.complemento : "");
		if (this.numero != null ) {
			logradouro.append(", No. ").append(this.numero);	
		}
		
		logradouro.append(" - ").append(getBairro())
		.append(" - ").append(this.getCidade())
		.append(" - ").append(this.getUf())
		.append(" - ").append(this.getPais());
		
		return logradouro.toString();
	}
	
	@Override
	public Logradouro clone() throws CloneNotSupportedException {
		return (Logradouro) super.clone();
	}
}
