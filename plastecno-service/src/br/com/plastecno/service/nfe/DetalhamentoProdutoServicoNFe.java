package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class DetalhamentoProdutoServicoNFe {
	@XmlAttribute(name = "nItem")
	private Integer numeroItem;
	
	@XmlElement(name="prod")
	private ProdutoServicoNFe produtoServicoNFe;

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}

	public void setProdutoServicoNFe(ProdutoServicoNFe produtoServicoNFe) {
		this.produtoServicoNFe = produtoServicoNFe;
	}
	
}
