package br.com.plastecno.service.nfe;

import java.util.Date;
import java.util.List;

public class NFe {
	private Integer consumidorFinal;
	private Date dataHoraEmissao;
	private Date dataHoraSaidaEntrada;
	private DestinatarioNFe destinatarioNFe;
	private Integer destinoOperacao;
	private EmitenteNFe emitenteNFe;
	private Integer finalidadeEmissao;
	private String formaEmissao;
	private Integer formaPagamento;
	private List<DetalhamentoProdutoServicoNFe> listaDetalhamentoProdutoServicoNFe;
	private Integer modelo;
	private String municipioOcorrencia;
	private Integer serie;
	private Integer tipoAtendimento;
	private Integer tipoDocumento;
	private String tipoImpressao;
	private String UF;

	public Integer getConsumidorFinal() {
		return consumidorFinal;
	}

	public Date getDataHoraEmissao() {
		return dataHoraEmissao;
	}

	public Date getDataHoraSaidaEntrada() {
		return dataHoraSaidaEntrada;
	}

	public DestinatarioNFe getDestinatarioNFe() {
		return destinatarioNFe;
	}

	public Integer getDestinoOperacao() {
		return destinoOperacao;
	}

	public EmitenteNFe getEmitenteNFe() {
		return emitenteNFe;
	}

	public Integer getFinalidadeEmissao() {
		return finalidadeEmissao;
	}

	public String getFormaEmissao() {
		return formaEmissao;
	}

	public Integer getFormaPagamento() {
		return formaPagamento;
	}

	public Integer getModelo() {
		return modelo;
	}

	public String getMunicipioOcorrencia() {
		return municipioOcorrencia;
	}

	public Integer getSerie() {
		return serie;
	}

	public Integer getTipoAtendimento() {
		return tipoAtendimento;
	}

	public Integer getTipoDocumento() {
		return tipoDocumento;
	}

	public String getTipoImpressao() {
		return tipoImpressao;
	}

	public String getUF() {
		return UF;
	}

	public void setConsumidorFinal(Integer consumidorFinal) {
		this.consumidorFinal = consumidorFinal;
	}

	public void setDataHoraEmissao(Date dataHoraEmissao) {
		this.dataHoraEmissao = dataHoraEmissao;
	}

	public void setDataHoraSaidaEntrada(Date dataHoraSaidaEntrada) {
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
