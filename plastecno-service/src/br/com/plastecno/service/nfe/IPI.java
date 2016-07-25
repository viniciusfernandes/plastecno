package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class IPI {
	@XmlElement(name = "clEnq")
	private String classeEnquadramento;

	@XmlElement(name = "CNPJProd")
	private String cnpjProdutor;

	@XmlElement(name = "cSelo")
	private String codigoSeloControle;

	@XmlElement(name = "qSelo")
	private Integer quantidadeSeloControle;

	@XmlElement(name = "cEnq")
	private String codigoEnquadramento;

	@XmlElement(name = "IPITrib")
	private IPIGeral ipiTrib;
	
	@XmlElement(name = "IPINI")
	private IPIGeral ipint;
}
