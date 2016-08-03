package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class DetalhamentoProdutoServicoNFe {
	@XmlElement(name = "infAdProd")
	private String informacoesAdicionais;

	@XmlAttribute(name = "nItem")
	private Integer numeroItem;

	@XmlElement(name = "prod")
	private ProdutoServicoNFe produtoServicoNFe;

	@XmlElement(name = "imposto")
	private TributosProdutoServico tributosProdutoServico;

	public boolean contemICMS() {
		return tributosProdutoServico != null
				&& tributosProdutoServico.contemICMS();
	}

	@XmlTransient
	public ProdutoServicoNFe getProdutoServicoNFe() {
		return produtoServicoNFe;
	}

	@XmlTransient
	public TributosProdutoServico getTributosProdutoServico() {
		return tributosProdutoServico;
	}

	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

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
