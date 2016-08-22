package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.*;

public class COFINSGeral {
	@XmlElement(name = "pCOFINS")
	private Double aliquota;

	@XmlElement(name = "CST")
	private String codigoSituacaoTributaria;

	@XmlElement(name = "qBCProd")
	private Integer quantidadeVendida;

	@XmlElement(name = "vCOFINS")
	private Double valor;

	@XmlElement(name = "vAliqProd")
	private Double valorAliquota;

	@XmlElement(name = "vBC")
	private Double valorBC;

	public double calcularValor() {
		return valorBC != null && aliquota != null ? valorBC
				* (aliquota / 100d) : 0;
	}

	public COFINSGeral carregarValoresAliquotas() {
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
	public TipoTributacaoCOFINS getTipoTributacao() {
		return TipoTributacaoCOFINS.getTipoTributacao(codigoSituacaoTributaria);
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
		TipoTributacaoCOFINS t = getTipoTributacao();
		if (t == null) {
			throw new BusinessException(
					"Situação tributária do COFINS é obrigatório");
		}

		// Nao tributaveis
		if (COFINS_4.equals(t) || COFINS_6.equals(t) || COFINS_7.equals(t)
				|| COFINS_8.equals(t) || COFINS_9.equals(t)) {

			aliquota = null;
			valor = null;
			valorBC = null;
			quantidadeVendida = null;

			return;
		}

		if (aliquota == null) {
			throw new BusinessException("Alíquota do COFINS é obrigatório");
		}

		if (valor == null) {
			throw new BusinessException("Valor do COFINS é obrigatório");
		}

		if (valorBC == null
				&& (COFINS_1.equals(t) || COFINS_2.equals(t)
						|| COFINS_99.equals(t) || COFINS_ST.equals(t))) {
			throw new BusinessException("Valor do BC do COFINS é obrigatório");
		}

		if (valorAliquota == null
				&& (COFINS_3.equals(t) || COFINS_99.equals(t) || COFINS_ST
						.equals(t))) {
			throw new BusinessException(
					"Valor alíquota do COFINS é obrigatório");
		}

		if (quantidadeVendida == null
				&& (COFINS_3.equals(t) || COFINS_99.equals(t) || COFINS_ST
						.equals(t))) {
			throw new BusinessException(
					"Quantidade vendida do COFINS é obrigatório");
		}

	}
}