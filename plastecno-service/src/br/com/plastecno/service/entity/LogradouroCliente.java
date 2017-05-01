package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_logradouro_cliente", schema = "vendas")
@InformacaoValidavel(validarHierarquia = true)
public class LogradouroCliente extends Logradouravel implements Serializable, Cloneable {
	private static final long serialVersionUID = -8911271247053259317L;
	private String bairro;

	private boolean cancelado = false;

	private String cep;

	private String cidade;

	@ManyToOne
	@JoinColumn(name = "id_cliente", referencedColumnName = "id", nullable = false)
	private Cliente cliente;
	private Boolean codificado = true;
	@Transient
	private String codigoMunicipio;
	private String complemento;
	private String endereco;

	@Id
	@SequenceGenerator(name = "logradouroClienteSequence", sequenceName = "vendas.seq_logradouro_cliente_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logradouroClienteSequence")
	private Integer id;

	private String numero;
	private String pais;
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_logradouro")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo do Logradouro do cliente")
	private TipoLogradouro tipoLogradouro;
	private String uf;

	public LogradouroCliente() {
	}

	public Endereco gerarEndereco() {
		Pais p = new Pais();
		p.setDescricao(getPais());

		UF uf = new UF();
		uf.setSigla(getUf());
		uf.setPais(p);

		Cidade c = new Cidade();
		c.setDescricao(getCidade());
		c.setPais(p);
		c.setUf(uf.getSigla());

		Bairro b = new Bairro();
		b.setCidade(c);
		b.setDescricao(getBairro());

		Endereco e = new Endereco();
		e.setBairro(b);
		e.setCep(getCep());
		e.setCidade(c);
		e.setDescricao(getEndereco());
		return e;
	}

	public String getBairro() {
		return bairro;
	}

	public String getCep() {
		return cep;
	}

	public String getCidade() {
		return cidade;
	}

	public Cliente getCliente() {
		return cliente;
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
		return endereco;
	}

	public Integer getId() {
		return id;
	}

	public String getNumero() {
		return numero;
	}

	public String getPais() {
		return pais;
	}

	public TipoLogradouro getTipoLogradouro() {
		return tipoLogradouro;
	}

	public String getUf() {
		return uf;
	}

	public boolean isCancelado() {
		return cancelado;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public void setCancelado(boolean cancelado) {
		this.cancelado = cancelado;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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
		this.endereco = endereco;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public void setTipoLogradouro(TipoLogradouro tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
}
