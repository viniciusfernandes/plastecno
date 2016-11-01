package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "identificacaoNFe", "identificacaoEmitenteNFe", "identificacaoDestinatarioNFe",
		"identificacaoLocalRetirada", "identificacaoLocalEntrega", "listaDetalhamentoProdutoServicoNFe",
		"valoresTotaisNFe", "transporteNFe", "cobrancaNFe", "informacoesAdicionaisNFe", "exportacaoNFe", "compraNFe" })
public class DadosNFe {

	@InformacaoValidavel(cascata = true, nomeExibicao = "Cobrança da NFe")
	@XmlElement(name = "cobr")
	private CobrancaNFe cobrancaNFe;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Compra da NFe")
	@XmlElement(name = "compra")
	private CompraNFe compraNFe;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Exportação da NFe")
	@XmlElement(name = "exporta")
	private ExportacaoNFe exportacaoNFe;

	@InformacaoValidavel(obrigatorio = true, tamanho = 47, nomeExibicao = "Id da NFe")
	@XmlAttribute(name = "Id")
	private String id;

	@XmlElement(name = "dest")
	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Identificação do destinatário")
	private IdentificacaoDestinatarioNFe identificacaoDestinatarioNFe;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Identificação do emitente")
	@XmlElement(name = "emit")
	private IdentificacaoEmitenteNFe identificacaoEmitenteNFe;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Local de entrega do produto")
	@XmlElement(name = "entrega")
	private IdentificacaoLocalGeral identificacaoLocalEntrega;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Local de retirada do produto")
	@XmlElement(name = "retirada")
	private IdentificacaoLocalGeral identificacaoLocalRetirada;

	@XmlElement(name = "ide")
	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Dados de Identificação da NFe")
	private IdentificacaoNFe identificacaoNFe;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Informações adicionais da NFe")
	@XmlElement(name = "infAdic")
	private InformacoesAdicionaisNFe informacoesAdicionaisNFe;

	@InformacaoValidavel(obrigatorio = true, iteravel = true, nomeExibicao = "Lista de produtos/serviços")
	@XmlElement(name = "det")
	private List<DetalhamentoProdutoServicoNFe> listaDetalhamentoProdutoServicoNFe;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Transporte da NFe")
	@XmlElement(name = "transp")
	private TransporteNFe transporteNFe;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Valores totais da NFe")
	@XmlElement(name = "total")
	private ValoresTotaisNFe valoresTotaisNFe;

	@XmlAttribute(name = "versao")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Versão da NFe")
	private final Double versao = 3.10d;

	public boolean contemDuplicata() {
		return cobrancaNFe != null && cobrancaNFe.contemDuplicata();
	}

	@XmlTransient
	public CobrancaNFe getCobrancaNFe() {
		return cobrancaNFe;
	}

	@XmlTransient
	public CompraNFe getCompraNFe() {
		return compraNFe;
	}

	@XmlTransient
	public ExportacaoNFe getExportacaoNFe() {
		return exportacaoNFe;
	}

	@XmlTransient
	public String getId() {
		return id;
	}

	@XmlTransient
	public IdentificacaoDestinatarioNFe getIdentificacaoDestinatarioNFe() {
		return identificacaoDestinatarioNFe;
	}

	@XmlTransient
	public IdentificacaoEmitenteNFe getIdentificacaoEmitenteNFe() {
		return identificacaoEmitenteNFe;
	}

	@XmlTransient
	public IdentificacaoLocalGeral getIdentificacaoLocalEntrega() {
		return identificacaoLocalEntrega;
	}

	@XmlTransient
	public IdentificacaoLocalGeral getIdentificacaoLocalRetirada() {
		return identificacaoLocalRetirada;
	}

	@XmlTransient
	public IdentificacaoNFe getIdentificacaoNFe() {
		return identificacaoNFe;
	}

	@XmlTransient
	public InformacoesAdicionaisNFe getInformacoesAdicionaisNFe() {
		return informacoesAdicionaisNFe;
	}

	@XmlTransient
	public List<DetalhamentoProdutoServicoNFe> getListaDetalhamentoProdutoServicoNFe() {
		return listaDetalhamentoProdutoServicoNFe;
	}

	@XmlTransient
	public List<DuplicataNFe> getListaDuplicata() {
		return cobrancaNFe != null ? cobrancaNFe.getListaDuplicata() : null;
	}

	@XmlTransient
	public List<DetalhamentoProdutoServicoNFe> getListaItem() {
		return listaDetalhamentoProdutoServicoNFe;
	}

	@XmlTransient
	public TransporteNFe getTransporteNFe() {
		return transporteNFe;
	}

	@XmlTransient
	public ValoresTotaisNFe getValoresTotaisNFe() {
		return valoresTotaisNFe;
	}

	@XmlTransient
	public Double getVersao() {
		return versao;
	}

	public void setCobrancaNFe(CobrancaNFe cobrancaNFe) {
		this.cobrancaNFe = cobrancaNFe;
	}

	public void setCompraNFe(CompraNFe compraNFe) {
		this.compraNFe = compraNFe;
	}

	/*
	 * Metodo criado apenas para abreviar as marcacoes nos .jsp
	 */
	public void setDestinatario(IdentificacaoDestinatarioNFe destinatario) {
		setIdentificacaoDestinatarioNFe(destinatario);
	}

	public void setExportacaoNFe(ExportacaoNFe exportacaoNFe) {
		this.exportacaoNFe = exportacaoNFe;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdentificacaoDestinatarioNFe(IdentificacaoDestinatarioNFe identificacaoDestinatarioNFe) {
		this.identificacaoDestinatarioNFe = identificacaoDestinatarioNFe;
	}

	public void setIdentificacaoEmitenteNFe(IdentificacaoEmitenteNFe identificacaoEmitenteNFe) {
		this.identificacaoEmitenteNFe = identificacaoEmitenteNFe;
	}

	public void setIdentificacaoLocalEntrega(IdentificacaoLocalGeral identificacaoLocalEntrega) {
		this.identificacaoLocalEntrega = identificacaoLocalEntrega;
	}

	public void setIdentificacaoLocalRetirada(IdentificacaoLocalGeral identificacaoLocalRetirada) {
		this.identificacaoLocalRetirada = identificacaoLocalRetirada;
	}

	public void setIdentificacaoNFe(IdentificacaoNFe identificacaoNFe) {
		this.identificacaoNFe = identificacaoNFe;
	}

	public void setInformacoesAdicionaisNFe(InformacoesAdicionaisNFe informacoesAdicionaisNFe) {
		this.informacoesAdicionaisNFe = informacoesAdicionaisNFe;
	}

	public void setListaDetalhamentoProdutoServicoNFe(
			List<DetalhamentoProdutoServicoNFe> listaDetalhamentoProdutoServicoNFe) {
		this.listaDetalhamentoProdutoServicoNFe = listaDetalhamentoProdutoServicoNFe;
	}

	/*
	 * Metodo criado apenas para simplificar a marcacao no .jsp
	 */
	public void setListaItem(List<DetalhamentoProdutoServicoNFe> listaItem) {
		setListaDetalhamentoProdutoServicoNFe(listaItem);
	}

	public void setTransporteNFe(TransporteNFe transporteNFe) {
		this.transporteNFe = transporteNFe;
	}

	public void setValoresTotaisNFe(ValoresTotaisNFe valoresTotaisNFe) {
		this.valoresTotaisNFe = valoresTotaisNFe;
	}
}
