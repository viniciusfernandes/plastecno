package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "valorTotalTributos", "icms", "ipi", "impostoImportacao", "pis", "pisSubstituicaoTributaria",
		"cofins", "icmsInterestadual", "issqn", "informacaoAdicional" })
public class TributosProdutoServico {
	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "COFINS do produtos/serviços")
	@XmlElement(name = "COFINS")
	private COFINS cofins;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "ICMS do produtos/serviços")
	@XmlElement(name = "ICMS")
	private ICMS icms;

	@XmlElement(name = "ICMSUFDest")
	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS interestadual")
	private ICMSInterestadual icmsInterestadual;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Imposto de importação do produtos/serviços")
	@XmlElement(name = "II")
	private ImpostoImportacao impostoImportacao;

	@XmlElement(name = "infAdProd")
	private String informacaoAdicional;

	@InformacaoValidavel(cascata = true, nomeExibicao = "IPI do produtos/serviços")
	@XmlElement(name = "IPI")
	private IPI ipi;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ISS do produtos/serviços")
	@XmlElement(name = "ISSQN")
	private ISSQN issqn;

	@InformacaoValidavel(cascata = true, obrigatorio = true, nomeExibicao = "PIS do produtos/serviços")
	@XmlElement(name = "PIS")
	private PIS pis;

	@XmlElement(name = "PISST")
	private PISGeral pisSubstituicaoTributaria;

	@InformacaoValidavel(decimal = { 13, 2 }, nomeExibicao = "Valor total dos tributos do produto/serviço")
	@XmlElement(name = "vTotTrib")
	private Double valorTotalTributos;

	public boolean contemCOFINS() {
		return cofins != null && cofins.getTipoCofins() != null;
	}

	public boolean contemICMS() {
		return icms != null && icms.getTipoIcms() != null;
	}

	public boolean contemICMSInterestadual() {
		return icmsInterestadual != null;
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
	public ICMSInterestadual getIcmsInterestadual() {
		return icmsInterestadual;
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

	@XmlTransient
	public Double getValorTotalTributos() {
		return valorTotalTributos == null ? 0 : valorTotalTributos;
	}

	public void setCofins(COFINS cofins) {
		this.cofins = cofins;
	}

	public void setIcms(ICMS icms) {
		this.icms = icms;
	}

	public void setIcmsInterestadual(ICMSInterestadual icmsInterestadual) {
		this.icmsInterestadual = icmsInterestadual;
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

	public void setValorTotalTributos(Double valorTotalTributos) {
		this.valorTotalTributos = valorTotalTributos;
	}

}
