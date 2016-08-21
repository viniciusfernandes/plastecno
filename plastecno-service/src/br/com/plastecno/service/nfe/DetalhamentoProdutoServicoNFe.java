package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class DetalhamentoProdutoServicoNFe {
	// Campos criado para utilizar a indexacao dos itens da lista no arquivo
	// .jsp
	@XmlTransient
	private Integer indiceItem;

	@InformacaoValidavel(intervaloComprimento = { 1, 500 }, nomeExibicao = "Informações adicionais de produtos/serviços")
	@XmlElement(name = "infAdProd")
	private String informacoesAdicionais;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 990 }, nomeExibicao = "Número de produtos/serviços")
	@XmlAttribute(name = "nItem")
	private Integer numeroItem;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Produto/serviço")
	@XmlElement(name = "prod")
	private ProdutoServicoNFe produtoServicoNFe;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Tributos do produtos/serviços")
	@XmlElement(name = "imposto")
	private TributosProdutoServico tributosProdutoServico;

	public boolean contemICMS() {
		return tributosProdutoServico != null
				&& tributosProdutoServico.contemICMS();
	}

	@XmlTransient
	public Integer getIndiceItem() {
		return indiceItem;
	}

	@XmlTransient
	public String getInformacoesAdicionais() {
		return informacoesAdicionais;
	}

	@XmlTransient
	public Integer getNumeroItem() {
		return numeroItem;
	}

	/*
	 * Metodo criado apenas para simplificar e abreviar a marcacao dos .jsp
	 */
	public ProdutoServicoNFe getProduto() {
		return produtoServicoNFe;
	}

	@XmlTransient
	public ProdutoServicoNFe getProdutoServicoNFe() {
		return produtoServicoNFe;
	}

	/*
	 * Metodo criado apenas para simplificar e abreviar a marcacao dos .jsp
	 */
	public TributosProdutoServico getTributos() {
		return tributosProdutoServico;
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
		if (numeroItem != null && numeroItem > 0) {
			indiceItem = numeroItem - 1;
		}
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
