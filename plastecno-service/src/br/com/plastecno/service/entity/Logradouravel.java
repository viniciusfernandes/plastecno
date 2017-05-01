package br.com.plastecno.service.entity;

import br.com.plastecno.service.constante.TipoLogradouro;

public abstract class Logradouravel {

	public abstract String getBairro();

	public abstract String getCep();

	public abstract String getCidade();

	public abstract Boolean getCodificado();

	public abstract String getCodigoMunicipio();

	public abstract String getComplemento();

	public abstract String getDescricao();

	public abstract String getEndereco();

	public abstract Integer getId();

	public abstract String getNumero();

	public abstract String getPais();

	public abstract TipoLogradouro getTipoLogradouro();

	public abstract String getUf();

	public abstract void setBairro(String bairro);

	public abstract void setCep(String cep);

	public abstract void setCidade(String cidade);

	public abstract void setCodificado(Boolean codificado);

	public abstract void setCodigoMunicipio(String codigoMunicipio);

	public abstract void setComplemento(String complemento);

	public abstract void setEndereco(String endereco);

	public abstract void setId(Integer id);

	public abstract void setNumero(String numero);

	public abstract void setPais(String pais);

	public abstract void setTipoLogradouro(TipoLogradouro tipoLogradouro);

	public abstract void setUf(String uf);

}
