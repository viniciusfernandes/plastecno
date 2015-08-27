package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.Transient;

import br.com.plastecno.service.constante.FormaMaterial;

public abstract class Item implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1708677510288806140L;

	@Transient
	private String aliquotaICMSFormatado;

	@Transient
	private String aliquotaIPIFormatado;

	@Transient
	private String comissaoFormatado;

	@Transient
	private String comprimentoFormatado;

	@Transient
	private String medidaExternaFomatada;

	@Transient
	private String medidaInternaFomatada;

	@Transient
	private String precoItemFormatado;

	@Transient
	private String precoMedioFormatado;

	@Transient
	private String precoUnidadeFormatado;

	@Transient
	private String precoUnidadeIPIFormatado;

	@Transient
	private String precoVendaFormatado;

	@Transient
	private String valorComissaoFormatado;

	public Item() {
	}

	public void addQuantidade(Integer quantidade) {
		setQuantidade(getQuantidade() + quantidade);
	}

	public double calcularPrecoItem() {
		if (getPrecoUnidade() == null || getQuantidade() == null) {
			return 0d;
		}

		return getPrecoUnidade() * getQuantidade();
	}

	public void configurarMedidaInterna() {
		if (isMedidaExternaIgualInterna()) {
			setMedidaInterna(getMedidaExterna());
		}
	}

	public boolean contemLargura() {
		return getFormaMaterial() != null && getFormaMaterial().contemLargura();
	}

	public boolean contemMedida() {
		return getMedidaExterna() != null || getMedidaInterna() != null || getComprimento() != null;
	}

	public abstract Double getAliquotaICMS();

	public String getAliquotaICMSFormatado() {
		return aliquotaICMSFormatado;
	}

	public abstract Double getAliquotaIPI();

	public String getAliquotaIPIFormatado() {
		return aliquotaIPIFormatado;
	}

	public String getComissaoFormatado() {
		return comissaoFormatado;
	}

	public abstract Double getComprimento();

	public String getComprimentoFormatado() {
		if (comprimentoFormatado == null) {
			return " _ ";
		}
		return comprimentoFormatado;
	}

	public String getDescricao() {

		StringBuilder descricao = new StringBuilder();
		if (getMaterial() != null) {
			descricao.append(getFormaMaterial());
			descricao.append(" - ");
			descricao.append(getMaterial().getSigla());
			descricao.append(" - ");
			descricao.append(getMaterial().getDescricao() == null ? " " : getMaterial().getDescricao());
			descricao.append(" - ");
		}

		if (!this.isPeca()) {
			descricao.append(getMedidaExternaFomatada());
			descricao.append(" X ");

			if (getMedidaInterna() != null) {
				descricao.append(getMedidaInternaFomatada());
				descricao.append(" X ");
			}

			descricao.append(getComprimentoFormatado());
			descricao.append(" mm");
		} else {
			descricao.append(getDescricaoPeca());
		}
		return descricao.toString();
	}

	public String getDescricaoMaterial() {

		StringBuilder descricao = new StringBuilder();
		if (getMaterial() != null) {
			descricao.append(getFormaMaterial());
			descricao.append(" - ");
			descricao.append(getMaterial().getSigla());
		}

		return descricao.toString();
	}

	public abstract String getDescricaoPeca();

	public abstract FormaMaterial getFormaMaterial();

	public abstract Integer getId();

	public abstract Material getMaterial();

	public abstract Double getMedidaExterna();

	public String getMedidaExternaFomatada() {
		if (medidaExternaFomatada == null) {
			return " _ ";
		}
		return medidaExternaFomatada;
	}

	public abstract Double getMedidaInterna();

	public String getMedidaInternaFomatada() {
		if (medidaInternaFomatada == null) {
			return " _ ";
		}
		return medidaInternaFomatada;
	}

	public String getPrecoItemFormatado() {
		return precoItemFormatado;
	}

	public String getPrecoMedioFormatado() {
		return precoMedioFormatado;
	}

	public abstract Double getPrecoUnidade();

	public String getPrecoUnidadeFormatado() {
		return precoUnidadeFormatado;
	}

	public abstract Double getPrecoUnidadeIPI();

	public String getPrecoUnidadeIPIFormatado() {
		return precoUnidadeIPIFormatado;
	}

	public String getPrecoVendaFormatado() {
		return precoVendaFormatado;
	}

	public abstract Integer getQuantidade();

	public String getValorComissaoFormatado() {
		return valorComissaoFormatado;
	}

	public boolean isCoincidente(Item item) {
		boolean coincide = false;

		coincide = (getFormaMaterial() == null && item.getFormaMaterial() == null)
				|| (getFormaMaterial().equals(item.getFormaMaterial()));

		coincide &= (getMaterial() == null && item.getMaterial() == null)
				|| (getMaterial().getId() == null && item.getMaterial().getId() == null)
				|| (getMaterial().getId().equals(item.getMaterial().getId()));

		coincide &= (getMedidaExterna() == null && item.getMedidaExterna() == null)
				|| (getMedidaExterna() != null && getMedidaExterna().equals(item.getMedidaExterna()));

		coincide &= (getMedidaInterna() == null && item.getMedidaInterna() == null)
				|| (getMedidaInterna() != null && getMedidaInterna().equals(item.getMedidaInterna()));

		coincide &= (getComprimento() == null && item.getComprimento() == null)
				|| (getComprimento() != null && getComprimento().equals(item.getComprimento()));

		return coincide;
	}

	public boolean isFormaMaterialVazada() {
		return getFormaMaterial() != null && getFormaMaterial().isFormaMaterialVazada();
	}

	public boolean isMedidaExternaIgualInterna() {
		return getFormaMaterial() != null && getFormaMaterial().isMedidaExternaIgualInterna();
	}

	public abstract boolean isNovo();

	public boolean isPeca() {
		return FormaMaterial.PC.equals(getFormaMaterial());
	}

	public abstract void setAliquotaICMS(Double aliquotaICMS);

	public void setAliquotaICMSFormatado(String aliquotaICMSFormatado) {
		this.aliquotaICMSFormatado = aliquotaICMSFormatado;
	}

	public abstract void setAliquotaIPI(Double aliquotaIPI);

	public void setAliquotaIPIFormatado(String aliquotaIPIFormatado) {
		this.aliquotaIPIFormatado = aliquotaIPIFormatado;
	}

	public void setComissaoFormatado(String comissaoFormatado) {
		this.comissaoFormatado = comissaoFormatado;
	}

	public abstract void setComprimento(Double comprimento);

	public void setComprimentoFormatado(String comprimentoFormatado) {
		this.comprimentoFormatado = comprimentoFormatado;
	}

	public abstract void setDescricaoPeca(String descricaoPeca);

	public abstract void setFormaMaterial(FormaMaterial formaMaterial);

	public abstract void setId(Integer id);

	public abstract void setMaterial(Material material);

	public abstract void setMedidaExterna(Double medidaExterna);

	public void setMedidaExternaFomatada(String medidaExternaFomatada) {
		this.medidaExternaFomatada = medidaExternaFomatada;
	}

	public abstract void setMedidaInterna(Double medidaInterna);

	public void setMedidaInternaFomatada(String medidaInternaFomatada) {
		this.medidaInternaFomatada = medidaInternaFomatada;
	}

	public void setPrecoItemFormatado(String precoItemFormatado) {
		this.precoItemFormatado = precoItemFormatado;
	}

	public void setPrecoMedioFormatado(String precoMedioFormatado) {
		this.precoMedioFormatado = precoMedioFormatado;
	}

	public abstract void setPrecoUnidade(Double precoUnidade);

	public void setPrecoUnidadeFormatado(String precoUnidadeFormatado) {
		this.precoUnidadeFormatado = precoUnidadeFormatado;
	}

	public abstract void setPrecoUnidadeIPI(Double precoUnidadeIPI);

	public void setPrecoUnidadeIPIFormatado(String precoUnidadeIPIFormatado) {
		this.precoUnidadeIPIFormatado = precoUnidadeIPIFormatado;
	}

	public void setPrecoVendaFormatado(String precoVendaFormatado) {
		this.precoVendaFormatado = precoVendaFormatado;
	}

	public abstract void setQuantidade(Integer quantidade);

	public void setValorComissionadoFormatado(String valorComissaoFormatado) {
		this.valorComissaoFormatado = valorComissaoFormatado;
	}
}
