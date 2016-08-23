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
			return;
		}

		TipoTributacaoPIS tribut = tipoPis.getTipoTributacao();
		if (PIS_1.equals(tribut) || PIS_2.equals(tribut)) {
			this.pisAliquota = tipoPis;
		} else if (PIS_3.equals(tribut)) {
			this.pisQuantidade = tipoPis;
		} else if (PIS_4.equals(tribut) || PIS_6.equals(tribut) || PIS_7.equals(tribut) || PIS_8.equals(tribut)
				|| PIS_9.equals(tribut)) {
			this.pisNaoTributado = tipoPis;
		} else if (PIS_99.equals(tribut)) {
			this.pisOutrasOperacoes = tipoPis;
		} else if (PIS_ST.equals(tribut)) {
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
