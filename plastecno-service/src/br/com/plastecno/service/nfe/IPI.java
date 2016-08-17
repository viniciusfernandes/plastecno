package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoIPI.*;

public class IPI {
	@XmlElement(name = "cEnq")
	private String classeEnquadramento;

	@XmlElement(name = "clEnq")
	private String classeEnquadramentoCigarrosBebidas;

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

	public void validar() throws BusinessException {
		if (tipoIpi == null) {
			throw new BusinessException("Tipo IPI é obrigatório");
		}

		if (classeEnquadramentoCigarrosBebidas != null
				&& classeEnquadramentoCigarrosBebidas.length() != 5) {
			throw new BusinessException(
					"Classe de enquadramento para cigarro e bebida do IPI é obrigatório");
		}

		if (cnpjProdutor != null && cnpjProdutor.length() != 14) {
			throw new BusinessException(
					"CNPJ do produto da mercadoria do IPI é obrigatório");
		}

		if (codigoSeloControle != null && codigoSeloControle.length() != 5) {
			throw new BusinessException(
					"Código do selo de controle do IPI é obrigatório");
		}

		if (classeEnquadramento != null) {
			throw new BusinessException(
					"Classe de enquadramento do IPI é obrigatório");
		}

	}
}
