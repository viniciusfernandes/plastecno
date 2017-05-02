package br.com.plastecno.service.entity;

import br.com.plastecno.service.constante.TipoLogradouro;

public interface Logradouro {
	String getBairro();

	String getCep();

	String getCidade();

	Boolean getCodificado();

	String getCodigoMunicipio();

	String getComplemento();

	String getDescricao();

	String getEndereco();

	Integer getId();

	String getNumero();

	String getPais();

	TipoLogradouro getTipoLogradouro();

	String getUf();

	void setBairro(String bairro);

	void setCep(String cep);

	void setCidade(String cidade);

	void setCodificado(Boolean codificado);

	void setComplemento(String complemento);

	void setEndereco(String endereco);

	void setId(Integer id);

	void setNumero(String numero);

	void setPais(String pais);

	void setTipoLogradouro(TipoLogradouro tipoLogradouro);

	void setUf(String uf);

}
