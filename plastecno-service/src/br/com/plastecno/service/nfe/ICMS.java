package br.com.plastecno.service.nfe;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;

public class ICMS {
	@XmlElement(name = "ICMS00")
	private ICMSGeral icms00;

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

	/*
	 * Metodo criado apenas para simplificar e abreviar a marcacao dos .jsp
	 */
	public void setTipoIcms(ICMSGeral icms) {
		if (icms == null) {
			return;
		}
		Field campo = null;
		try {
			campo = this.getClass().getDeclaredField("icms" + icms.getTributacaoICMS());
			campo.setAccessible(true);
			campo.set(this, icms);
		} catch (Exception e) {
			throw new RuntimeException(
					"Falha no atribuicao dos valores do ICMS com o tipo de tributacao \""
							+ icms.getTributacaoICMS() + "\"", e);
		} finally {
			if (campo != null) {
				campo.setAccessible(false);
			}
		}
	}
}