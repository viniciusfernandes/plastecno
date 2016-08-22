package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class TributosProdutoServico {
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "COFINS do produtos/servi�os")
	@XmlElement(name = "COFINS")
	private COFINS cofins;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "ICMS do produtos/servi�os")
	@XmlElement(name = "ICMS")
	private ICMS icms;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Imposto de importa��o do produtos/servi�os")
	@XmlElement(name = "II")
	private ImpostoImportacao impostoImportacao;

	@XmlElement(name = "infAdProd")
	private String informacaoAdicional;

	@InformacaoValidavel(nomeExibicao = "IPI do produtos/servi�os")
	@XmlElement(name = "IPI")
	private IPI ipi;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ISS do produtos/servi�os")
	@XmlElement(name = "ISSQN")
	private ISSQN issqn;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "PIS do produtos/servi�os")
	@XmlElement(name = "PIS")
	private PIS pis;

	@XmlElement(name = "PISST")
	private PISGeral pisSubstituicaoTributaria;

	public boolean contemCOFINS() {
		return cofins != null && cofins.getTipoCofins() != null;
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

	public boolean contemISS() {
		return issqn != null;
	}

	public boolean contemPIS() {
		return pis != null && pis.getTipoPis() != null;
	}

	@XmlTransient
	public COFINS getCofins() {
		return cofins;
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
	public String getInformacaoAdicional() {
		return informacaoAdicional;
	}

	@XmlTransient
	public IPI getIpi() {
		return ipi;
	}

	@XmlTransient
	public ISSQN getIssqn() {
		return issqn;
	}

	@XmlTransient
	public PIS getPis() {
		return pis;
	}

	@XmlTransient
	public PISGeral getPisSubstituicaoTributaria() {
		return pisSubstituicaoTributaria;
	}

	@XmlTransient
	public COFINSGeral getTipoCofins() {
		return cofins != null ? cofins.getTipoCofins() : null;
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

	public void setIssqn(ISSQN issqn) {
		this.issqn = issqn;
	}

	public void setPis(PIS pis) {
		this.pis = pis;
	}

	public void setPisSubstituicaoTributaria(PISGeral pisSubstituicaoTributaria) {
		this.pisSubstituicaoTributaria = pisSubstituicaoTributaria;
	}

}
