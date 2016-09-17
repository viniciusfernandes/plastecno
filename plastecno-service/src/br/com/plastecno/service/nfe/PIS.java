package br.com.plastecno.service.nfe;

import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_1;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_2;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_3;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_4;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_6;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_7;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_8;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_9;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_99;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.PIS_ST;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class PIS {

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
	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Tipo PIS")
	private PISGeral tipoPis;

	public void configurarSubstituicaoTributaria() {
		if (tipoPis == null) {
			return;
		}
		if (TipoTributacaoPIS.PIS_ST.equals(tipoPis.getTipoTributacao())) {
			tipoPis.setCodigoSituacaoTributaria(null);
		}
	}

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
}
