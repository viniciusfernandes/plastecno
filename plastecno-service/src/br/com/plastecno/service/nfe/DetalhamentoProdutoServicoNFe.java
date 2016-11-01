package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel(campoIdentificacao = "numeroItem")
public class DetalhamentoProdutoServicoNFe {
	@InformacaoValidavel(tamanho = 36, nomeExibicao = "FCI de produtos/servi�os")
	@XmlElement(name = "nFCI")
	private String fichaConteudoImportacao;

	// Campos criado para utilizar a indexacao dos itens da lista no arquivo
	// .jsp
	@XmlTransient
	private Integer indiceItem;

	@InformacaoValidavel(intervaloComprimento = { 1, 500 }, nomeExibicao = "Informa��es adicionais de produtos/servi�os")
	@XmlElement(name = "infAdProd")
	private String informacoesAdicionais;

	@InformacaoValidavel(padrao = "\\d{6}", padraoExemplo = "6 digitos", prefixo = "0", tamanho = 6, nomeExibicao = "Item de pedido de compra de produtos/servi�os")
	@XmlElement(name = "nItemPed")
	private String itemPedidoCompra;

	@InformacaoValidavel(iteravel = true, nomeExibicao = "Declara��o de exporta��o do produto/servi�o")
	@XmlElement(name = "detExport")
	private List<DeclaracaoExportacao> listaDeclaracaoExportacao;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 990 }, nomeExibicao = "N�mero de produtos/servi�os")
	@XmlAttribute(name = "nItem")
	private Integer numeroItem;

	@InformacaoValidavel(intervaloComprimento = { 1, 15 }, nomeExibicao = "N�mero de pedido de compra de produtos/servi�os")
	@XmlElement(name = "xPed")
	private String numeroPedidoCompra;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Produto/servi�o")
	@XmlElement(name = "prod")
	private ProdutoServicoNFe produtoServicoNFe;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Tributos do produtos/servi�os")
	@XmlElement(name = "imposto")
	private TributosProdutoServico tributosProdutoServico;

	public boolean contemICMS() {
		return tributosProdutoServico != null && tributosProdutoServico.contemICMS();
	}

	public boolean contemIPI() {
		return tributosProdutoServico != null && tributosProdutoServico.contemIPI();
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
	public List<DeclaracaoExportacao> getListaExportacao() {
		return getListaDeclaracaoExportacao();
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

	public void setListaExportacao(List<DeclaracaoExportacao> listaDeclaracaoExportacao) {
		setListaDeclaracaoExportacao(listaDeclaracaoExportacao);
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
