package br.com.plastecno.service.nfe;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "nfe")
public class NFe {
	@XmlElement(name = "gen")
	private Integer consumidorFinal;
	private String dataHoraEmissao;
	private String dataHoraSaidaEntrada;
	private DestinatarioNFe destinatarioNFe;
	private Integer destinoOperacao;
	private EmitenteNFe emitenteNFe;
	private Integer finalidadeEmissao;
	private String formaEmissao;
	private Integer formaPagamento;
	@XmlElement(name = "det")
	private List<DetalhamentoProdutoServicoNFe> listaDetalhamentoProdutoServicoNFe;
	private Integer modelo;
	private String municipioOcorrencia;
	private Integer serie;
	private Integer tipoAtendimento;
	private Integer tipoDocumento;
	private String tipoImpressao;
	private String UF;
	
	@XmlElement(name="total")
	private ValorTotalNFe valorTotalNFe;

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

	public void setUF(String uF) {
		UF = uF;
	}

}
