package br.com.plastecno.service.nfe;

import static br.com.plastecno.service.nfe.constante.TipoTributacaoIPI.IPI_00;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoIPI.IPI_49;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoIPI.IPI_50;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoIPI.IPI_99;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "classeEnquadramentoCigarrosBebidas", "cnpjProdutor", "codigoSeloControle",
		"quantidadeSeloControle", "codigoEnquadramento", "ipiTrib", "ipiNt" })
public class IPI {
	@XmlElement(name = "clEnq")
	@InformacaoValidavel(tamanho = 5, nomeExibicao = "Classe de enquadramento de cigarros/bebidas do IPI")
	private String classeEnquadramentoCigarrosBebidas;

	@XmlElement(name = "CNPJProd")
	@InformacaoValidavel(substituicao = "\\D", padrao = "\\d{14}", padraoExemplo = "14 digitos", nomeExibicao = "CNPJ do produtor do IPI")
	private String cnpjProdutor;

	@XmlElement(name = "cEnq")
	private String codigoEnquadramento;

	@XmlElement(name = "cSelo")
	@InformacaoValidavel(tamanho = 5, nomeExibicao = "Código do selo de controle do IPI")
	private String codigoSeloControle;

	@XmlElement(name = "IPINT")
	private IPIGeral ipiNt;

	@XmlElement(name = "IPITrib")
	private IPIGeral ipiTrib;

	@XmlElement(name = "qSelo")
	private Integer quantidadeSeloControle;

	@XmlTransient
	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Tipo IPI")
	private IPIGeral tipoIpi;

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
				if ((conteudo = campo.get(this)) == null || !(conteudo instanceof IPIGeral)) {
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
}
