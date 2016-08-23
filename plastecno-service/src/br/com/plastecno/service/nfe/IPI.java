package br.com.plastecno.service.nfe;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;
import br.com.plastecno.service.validacao.Validavel;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoIPI.*;

public class IPI implements Validavel {
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
	public String getClasseEnquadramento() {
		return classeEnquadramento;
	}

	@XmlTransient
	public String getClasseEnquadramentoCigarrosBebidas() {
		return classeEnquadramentoCigarrosBebidas;
	}

	@XmlTransient
	public String getCnpjProdutor() {
		return cnpjProdutor;
	}

	@XmlTransient
	public String getCodigoEnquadramento() {
		return codigoEnquadramento;
	}

	@XmlTransient
	public String getCodigoSeloControle() {
		return codigoSeloControle;
	}

	@XmlTransient
	public IPIGeral getIpiNt() {
		return ipiNt;
	}

	@XmlTransient
	public IPIGeral getIpiTrib() {
		return ipiTrib;
	}

	@XmlTransient
	public Integer getQuantidadeSeloControle() {
		return quantidadeSeloControle;
	}

	@XmlTransient
	public IPIGeral getTipoIpi() {
		if (tipoIpi == null) {
			recuperarTipoIpi();
		}
		return tipoIpi;
	}

	private void recuperarTipoIpi() {
		Field[] campos = this.getClass().getDeclaredFields();
		Object conteudo = null;
		for (Field campo : campos) {
			campo.setAccessible(true);
			try {
				if ((conteudo = campo.get(this)) == null) {
					campo.setAccessible(false);
					continue;
				}

				setTipoIpi((IPIGeral) conteudo);

			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Nao foi possivel recuperar o tipo de IPI a partir do xml do servidor", e);
			} finally {
				campo.setAccessible(false);
			}
		}
	}

	public void setClasseEnquadramento(String classeEnquadramento) {
		this.classeEnquadramento = classeEnquadramento;
	}

	public void setClasseEnquadramentoCigarrosBebidas(String classeEnquadramentoCigarrosBebidas) {
		this.classeEnquadramentoCigarrosBebidas = classeEnquadramentoCigarrosBebidas;
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
			this.tipoIpi = null;
			return;

		}
		TipoTributacaoIPI t = tipoIpi.getTipoTributacao();
		if (t == null) {
			this.tipoIpi = null;
			return;
		}

		if (IPI_00.equals(t) || IPI_49.equals(t) || IPI_50.equals(t) || IPI_99.equals(t)) {
			ipiTrib = tipoIpi;
		} else {
			ipiNt = tipoIpi;
		}
		this.tipoIpi = tipoIpi;
	}

	public void validar() throws BusinessException {
		if (tipoIpi == null) {
			throw new BusinessException("Tipo IPI � obrigat�rio");
		}

		if (classeEnquadramentoCigarrosBebidas != null && classeEnquadramentoCigarrosBebidas.length() != 5) {
			throw new BusinessException("Classe de enquadramento para cigarro e bebida do IPI deve ter tamanho 5");
		}

		if (cnpjProdutor != null && cnpjProdutor.length() != 14) {
			throw new BusinessException("CNPJ do produto da mercadoria do IPI deve ter tamanho 14");
		}

		if (codigoSeloControle != null && codigoSeloControle.length() != 5) {
			throw new BusinessException("C�digo do selo de controle do IPI deve ter o tamanho de 5");
		}

		if (classeEnquadramento != null) {
			throw new BusinessException("Classe de enquadramento do IPI � obrigat�rio");
		}

		if (cnpjProdutor != null) {
			setCnpjProdutor(cnpjProdutor.replaceAll("\\D", ""));
		}
	}
}
