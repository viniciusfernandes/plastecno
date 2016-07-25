package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

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
		if (tipoIpi == null || tipoIpi.getCodigoSituacaoTributaria() == null
				|| tipoIpi.getCodigoSituacaoTributaria().isEmpty()) {
			return;

		}

		String codigo = tipoIpi.getCodigoSituacaoTributaria();
		if ("00".equals(codigo) || "49".equals(codigo) || "50".equals(codigo)
				|| "99".equals(codigo)) {
			this.ipiTrib = tipoIpi;
		} else if ("01".equals(codigo) || "02".equals(codigo)
				|| "03".equals(codigo) || "04".equals(codigo)
				|| "51".equals(codigo) || "52".equals(codigo)
				|| "53".equals(codigo) || "54".equals(codigo)
				|| "55".equals(codigo)) {
			this.ipiNt = tipoIpi;
		}
	}
}
