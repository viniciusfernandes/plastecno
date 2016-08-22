package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.*;

public class PISGeral {
	@XmlElement(name = "pPIS")
	private Double aliquota;

	@XmlElement(name = "CST")
	private String codigoSituacaoTributaria;

	@XmlElement(name = "qBCProd")
	private Integer quantidadeVendida;

	@XmlElement(name = "vPIS")
	private Double valor;

	@XmlElement(name = "vAliqProd")
	private Double valorAliquota;

	@XmlElement(name = "vBC")
	private Double valorBC;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC
				* (aliquota / 100d) : 0;
	}

	public PISGeral carregarValoresAliquotas() {
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
	public Integer getQuantidadeVendida() {
		return quantidadeVendida;
	}

	@XmlTransient
	public TipoTributacaoPIS getTipoTributacao() {
		return TipoTributacaoPIS.getTipoTributacao(codigoSituacaoTributaria);
	}

	@XmlTransient
	public Double getValor() {
		return valor == null ? 0 : valor;
	}

	@XmlTransient
	public Double getValorAliquota() {
		return valorAliquota;
	}

	@XmlTransient
	public Double getValorBC() {
		return valorBC == null ? 0 : valorBC;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCodigoSituacaoTributaria(String codigoSituacaoTributaria) {
		this.codigoSituacaoTributaria = codigoSituacaoTributaria;
	}

	public void setQuantidadeVendida(Integer quantidadeVendida) {
		this.quantidadeVendida = quantidadeVendida;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorAliquota(Double valorAliquota) {
		this.valorAliquota = valorAliquota;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void validar() throws BusinessException {
		TipoTributacaoPIS t = getTipoTributacao();
		if (t == null) {
			throw new BusinessException("Situação tributária é obrigatório");
		}

		// Essas tributacoes sao isentas
		if (PIS_4.equals(t) || PIS_6.equals(t) || PIS_7.equals(t)
				|| PIS_8.equals(t) || PIS_9.equals(t)) {

			aliquota = null;
			quantidadeVendida = null;
			valor = null;
			valorAliquota = null;
			valorBC = null;

			return;
		}

		if (valorBC == null
				&& (PIS_1.equals(t) || PIS_2.equals(t) || PIS_99.equals(t) || PIS_ST
						.equals(t))) {
			throw new BusinessException("Valor da BC do PIS é obrigatório");
		}

		if (aliquota == null) {
			throw new BusinessException("Alíquota do PIS é obrigatório");
		}

		if (valor == null) {
			throw new BusinessException("Valor do PIS é obrigatório");
		}

		if (quantidadeVendida == null && (PIS_3.equals(t) || PIS_99.equals(t))) {
			throw new BusinessException(
					"Quantidade vendida do PIS é obrigatório");
		}

		if (valorAliquota == null && (PIS_99.equals(t))) {
			throw new BusinessException(
					"Valor da alíquota do PIS é obrigatório");
		}

	}
}