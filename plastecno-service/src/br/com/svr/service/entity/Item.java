package br.com.svr.service.entity;

import java.io.Serializable;

import javax.persistence.Transient;

import br.com.svr.service.constante.FormaMaterial;

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

	@Transient
	private String valorICMSFormatado;

	@Transient
	private String valorIPIFormatado;

	@Transient
	private String valorTotalFormatado;

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

	public double calcularPrecoTotalIPI() {
		return getQuantidade() != null ? getQuantidade() * calcularPrecoUnidadeIPI() : 0d;
	}

	public Double calcularPrecoUnidadeIPI() {
		if (getPrecoUnidade() == null) {
			return null;
		}
		if (getPrecoUnidade() == null) {
			return 0d;
		}
		if (getAliquotaIPI() == null) {
			return getPrecoUnidade();
		}
		return getPrecoUnidade() * (1 + getAliquotaIPI());
	}

	public double calcularValorICMS() {
		return getAliquotaICMS() == null ? 0 : calcularPrecoItem() * getAliquotaICMS();
	}

	public double calcularValorIPI() {
		return getAliquotaIPI() == null ? 0 : calcularPrecoItem() * getAliquotaIPI();
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

	public void copiar(ItemEstoque itemEStoque) {
		this.setComprimento(itemEStoque.getComprimento());
		this.setDescricaoPeca(itemEStoque.getDescricaoPeca());
		this.setFormaMaterial(itemEStoque.getFormaMaterial());
		this.setMaterial(itemEStoque.getMaterial());
		this.setMedidaExterna(itemEStoque.getMedidaExterna());
		this.setMedidaInterna(itemEStoque.getMedidaInterna());
		this.setQuantidade(itemEStoque.getQuantidade());
	}

	public void copiar(ItemPedido itemPedido) {
		this.setComprimento(itemPedido.getComprimento());
		this.setDescricaoPeca(itemPedido.getDescricaoPeca());
		this.setFormaMaterial(itemPedido.getFormaMaterial());
		this.setMaterial(itemPedido.getMaterial());
		this.setMedidaExterna(itemPedido.getMedidaExterna());
		this.setMedidaInterna(itemPedido.getMedidaInterna());
		this.setQuantidade(itemPedido.getQuantidade());
		this.setAliquotaIPI(itemPedido.getAliquotaIPI());
		this.setAliquotaICMS(itemPedido.getAliquotaICMS());
	}

	public String gerarCodigo() {
		FormaMaterial f = getFormaMaterial();
		String s = getMaterial() != null ? getMaterial().getSigla() : null;
		return f != null && s != null ? f.toString() + s : null;
	}

	private String gerarDescricaoItem(boolean isCompleto, boolean isDescricaoMaterial, boolean isFormatado) {
		StringBuilder descricao = new StringBuilder();
		if (getMaterial() != null) {
			descricao.append(isDescricaoMaterial ? getFormaMaterial().getDescricao() : getFormaMaterial());
			descricao.append(" - ");

			if (isCompleto) {
				descricao.append(getMaterial().getSigla());
				descricao.append(" - ");
				descricao.append(getMaterial().getDescricao() == null ? " " : getMaterial().getDescricao());
				descricao.append(" - ");
			} else {
				descricao.append(getMaterial().getDescricao() == null ? " " : getMaterial().getDescricao());
				descricao.append(" - ");
			}
		}

		if (!this.isPeca()) {
			descricao.append(isFormatado && getMedidaExternaFomatada() != null ? getMedidaExternaFomatada()
					: getMedidaExterna());
			descricao.append(" X ");

			if (getMedidaInterna() != null) {
				descricao.append(isFormatado && getMedidaInternaFomatada() != null ? getMedidaInternaFomatada()
						: getMedidaInterna());
				descricao.append(" X ");
			}

			descricao.append(isFormatado && getComprimentoFormatado() != null ? getComprimentoFormatado()
					: getComprimento());
			descricao.append(" mm");
		} else {
			descricao.append(getDescricaoPeca());
		}
		return descricao.toString();
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
			return null;
		}
		return comprimentoFormatado;
	}

	public String getDescricao() {
		return gerarDescricaoItem(true, false, true);
	}

	public String getDescricaoItemMaterial() {
		return gerarDescricaoItem(true, true, true);
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

	public String getDescricaoSemFormatacao() {
		return gerarDescricaoItem(false, false, false);
	}

	public abstract FormaMaterial getFormaMaterial();

	public abstract Integer getId();

	public abstract Material getMaterial();

	public abstract Double getMedidaExterna();

	public String getMedidaExternaFomatada() {
		if (medidaExternaFomatada == null) {
			return null;
		}
		return medidaExternaFomatada;
	}

	public abstract Double getMedidaInterna();

	public String getMedidaInternaFomatada() {
		if (medidaInternaFomatada == null) {
			return null;
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

	public double getValorICMS() {
		return calcularValorICMS();
	}

	public String getValorICMSFormatado() {
		return valorICMSFormatado;
	}

	public String getValorIPIFormatado() {
		return valorIPIFormatado;
	}

	public String getValorTotalFormatado() {
		return valorTotalFormatado;
	}

	public boolean isEqual(Item item) {
		boolean igual = false;

		igual = (getFormaMaterial() == null && item.getFormaMaterial() == null)
				|| (getFormaMaterial().equals(item.getFormaMaterial()));

		igual &= (getMaterial() == null && item.getMaterial() == null)
				|| (getMaterial().getId() == null && item.getMaterial().getId() == null)
				|| (getMaterial().getId().equals(item.getMaterial().getId()));

		igual &= (getMedidaExterna() == null && item.getMedidaExterna() == null)
				|| (getMedidaExterna() != null && getMedidaExterna().equals(item.getMedidaExterna()));

		igual &= (getMedidaInterna() == null && item.getMedidaInterna() == null)
				|| (getMedidaInterna() != null && getMedidaInterna().equals(item.getMedidaInterna()));

		igual &= (getComprimento() == null && item.getComprimento() == null)
				|| (getComprimento() != null && getComprimento().equals(item.getComprimento()));

		return igual;
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

	public void setValorICMSFormatado(String valorICMSFormatado) {
		this.valorICMSFormatado = valorICMSFormatado;
	}

	public void setValorIPIFormatado(String valorIPIFormatado) {
		this.valorIPIFormatado = valorIPIFormatado;
	}

	public void setValorTotalFormatado(String valorTotalFormatado) {
		this.valorTotalFormatado = valorTotalFormatado;
	}
}
