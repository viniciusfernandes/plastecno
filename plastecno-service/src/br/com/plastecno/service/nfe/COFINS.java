package br.com.plastecno.service.nfe;

import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_1;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_2;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_3;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_4;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_5;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_6;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_7;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_8;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_9;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.COFINS_ST;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "cofinsAliquota", "cofinsQuantidade", "cofinsNaoTributado", "cofinsOutrasOperacoes", "cofinsST" })
public class COFINS {
	@XmlElement(name = "COFINSAliq")
	private COFINSGeral cofinsAliquota;

	@XmlElement(name = "COFINSNT")
	private COFINSGeral cofinsNaoTributado;

	@XmlElement(name = "COFINSOutr")
	private COFINSGeral cofinsOutrasOperacoes;

	@XmlElement(name = "COFINSQtde")
	private COFINSGeral cofinsQuantidade;

	@XmlElement(name = "COFINSST")
	private COFINSGeral cofinsST;

	@XmlTransient
	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Tipo COFINS")
	private COFINSGeral tipoCofins;

	public void configurarSubstituicaoTributaria() {
		if (tipoCofins == null) {
			return;
		}
		if (TipoTributacaoCOFINS.COFINS_ST.equals(tipoCofins.getTipoTributacao())) {
			tipoCofins.setCodigoSituacaoTributaria(null);
		}
	}

	@XmlTransient
	public COFINSGeral getTipoCofins() {
		if (tipoCofins == null) {
			recuperarTipoCofins();
		}
		return tipoCofins;
	}

	private void recuperarTipoCofins() {
		Field[] campos = this.getClass().getDeclaredFields();
		Object conteudo = null;
		for (Field campo : campos) {
			campo.setAccessible(true);
			try {
				if ((conteudo = campo.get(this)) == null) {
					campo.setAccessible(false);
					continue;
				}

				setTipoCofins((COFINSGeral) conteudo);

			} catch (Exception e) {
				throw new IllegalArgumentException(
						"Nao foi possivel gerar o tipo de COFINS a partir do xml do servidor", e);
			} finally {
				campo.setAccessible(false);
			}
		}
	}

	public void setTipoCofins(COFINSGeral tipoCofins) {
		if (tipoCofins == null) {
			this.tipoCofins = null;
			return;
		}
		TipoTributacaoCOFINS t = tipoCofins.getTipoTributacao();

		if (t == null) {
			this.tipoCofins = null;
			return;
		}

		if (COFINS_1.equals(t) || COFINS_2.equals(t)) {
			cofinsAliquota = tipoCofins;
		} else if (COFINS_3.equals(t)) {
			cofinsQuantidade = tipoCofins;
		} else if (COFINS_4.equals(t) || COFINS_6.equals(t) || COFINS_7.equals(t) || COFINS_8.equals(t)
				|| COFINS_9.equals(t)) {
			cofinsNaoTributado = tipoCofins;
		} else if (COFINS_5.equals(t)) {
			cofinsST = tipoCofins;
		} else if (COFINS_ST.equals(t)) {
			cofinsST = tipoCofins;
		} else {
			cofinsOutrasOperacoes = tipoCofins;
		}
		this.tipoCofins = tipoCofins;
	}
}
