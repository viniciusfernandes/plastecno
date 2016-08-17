package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ISSQN {

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Alíquota do ISS")
	@XmlElement(name = "vAliq")
	private Double aliquota;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 999999 }, nomeExibicao = "Código do município do fato gerador do ISS")
	@XmlElement(name = "cMunFG")
	private Long codigoMunicipioGerador;

	@InformacaoValidavel(obrigatorio = true, tamanho = 1, nomeExibicao = "Situação tributária do ISS")
	@XmlElement(name = "cSitTrib")
	private String codigoSituacaoTributaria;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{3,4}", padraoExemplo = "999 ou 9999", nomeExibicao = "Código do item da lista de serviços do ISS")
	@XmlElement(name = "cListServ")
	private String itemListaServicos;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor do ISS")
	@XmlElement(name = "vISSQN")
	private Double valor;

	@InformacaoValidavel(obrigatorio = true, decimal = { 15, 2 }, nomeExibicao = "Valor da BC do ISS")
	@XmlElement(name = "vBC")
	private Double valorBC;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC
				* (aliquota / 100d) : 0;
	}

	public ISSQN carregarValoresAliquotas() {
		valor = calcularValor();
		return this;
	}

	@XmlTransient
	public Double getValor() {
		return valor == null ? 0 : valor;
	}

	@XmlTransient
	public Double getValorBC() {
		return valorBC;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCodigoMunicipioGerador(Long codigoMunicipioGerador) {
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
