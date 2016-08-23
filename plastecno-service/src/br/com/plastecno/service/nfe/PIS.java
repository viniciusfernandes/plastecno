package br.com.plastecno.service.nfe;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.*;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
import br.com.plastecno.service.validacao.Validavel;

public class PIS implements Validavel {

	@XmlElement(name = "PISAliq")
	private PISGeral pisAliquota;

	@XmlElement(name = "PISNT")
	private PISGeral pisNaoTributado;

	@XmlElement(name = "PISOutr")
	private PISGeral pisOutrasOperacoes;

	@XmlElement(name = "PISQtde")
	private PISGeral pisQuantidade;

	@XmlElement(name = "PISST")
	private PISGeral pisST;

	@XmlTransient
	private PISGeral tipoPis;

	@XmlTransient
	public PISGeral getTipoPis() {
		if (tipoPis == null) {
			recuperarTipoPis();
		}
		return tipoPis;
	}

	private void recuperarTipoPis() {
		Field[] campos = this.getClass().getDeclaredFields();
		Object conteudo = null;
		for (Field campo : campos) {
			campo.setAccessible(true);
			try {
				if ((conteudo = campo.get(this)) == null) {
					campo.setAccessible(false);
					continue;
				}

				setTipoPis((PISGeral) conteudo);

			} catch (Exception e) {
				throw new IllegalArgumentException("Nao foi possivel gerar o tipo de PIS a partir do xml do servidor",
						e);
			} finally {
				campo.setAccessible(false);
			}
		}
	}

	public void setTipoPis(PISGeral tipoPis) {
		if (tipoPis == null) {
			this.tipoPis = null;
			return;
		}

		TipoTributacaoPIS t = tipoPis.getTipoTributacao();
		if (t == null) {
			this.tipoPis = null;
			return;
		}

		if (PIS_1.equals(t) || PIS_2.equals(t)) {
			this.pisAliquota = tipoPis;
		} else if (PIS_3.equals(t)) {
			this.pisQuantidade = tipoPis;
		} else if (PIS_4.equals(t) || PIS_6.equals(t) || PIS_7.equals(t) || PIS_8.equals(t) || PIS_9.equals(t)) {
			this.pisNaoTributado = tipoPis;
		} else if (PIS_99.equals(t)) {
			this.pisOutrasOperacoes = tipoPis;
		} else if (PIS_ST.equals(t)) {
			this.pisST = tipoPis;
		}
		this.tipoPis = tipoPis;
	}

	public void validar() throws BusinessException {
		if (tipoPis == null) {
			throw new BusinessException("Tipo de PIS é obrigatório");
		}

		tipoPis.validar();
	}
}
