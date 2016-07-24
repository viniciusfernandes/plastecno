package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class DetalhamentoProdutoServicoNFe {
	@XmlAttribute(name = "nItem")
	private Integer numeroItem;

	@XmlElement(name = "prod")
	private ProdutoServicoNFe produtoServicoNFe;

	@XmlElement(name = "imposto")
	private TributosProdutoServico tributosProdutoServico;

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}

	public void setProdutoServicoNFe(ProdutoServicoNFe produtoServicoNFe) {
		this.produtoServicoNFe = produtoServicoNFe;
	}

	/*
	 * Metodo criado apenas para simplificar e abreviar a marcacao dos .jsp
	 */
	public void setTributos(TributosProdutoServico tributos) {
		setTributosProdutoServico(tributos);
	}

	public void setTributosProdutoServico(
			TributosProdutoServico tributosProdutoServico) {
		this.tributosProdutoServico = tributosProdutoServico;
	}
}
