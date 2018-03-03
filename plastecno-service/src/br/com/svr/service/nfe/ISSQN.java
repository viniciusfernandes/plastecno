package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "valorBC", "aliquota", "valor", "codigoMunicipioGerador", "itemListaServicos" })
public class ISSQN {
	@InformacaoValidavel(obrigatorio = true, decimal = { 3, 4 }, nomeExibicao = "Alíquota do ISS")
	@XmlElement(name = "vAliq")
	private Double aliquota;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{7}", padraoExemplo = "7 dígitos", nomeExibicao = "Código do município do fato gerador do ISS")
	@XmlElement(name = "cMunFG")
	private String codigoMunicipioGerador;

	@InformacaoValidavel(obrigatorio = true, tamanho = 5, nomeExibicao = "Código do item da lista de serviços do ISS")
	@XmlElement(name = "cListServ")
	private String itemListaServicos;

	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor do ISS")
	@XmlElement(name = "vISSQN")
	private Double valor;

	@InformacaoValidavel(obrigatorio = true, decimal = { 13, 2 }, nomeExibicao = "Valor da BC do ISS")
	@XmlElement(name = "vBC")
	private Double valorBC;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC * (aliquota / 100d) : null;
	}

	public ISSQN carregarValores() {
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
