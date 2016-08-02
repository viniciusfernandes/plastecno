package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "nfe")
public class NFe {
	@XmlElement(name = "cobr")
	private CobrancaNFe cobrancaNFe;

	@XmlAttribute(name = "Id")
	private String id;

	@XmlElement(name = "dest")
	private IdentificacaoDestinatarioNFe identificacaoDestinatarioNFe;

	@XmlElement(name = "emit")
	private IdentificacaoEmitenteNFe identificacaoEmitenteNFe;

	@XmlElement(name = "ide")
	private IdentificacaoNFe identificacaoNFe;

	@XmlElement(name = "compra")
	private InformacaoCompra informacaoCompra;

	@XmlElement(name = "exporta")
	private InformacaoExportacao informacaoExportacao;

	@XmlElement(name = "infAdic")
	private InformacoesAdicionaisNFe informacoesAdicionaisNFe;

	@XmlElement(name = "det")
	private List<DetalhamentoProdutoServicoNFe> listaDetalhamentoProdutoServicoNFe;

	@XmlElement(name = "NFref")
	private NFeRerefenciada nFeRerefenciada;

	@XmlElement(name = "transp")
	private TransporteNFe transporteNFe;

	@XmlElement(name = "total")
	private ValoresTotaisNFe valoresTotaisNFe;

	@XmlAttribute(name = "versao")
	private Double versao;

	

	@XmlTransient
	public IdentificacaoDestinatarioNFe getIdentificacaoDestinatarioNFe() {
		return identificacaoDestinatarioNFe;
	}

	@XmlTransient
	public List<DetalhamentoProdutoServicoNFe> getListaDetalhamentoProdutoServicoNFe() {
		return listaDetalhamentoProdutoServicoNFe;
	}

	@XmlTransient
	public ValoresTotaisNFe getValoresTotaisNFe() {
		return valoresTotaisNFe;
	}

	public void setCobrancaNFe(CobrancaNFe cobrancaNFe) {
		this.cobrancaNFe = cobrancaNFe;
	}

	/*
	 * Metodo criado apenas para abreviar as marcacoes nos .jsp
	 */
	public void setDestinatario(IdentificacaoDestinatarioNFe destinatario) {
		setIdentificacaoDestinatarioNFe(destinatario);
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdentificacaoDestinatarioNFe(
			IdentificacaoDestinatarioNFe identificacaoDestinatarioNFe) {
		this.identificacaoDestinatarioNFe = identificacaoDestinatarioNFe;
	}

	public void setIdentificacaoEmitenteNFe(
			IdentificacaoEmitenteNFe identificacaoEmitenteNFe) {
		this.identificacaoEmitenteNFe = identificacaoEmitenteNFe;
	}

	public void setIdentificacaoNFe(IdentificacaoNFe identificacaoNFe) {
		this.identificacaoNFe = identificacaoNFe;
	}

	public void setInformacaoCompra(InformacaoCompra informacaoCompra) {
		this.informacaoCompra = informacaoCompra;
	}

	public void setInformacaoExportacao(
			InformacaoExportacao informacaoExportacao) {
		this.informacaoExportacao = informacaoExportacao;
	}

	public void setInformacoesAdicionaisNFe(
			InformacoesAdicionaisNFe informacoesAdicionaisNFe) {
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

	public void setnFeRerefenciada(NFeRerefenciada nFeRerefenciada) {
		this.nFeRerefenciada = nFeRerefenciada;
	}

	public void setTransporteNFe(TransporteNFe transporteNFe) {
		this.transporteNFe = transporteNFe;
	}

	public void setValoresTotaisNFe(ValoresTotaisNFe valoresTotaisNFe) {
		this.valoresTotaisNFe = valoresTotaisNFe;
	}

	public void setVersao(Double versao) {
		this.versao = versao;
	}

}
