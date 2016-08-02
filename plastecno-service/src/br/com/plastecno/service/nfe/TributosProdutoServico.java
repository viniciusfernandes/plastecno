package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

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

	public boolean contemCOFINS() {
		return cofins != null && cofins.getTipoConfins() != null;
	}

	public boolean contemICMS() {
		return icms != null && icms.getTipoIcms() != null;
	}

	public boolean contemImpostoImportacao() {
		return impostoImportacao != null;
	}

	public boolean contemIPI() {
		return ipi != null && ipi.getTipoIpi() != null;
	}

	public boolean contemPIS() {
		return pis != null && pis.getTipoPis() != null;
	}

	@XmlTransient
	public ICMS getIcms() {
		return icms;
	}

	@XmlTransient
	public ImpostoImportacao getImpostoImportacao() {
		return impostoImportacao;
	}

	@XmlTransient
	public COFINSGeral getTipoCofins() {
		return cofins != null ? cofins.getTipoConfins() : null;
	}

	@XmlTransient
	public ICMSGeral getTipoIcms() {
		return icms != null ? icms.getTipoIcms() : null;
	}

	@XmlTransient
	public IPIGeral getTipoIpi() {
		return ipi != null ? ipi.getTipoIpi() : null;
	}

	@XmlTransient
	public PISGeral getTipoPis() {
		return pis != null ? pis.getTipoPis() : null;
	}

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
