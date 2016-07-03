package br.com.plastecno.service.nfe;

public class DetalhamentoProdutoServicoNFe {
	private Integer numeroItem;
	private ProdutoServicoNFe produtoServicoNFe;

	public Integer getNumeroItem() {
		return numeroItem;
	}

	public ProdutoServicoNFe getProdutoServicoNFe() {
		return produtoServicoNFe;
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}

	public void setProdutoServicoNFe(ProdutoServicoNFe produtoServicoNFe) {
		this.produtoServicoNFe = produtoServicoNFe;
	}
}
