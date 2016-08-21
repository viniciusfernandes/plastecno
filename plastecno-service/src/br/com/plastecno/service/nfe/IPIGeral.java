package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;

import static br.com.plastecno.service.nfe.constante.TipoTributacaoIPI.*;

public class IPIGeral {
	@XmlElement(name = "pIPI")
	private Double aliquota;

	@XmlElement(name = "CST")
	private String codigoSituacaoTributaria;

	@XmlElement(name = "qUnid")
	private Integer quantidadeUnidadeTributavel;

	@XmlElement(name = "vIPI")
	private Double valor;

	@XmlElement(name = "vBC")
	private Double valorBC;

	@XmlElement(name = "vUnid")
	private Double valorUnidadeTributavel;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC
				* (aliquota / 100d) : 0;
	}

	public IPIGeral carregarValoresAliquotas() {
		valor = calcularValor();
		return this;
	}

	@XmlTransient
	public Double getAliquota() {
		return aliquota;
	}

	@XmlTransient
	public String getCodigoSituacaoTributaria() {
		return codigoSituacaoTributaria;
	}

	@XmlTransient
	public Integer getQuantidadeUnidadeTributavel() {
		return quantidadeUnidadeTributavel;
	}

	@XmlTransient
	public TipoTributacaoIPI getTipoTributacao() {
		return TipoTributacaoIPI.getTipoTributacao(codigoSituacaoTributaria);
	}

	@XmlTransient
	public Double getValor() {
		return valor == null ? 0 : valor;
	}

	@XmlTransient
	public Double getValorBC() {
		return valorBC == null ? 0 : valorBC;
	}

	@XmlTransient
	public Double getValorUnidadeTributavel() {
		return valorUnidadeTributavel;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCodigoSituacaoTributaria(String codigoSituacaoTributaria) {
		this.codigoSituacaoTributaria = codigoSituacaoTributaria;
	}

	public void setQuantidadeUnidadeTributavel(
			Integer quantidadeUnidadeTributavel) {
		this.quantidadeUnidadeTributavel = quantidadeUnidadeTributavel;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void setValorUnidadeTributavel(Double valorUnidadeTributavel) {
		this.valorUnidadeTributavel = valorUnidadeTributavel;
	}

	public void validar() throws BusinessException {
		TipoTributacaoIPI t = getTipoTributacao();

		if (t == null) {
			throw new BusinessException(
					"Código de situação tributária do IPI é obrigatório");
		}

		if (valorBC == null
				&& (IPI_00.equals(t) || IPI_49.equals(t) || IPI_50.equals(t) || IPI_99
						.equals(t))) {
			throw new BusinessException("Valor da BC do IPI é obrigatório");
		}

		if (aliquota == null
				&& (IPI_00.equals(t) || IPI_49.equals(t) || IPI_50.equals(t) || IPI_99
						.equals(t))) {
			throw new BusinessException("Alíquota do IPI é obrigatório");
		}

		if (quantidadeUnidadeTributavel == null
				&& (IPI_00.equals(t) || IPI_49.equals(t) || IPI_50.equals(t) || IPI_99
						.equals(t))) {
			throw new BusinessException(
					"Quantidade de unidade tributável do IPI é obrigatório");
		}

		if (valorUnidadeTributavel == null
				&& (IPI_00.equals(t) || IPI_49.equals(t) || IPI_50.equals(t) || IPI_99
						.equals(t))) {
			throw new BusinessException(
					"Valor pr unidade tributável do IPI é obrigatório");
		}

		if (valor == null
				&& (IPI_00.equals(t) || IPI_49.equals(t) || IPI_50.equals(t) || IPI_99
						.equals(t))) {
			throw new BusinessException("Valor do IPI é obrigatório");
		}
	}
}
