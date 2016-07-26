package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class TributosProdutoServico {
	@XmlElement(name = "COFINS")
	private COFINS cofins;

	@XmlElement(name = "ICMS")
	private ICMS icms;

	@XmlElement(name = "II")
	private ImpostoImportacao impostoImportacao;

	@XmlElement(name = "infAdProd")
	private String informacaoAdicional;

	@XmlElement(name = "IPI")
	private IPI ipi;

	@XmlElement(name = "PIS")
	private PIS pis;

	@XmlElement(name = "PISST")
	private PISGeral pisSubstituicaoTributaria;

	public void setCofins(COFINS cofins) {
		this.cofins = cofins;
	}

	public void setIcms(ICMS icms) {
		this.icms = icms;
	}

	public void setImpostoImportacao(ImpostoImportacao impostoImportacao) {
		this.impostoImportacao = impostoImportacao;
	}

	public void setInformacaoAdicional(String informacaoAdicional) {
		this.informacaoAdicional = informacaoAdicional;
	}

	public void setIpi(IPI ipi) {
		this.ipi = ipi;
	}

	public void setPis(PIS pis) {
		this.pis = pis;
	}

	public void setPisSubstituicaoTributaria(PISGeral pisSubstituicaoTributaria) {
		this.pisSubstituicaoTributaria = pisSubstituicaoTributaria;
	}

}
