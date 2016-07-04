package br.com.plastecno.service.nfe;

public class TributoProdutoServico {
	private ICMS icms;
	private ImpostoImportacao impostoImportacao;
	private String informacaoAdicional;
	private IPI ipi;
	private PIS pis;

	public ICMS getIcms() {
		return icms;
	}

	public ImpostoImportacao getImpostoImportacao() {
		return impostoImportacao;
	}

	public String getInformacaoAdicional() {
		return informacaoAdicional;
	}

	public IPI getIpi() {
		return ipi;
	}

	public PIS getPis() {
		return pis;
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
}
