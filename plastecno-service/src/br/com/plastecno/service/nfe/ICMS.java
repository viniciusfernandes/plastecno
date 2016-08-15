package br.com.plastecno.service.nfe;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ICMS {
	@XmlElement(name = "ICMS00")
	private ICMSGeral icms0;

	@XmlElement(name = "ICMS10")
	private ICMSGeral icms10;

	@XmlElement(name = "ICMS20")
	private ICMSGeral icms20;

	@XmlElement(name = "ICMS30")
	private ICMSGeral icms30;

	@XmlElement(name = "ICMS40")
	private ICMSGeral icms40;

	@XmlElement(name = "ICMS51")
	private ICMSGeral icms51;

	@XmlElement(name = "ICMS60")
	private ICMSGeral icms60;

	@XmlElement(name = "ICMS70")
	private ICMSGeral icms70;

	@XmlElement(name = "ICMS90")
	private ICMSGeral icms90;

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
}