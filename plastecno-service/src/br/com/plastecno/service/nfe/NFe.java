package br.com.plastecno.service.nfe;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "nfe")
public class NFe {
	private Integer consumidorFinal;
	private String dataHoraEmissao;
	private String dataHoraSaidaEntrada;
	private DestinatarioNFe destinatarioNFe;
	private Integer destinoOperacao;
	private EmitenteNFe emitenteNFe;
	private Integer finalidadeEmissao;
	private String formaEmissao;
	private Integer formaPagamento;
	@XmlAttribute(name = "Id")
	private String id;
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

	private Integer modelo;

	private String municipioOcorrencia;

	private Integer serie;

	private Integer tipoAtendimento;

	private Integer tipoDocumento;

	private String tipoImpressao;
	@XmlElement(name = "transp")
	private TransporteNFe transporteNFe;
	private String UF;
	@XmlElement(name = "total")
	private ValoresTotaisNFe valoresTotaisNFe;
	@XmlAttribute(name = "versao")
	private Double versao;
	public void addDetalhamentoProdutoServico(DetalhamentoProdutoServicoNFe detalhamento) {
		if (listaDetalhamentoProdutoServicoNFe == null) {
			listaDetalhamentoProdutoServicoNFe = new ArrayList<DetalhamentoProdutoServicoNFe>();
		}
		listaDetalhamentoProdutoServicoNFe.add(detalhamento);
	}
	public void setConsumidorFinal(Integer consumidorFinal) {
		this.consumidorFinal = consumidorFinal;
	}
	public void setDataHoraEmissao(String dataHoraEmissao) {
		this.dataHoraEmissao = dataHoraEmissao;
	}
	public void setDataHoraSaidaEntrada(String dataHoraSaidaEntrada) {
		this.dataHoraSaidaEntrada = dataHoraSaidaEntrada;
	}
	public void setDestinatarioNFe(DestinatarioNFe destinatarioNFe) {
		this.destinatarioNFe = destinatarioNFe;
	}
	public void setDestinoOperacao(Integer destinoOperacao) {
		this.destinoOperacao = destinoOperacao;
	}
	public void setEmitenteNFe(EmitenteNFe emitenteNFe) {
		this.emitenteNFe = emitenteNFe;
	}
	public void setFinalidadeEmissao(Integer finalidadeEmissao) {
		this.finalidadeEmissao = finalidadeEmissao;
	}

	public void setFormaEmissao(String formaEmissao) {
		this.formaEmissao = formaEmissao;
	}

	public void setFormaPagamento(Integer formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdentificacaoNFe(IdentificacaoNFe identificacaoNFe) {
		this.identificacaoNFe = identificacaoNFe;
	}

	public void setInformacaoCompra(InformacaoCompra informacaoCompra) {
		this.informacaoCompra = informacaoCompra;
	}

	public void setInformacaoExportacao(InformacaoExportacao informacaoExportacao) {
		this.informacaoExportacao = informacaoExportacao;
	}

	public void setInformacoesAdicionaisNFe(InformacoesAdicionaisNFe informacoesAdicionaisNFe) {
		this.informacoesAdicionaisNFe = informacoesAdicionaisNFe;
	}

	public void setListaDetalhamentoProdutoServicoNFe(
			List<DetalhamentoProdutoServicoNFe> listaDetalhamentoProdutoServicoNFe) {
		this.listaDetalhamentoProdutoServicoNFe = listaDetalhamentoProdutoServicoNFe;
	}

	public void setModelo(Integer modelo) {
		this.modelo = modelo;
	}

	public void setMunicipioOcorrencia(String municipioOcorrencia) {
		this.municipioOcorrencia = municipioOcorrencia;
	}

	public void setSerie(Integer serie) {
		this.serie = serie;
	}

	public void setTipoAtendimento(Integer tipoAtendimento) {
		this.tipoAtendimento = tipoAtendimento;
	}

	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public void setTipoImpressao(String tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public void setTransporteNFe(TransporteNFe transporteNFe) {
		this.transporteNFe = transporteNFe;
	}

	public void setUF(String uF) {
		UF = uF;
	}

	public void setValoresTotaisNFe(ValoresTotaisNFe valoresTotaisNFe) {
		this.valoresTotaisNFe = valoresTotaisNFe;
	}

	public void setVersao(Double versao) {
		this.versao = versao;
	}

}
