package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoIPI.*;

public class IPI {
	@XmlElement(name = "clEnq")
	private String classeEnquadramento;

	@XmlElement(name = "CNPJProd")
	private String cnpjProdutor;

	@XmlElement(name = "cEnq")
	private String codigoEnquadramento;

	@XmlElement(name = "cSelo")
	private String codigoSeloControle;

	@XmlElement(name = "IPINT")
	private IPIGeral ipiNt;

	@XmlElement(name = "IPITrib")
	private IPIGeral ipiTrib;

	@XmlElement(name = "qSelo")
	private Integer quantidadeSeloControle;

	@XmlTransient
	private IPIGeral tipoIpi;

	@XmlTransient
	public IPIGeral getTipoIpi() {
		return tipoIpi;
	}

	public void setClasseEnquadramento(String classeEnquadramento) {
		this.classeEnquadramento = classeEnquadramento;
	}

	public void setCnpjProdutor(String cnpjProdutor) {
		this.cnpjProdutor = cnpjProdutor;
	}

	public void setCodigoEnquadramento(String codigoEnquadramento) {
		this.codigoEnquadramento = codigoEnquadramento;
	}

	public void setCodigoSeloControle(String codigoSeloControle) {
		this.codigoSeloControle = codigoSeloControle;
	}

	public void setIpiNt(IPIGeral ipiNt) {
		this.ipiNt = ipiNt;
	}

	public void setIpiTrib(IPIGeral ipiTrib) {
		this.ipiTrib = ipiTrib;
	}

	public void setQuantidadeSeloControle(Integer quantidadeSeloControle) {
		this.quantidadeSeloControle = quantidadeSeloControle;
	}

	/*
	 * Esse metodo foi criado para abreviar as marcacoes no arquivo .jsp
	 */
	public void setTipoIpi(IPIGeral tipoIpi) {
		if (tipoIpi == null) {
			return;

		}
		TipoTributacaoIPI t = tipoIpi.getTipoTributacao();
		if (t == null) {
			return;
		}

		if (IPI_00.equals(t) || IPI_49.equals(t) || IPI_50.equals(t)
				|| IPI_99.equals(t)) {
			ipiTrib = tipoIpi;
		} else {
			ipiNt = tipoIpi;
		}
		this.tipoIpi = tipoIpi;
	}
}
