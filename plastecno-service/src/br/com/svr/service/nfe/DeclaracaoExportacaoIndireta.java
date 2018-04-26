package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "numeroRegistro", "chaveAcessoRecebida", "quantidadeItem" })
public class DeclaracaoExportacaoIndireta {

	@InformacaoValidavel(obrigatorio = true, padrao = { "\\d{44}" }, padraoExemplo = "44 digitos", nomeExibicao = "Chave de acesso de exportação indireta")
	@XmlElement(name = "chNFe")
	private String chaveAcessoRecebida;

	@InformacaoValidavel(obrigatorio = true, padrao = { "\\d{12}" }, padraoExemplo = "12 digitos", nomeExibicao = "Número de registro de exportação indireta")
	@XmlElement(name = "nRE")
	private String numeroRegistro;

	@InformacaoValidavel(obrigatorio = true, decimal = { 11, 4 }, nomeExibicao = "Quantidade do item de exportação indireta")
	@XmlElement(name = "qExport")
	private Double quantidadeItem;

	@XmlTransient
	public String getChaveAcessoRecebida() {
		return chaveAcessoRecebida;
	}

	@XmlTransient
	public String getNumeroRegistro() {
		return numeroRegistro;
	}

	@XmlTransient
	public Double getQuantidadeItem() {
		return quantidadeItem;
	}

	public void setChaveAcessoRecebida(String chaveAcessoRecebida) {
		this.chaveAcessoRecebida = chaveAcessoRecebida;
	}

	public void setNumeroRegistro(String numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}

	public void setQuantidadeItem(Double quantidadeItem) {
		this.quantidadeItem = quantidadeItem;
	}
}
