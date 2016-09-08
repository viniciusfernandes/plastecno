package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class DetalhamentoProdutoServicoNFe {
	@InformacaoValidavel(tamanho = 36, nomeExibicao = "FCI de produtos/serviços")
	@XmlElement(name = "nFCI")
	private String fichaConteudoImportacao;

	// Campos criado para utilizar a indexacao dos itens da lista no arquivo
	// .jsp
	@XmlTransient
	private Integer indiceItem;

	@InformacaoValidavel(intervaloComprimento = { 1, 500 }, nomeExibicao = "Informações adicionais de produtos/serviços")
	@XmlElement(name = "infAdProd")
	private String informacoesAdicionais;

	@InformacaoValidavel(padrao = "\\d{6}", nomeExibicao = "Item de pedido de compra de produtos/serviços")
	@XmlElement(name = "nItemPed")
	private String itemPedidoCompra;

	@InformacaoValidavel(iteravel = true, nomeExibicao = "Declaração de exportação do produto/serviço")
	@XmlElement(name = "detExport")
	private List<DeclaracaoExportacao> listaDeclaracaoExportacao;

	@InformacaoValidavel(iteravel = true, nomeExibicao = "Declaração de importação do produto/serviço")
	@XmlElement(name = "DI")
	private List<DeclaracaoImportacao> listaDeclaracaoImportacao;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 990 }, nomeExibicao = "Número de produtos/serviços")
	@XmlAttribute(name = "nItem")
	private Integer numeroItem;

	@InformacaoValidavel(intervaloComprimento = { 1, 15 }, nomeExibicao = "Número de pedido de compra de produtos/serviços")
	@XmlElement(name = "xPed")
	private String numeroPedidoCompra;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Produto/serviço")
	@XmlElement(name = "prod")
	private ProdutoServicoNFe produtoServicoNFe;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Tributos do produtos/serviços")
	@XmlElement(name = "imposto")
	private TributosProdutoServico tributosProdutoServico;

	public boolean contemICMS() {
		return tributosProdutoServico != null && tributosProdutoServico.contemICMS();
	}

	@XmlTransient
	public String getFichaConteudoImportacao() {
		return fichaConteudoImportacao;
	}

	// Devemos fazer esse tratamento do indice do item pois ele esta sendo
	// recuperado na pesquisa pelo numero do pedido e ele nao foi populado no
	// banco de dados pois nao esta no xml
	@XmlTransient
	public Integer getIndiceItem() {
		if (indiceItem == null && numeroItem != null) {
			indiceItem = numeroItem - 1;
		}
		return indiceItem;
	}

	@XmlTransient
	public String getInformacoesAdicionais() {
		return informacoesAdicionais;
	}

	@XmlTransient
	public String getItemPedidoCompra() {
		return itemPedidoCompra;
	}

	@XmlTransient
	public List<DeclaracaoExportacao> getListaDeclaracaoExportacao() {
		return listaDeclaracaoExportacao;
	}

	@XmlTransient
	public List<DeclaracaoImportacao> getListaDeclaracaoImportacao() {
		return listaDeclaracaoImportacao;
	}

	@XmlTransient
	public List<DeclaracaoExportacao> getListaExportacao() {
		return getListaDeclaracaoExportacao();
	}

	// Metodo criado para simplificar a marcacao no .jsp
	@XmlTransient
	public List<DeclaracaoImportacao> getListaImportacao() {
		return getListaDeclaracaoImportacao();
	}

	@XmlTransient
	public Integer getNumeroItem() {
		return numeroItem;
	}

	@XmlTransient
	public String getNumeroPedidoCompra() {
		return numeroPedidoCompra;
	}

	/*
	 * Metodo criado apenas para simplificar e abreviar a marcacao dos .jsp
	 */
	@XmlTransient
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
	@XmlTransient
	public TributosProdutoServico getTributos() {
		return tributosProdutoServico;
	}

	@XmlTransient
	public TributosProdutoServico getTributosProdutoServico() {
		return tributosProdutoServico;
	}

	public void setFichaConteudoImportacao(String fichaConteudoImportacao) {
		this.fichaConteudoImportacao = fichaConteudoImportacao;
	}

	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

	public void setItemPedidoCompra(String itemPedidoCompra) {
		this.itemPedidoCompra = itemPedidoCompra;
	}

	public void setListaDeclaracaoExportacao(List<DeclaracaoExportacao> listaDeclaracaoExportacao) {
		this.listaDeclaracaoExportacao = listaDeclaracaoExportacao;
	}

	public void setListaDeclaracaoImportacao(List<DeclaracaoImportacao> listaDeclaracaoImportacao) {
		this.listaDeclaracaoImportacao = listaDeclaracaoImportacao;
	}

	public void setListaExportacao(List<DeclaracaoExportacao> listaDeclaracaoExportacao) {
		setListaDeclaracaoExportacao(listaDeclaracaoExportacao);
	}

	// Metodo criado para simplificar marcacao do .jsp
	public void setListaImportacao(List<DeclaracaoImportacao> listaDeclaracaoImportacao) {
		this.setListaDeclaracaoImportacao(listaDeclaracaoImportacao);
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
		if (numeroItem != null) {
			indiceItem = numeroItem - 1;
		}
	}

	public void setNumeroPedidoCompra(String numeroPedidoCompra) {
		this.numeroPedidoCompra = numeroPedidoCompra;
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

	public void setTributosProdutoServico(TributosProdutoServico tributosProdutoServico) {
		this.tributosProdutoServico = tributosProdutoServico;
	}
}
