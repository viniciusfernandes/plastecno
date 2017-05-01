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
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;
import br.com.plastecno.util.StringUtils;

@Entity
@Table(name = "tb_logradouro", schema = "vendas")
@InformacaoValidavel
public class LogradouroEndereco implements Logradouro, Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9088499085002422043L;

	private Boolean codificado = true;

	@Transient
	private String codigoMunicipio;

	@InformacaoValidavel(intervaloComprimento = { 1, 250 }, nomeExibicao = "Complemento do logradouro")
	private String complemento;

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "id_endereco")
	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Endereço do Logradouro")
	private Endereco endereco;

	@Id
	@SequenceGenerator(name = "logradouroSequence", sequenceName = "vendas.seq_logradouro_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logradouroSequence")
	private Integer id;

	private String numero;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_logradouro")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo do Logradouro")
	private TipoLogradouro tipoLogradouro;

	public LogradouroEndereco() {
		this(new Endereco());
	}

	public LogradouroEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	/*
	 * Esse metodo foi nao pode ser sobreecrito por setEndereco(Endereco) pois o
	 * preenchimento dos entities pelo vraptor nao compreende essa sobreescricao
	 */
	public void addEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	@Override
	public LogradouroEndereco clone() throws CloneNotSupportedException {
		return (LogradouroEndereco) super.clone();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof LogradouroEndereco && this.id != null && this.id.equals(((LogradouroEndereco) o).id);
	}

	public String getBairro() {
		return this.endereco.getBairro().getDescricao();
	}

	public String getCep() {
		return this.endereco.getCep();
	}

	public String getCidade() {
		return this.endereco.getCidade().getDescricao();
	}

	public Boolean getCodificado() {
		return codificado;
	}

	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	public String getComplemento() {
		return complemento;
	}

	public String getDescricao() {
		return LogradouroUtils.gerarDescricao(this, codificado);
	}

	public String getEndereco() {
		return this.endereco.getDescricao();
	}

	public String getEnderecoComplemento() {
		if (complemento != null && !complemento.isEmpty()) {
			return this.endereco.getDescricao() + ", " + complemento;
		}
		return this.endereco.getDescricao();
	}

	public String getEnderecoNumeroBairro() {
		StringBuilder end = new StringBuilder();
		if (StringUtils.isNotEmpty(getEndereco())) {
			end.append(getEndereco());
		}
		if (StringUtils.isNotEmpty(getNumero())) {
			end.append(" - ").append(getNumero());
		}
		if (StringUtils.isNotEmpty(getBairro())) {
			end.append(" - ").append(getBairro());
		}
		return end.toString();
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdCidade() {
		return endereco.getCidade() != null ? endereco.getCidade().getId() : null;
	}

	public String getNumero() {
		return numero;
	}

	public String getPais() {
		return this.endereco.getCidade().getPais().getDescricao();
	}

	public TipoLogradouro getTipoLogradouro() {
		return this.tipoLogradouro;
	}

	public String getUf() {
		return this.endereco.getCidade().getUf();
	}

	@Override
	public int hashCode() {
		return this.id != null ? this.id.hashCode() : super.hashCode();
	}

	public Endereco recuperarEndereco() {
		return this.endereco;
	}

	public void setBairro(String bairro) {
		this.endereco.getBairro().setDescricao(bairro);
	}

	public void setCep(String cep) {
		this.endereco.setCep(cep);
	}

	public void setCidade(String cidade) {
		this.endereco.getCidade().setDescricao(cidade);
	}

	public void setCodificado(Boolean codificado) {
		this.codificado = codificado;
	}

	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public void setEndereco(String endereco) {
		this.endereco.setDescricao(endereco);
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdCidade(Integer idCidade) {
		endereco.getCidade().setId(idCidade);
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setPais(String pais) {
		this.endereco.getCidade().getPais().setDescricao(pais);
	}

	public void setTipoLogradouro(TipoLogradouro tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public void setUf(String uf) {
		this.endereco.getCidade().setUf(uf);
	}

}
