package br.com.plastecno.service.entity;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;
import br.com.plastecno.util.StringUtils;

@MappedSuperclass
public abstract class Logradouro {
	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 40 }, nomeExibicao = "Bairro")
	private String bairro;

	@InformacaoValidavel(obrigatorio = true, tamanho = 8, nomeExibicao = "CEP")
	private String cep;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 50 }, nomeExibicao = "Cidade")
	private String cidade;

	private Boolean codificado = true;

	@Column(name = "codigo_municipio")
	@InformacaoValidavel(intervaloComprimento = { 1, 7 }, nomeExibicao = "Código do município")
	private String codigoMunicipio;

	@InformacaoValidavel(intervaloComprimento = { 1, 250 }, nomeExibicao = "Complemento do logradouro")
	private String complemento;
	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 150 }, nomeExibicao = "Endereco")
	private String endereco;

	private String numero;
	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 50 }, nomeExibicao = "Pais")
	private String pais;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_logradouro")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo do Logradouro do cliente")
	private TipoLogradouro tipoLogradouro;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "UF")
	private String uf;

	public void configurar(Endereco endereco) {
		if (endereco == null) {
			return;
		}
		this.setBairro(endereco.getBairro().getDescricao());
	}

	private String gerarCepEnderecoNumeroBairro(boolean cepIncluido) {
		StringBuilder end = new StringBuilder();
		if (cepIncluido && StringUtils.isNotEmpty(getCep())) {
			end.append(getCep()).append(" - ");
		}
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
		c.setCodigoMunicipio(getCodigoMunicipio());

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

	public String getCepEnderecoNumeroBairro() {
		return gerarCepEnderecoNumeroBairro(true);
	}

	public String getCidade() {
		return cidade;
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

	public String getEnderecoNumeroBairro() {
		return gerarCepEnderecoNumeroBairro(false);
	}

	public abstract Integer getId();

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

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
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

	public abstract void setId(Integer id);

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
