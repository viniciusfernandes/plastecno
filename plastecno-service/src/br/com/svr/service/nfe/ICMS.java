package br.com.svr.service.nfe;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.nfe.constante.TipoTributacaoICMS;
import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "icms00", "icms10", "icms20", "icms30", "icms40", "icms41", "icms50", "icms51", "icms60",
		"icms70", "icms90", "icmsPART" })
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

	@XmlElement(name = "ICMS41")
	private ICMSGeral icms41;

	@XmlElement(name = "ICMS50")
	private ICMSGeral icms50;

	@XmlElement(name = "ICMS51")
	private ICMSGeral icms51;

	@XmlElement(name = "ICMS60")
	private ICMSGeral icms60;

	@XmlElement(name = "ICMS70")
	private ICMSGeral icms70;

	@XmlElement(name = "ICMS90")
	private ICMSGeral icms90;

	@XmlElement(name = "ICMSPart")
	private ICMSGeral icmsPART;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Tipo ICMS")
	@XmlTransient
	private ICMSGeral tipoIcms;

	public ICMS() {
	}

	public ICMS(ICMSGeral icmsGeral) {
		this.setTipoIcms(icmsGeral);
	}

	@XmlTransient
	public ICMSGeral getTipoIcms() {
		if (tipoIcms == null) {
			recuperarTipoIcms();
		}
		return tipoIcms;
	}

	private void recuperarTipoIcms() {
		Field[] campos = this.getClass().getDeclaredFields();
		Object conteudo = null;
		for (Field campo : campos) {
			campo.setAccessible(true);
			try {
				if ((conteudo = campo.get(this)) == null || !(conteudo instanceof ICMSGeral)) {
					campo.setAccessible(false);
					continue;
				}

				setTipoIcms((ICMSGeral) conteudo);

			} catch (Exception e) {
				throw new IllegalArgumentException("Nao foi possivel gerar o tipo de ICMS a partir do xml do servidor",
						e);
			} finally {
				campo.setAccessible(false);
			}
		}
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
			TipoTributacaoICMS t = tipoIcms.getTipoTributacao();
			if (t != null) {
				campo = this.getClass().getDeclaredField("icms" + t.getCodigo());
				campo.setAccessible(true);
				campo.set(this, tipoIcms);
				this.tipoIcms = tipoIcms;
			} else {
				this.tipoIcms = null;
			}
		} catch (Exception e) {
			throw new RuntimeException("Falha no atribuicao dos valores do ICMS com o tipo de tributacao \""
					+ tipoIcms.getTipoTributacao() + "\"", e);
		} finally {
			if (campo != null) {
				campo.setAccessible(false);
			}
		}
	}
}