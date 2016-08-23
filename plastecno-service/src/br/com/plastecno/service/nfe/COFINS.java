package br.com.plastecno.service.nfe;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.*;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.plastecno.service.validacao.Validavel;

public class COFINS implements Validavel {
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
	private COFINSGeral tipoCofins;

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
		} else if (COFINS_99.equals(t)) {
			cofinsOutrasOperacoes = tipoCofins;
		} else if (COFINS_ST.equals(t)) {
			cofinsST = tipoCofins;
		}
		this.tipoCofins = tipoCofins;
	}

	@Override
	public void validar() throws BusinessException {
		if (tipoCofins == null) {
			throw new BusinessException("Tipo de COFINS é obrigatório");
		}

		tipoCofins.validar();
	}
}
