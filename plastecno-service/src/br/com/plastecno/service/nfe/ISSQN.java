package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ISSQN {
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, nomeExibicao = "Al�quota do ISS")
	@XmlElement(name = "vAliq")
	private Double aliquota;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{7}", padraoExemplo = "7 d�gitos", nomeExibicao = "C�digo do munic�pio do fato gerador do ISS")
	@XmlElement(name = "cMunFG")
	private String codigoMunicipioGerador;

	@InformacaoValidavel(obrigatorio = true, tamanho = 1, nomeExibicao = "Situa��o tribut�ria do ISS")
	@XmlElement(name = "cSitTrib")
	private String codigoSituacaoTributaria;

	@InformacaoValidavel(obrigatorio = true, tamanho = 5, nomeExibicao = "C�digo do item da lista de servi�os do ISS")
	@XmlElement(name = "cListServ")
	private String itemListaServicos;

	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor do ISS")
	@XmlElement(name = "vISSQN")
	private Double valor;

	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor da BC do ISS")
	@XmlElement(name = "vBC")
	private Double valorBC;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC * (aliquota / 100d) : 0;
	}

	public ISSQN carregarValoresAliquotas() {
		valor = calcularValor();
		return this;
	}

	@XmlTransient
	public Double getAliquota() {
		return aliquota;
	}

	@XmlTransient
	public String getCodigoMunicipioGerador() {
		return codigoMunicipioGerador;
	}

	@XmlTransient
	public String getCodigoSituacaoTributaria() {
		return codigoSituacaoTributaria;
	}

	@XmlTransient
	public String getItemListaServicos() {
		return itemListaServicos;
	}

	@XmlTransient
	public Double getValor() {
		return valor == null ? 0 : valor;
	}

	@XmlTransient
	public Double getValorBC() {
		return valorBC == null ? 0 : valorBC;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCodigoMunicipioGerador(String codigoMunicipioGerador) {
		this.codigoMunicipioGerador = codigoMunicipioGerador;
	}

	public void setCodigoSituacaoTributaria(String codigoSituacaoTributaria) {
		this.codigoSituacaoTributaria = codigoSituacaoTributaria;
	}

	public void setItemListaServicos(String itemListaServicos) {
		this.itemListaServicos = itemListaServicos;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

}
