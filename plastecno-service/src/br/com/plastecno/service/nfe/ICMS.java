package br.com.plastecno.service.nfe;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ICMS {
	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS00 do produto/serviço")
	@XmlElement(name = "ICMS00")
	private ICMSGeral icms00;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS10 do produto/serviço")
	@XmlElement(name = "ICMS10")
	private ICMSGeral icms10;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS20 do produto/serviço")
	@XmlElement(name = "ICMS20")
	private ICMSGeral icms20;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS30 do produto/serviço")
	@XmlElement(name = "ICMS30")
	private ICMSGeral icms30;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS40 do produto/serviço")
	@XmlElement(name = "ICMS40")
	private ICMSGeral icms40;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS51 do produto/serviço")
	@XmlElement(name = "ICMS51")
	private ICMSGeral icms51;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS60 do produto/serviço")
	@XmlElement(name = "ICMS60")
	private ICMSGeral icms60;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS70 do produto/serviço")
	@XmlElement(name = "ICMS70")
	private ICMSGeral icms70;

	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS90 do produto/serviço")
	@XmlElement(name = "ICMS90")
	private ICMSGeral icms90;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo ICMS do produto/serviço")
	@XmlTransient
	private ICMSGeral tipoIcms;

	@XmlTransient
	public ICMSGeral getTipoIcms() {
		return tipoIcms;
	}

	/*
	 * Metodo criado apenas para simplificar e abreviar a marcacao dos .jsp
	 */
	public void setTipoIcms(ICMSGeral tipoIcms) {
		if (tipoIcms == null) {
			return;
		}
		Field campo = null;
		try {
			TipoTributacaoICMS tribut = tipoIcms.getTipoTributacao();
			campo = this.getClass().getDeclaredField(
					"icms" + (tribut != null ? tribut.getCodigo() : null));
			campo.setAccessible(true);
			campo.set(this, tipoIcms);
		} catch (Exception e) {
			throw new RuntimeException(
					"Falha no atribuicao dos valores do ICMS com o tipo de tributacao \""
							+ tipoIcms.getTipoTributacao() + "\"", e);
		} finally {
			if (campo != null) {
				campo.setAccessible(false);
			}
			this.tipoIcms = tipoIcms;
		}
	}

	public void validar() throws BusinessException {
		tipoIcms.validar();
	}
}