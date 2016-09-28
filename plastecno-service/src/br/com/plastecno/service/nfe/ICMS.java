package br.com.plastecno.service.nfe;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
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

	@XmlElement(name = "ICMSUFDest")
	@InformacaoValidavel(cascata = true, nomeExibicao = "ICMS interestadual")
	private ICMSInterestadual icmsInterestadual;

	@XmlElement(name = "ICMSPart")
	private ICMSGeral icmsPART;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Tipo ICMS")
	@XmlTransient
	private ICMSGeral tipoIcms;

	@XmlTransient
	public ICMSInterestadual getIcmsInterestadual() {
		return icmsInterestadual;
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
				if ((conteudo = campo.get(this)) == null) {
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

	public void setIcmsInterestadual(ICMSInterestadual icmsInterestadual) {
		this.icmsInterestadual = icmsInterestadual;
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