package br.com.plastecno.service.nfe;

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

	public COFINSGeral getTipoCofins() {
		return tipoCofins;
	}

	public void setTipoCofins(COFINSGeral tipoCofins) {
		TipoTributacaoCOFINS tribut = tipoCofins.getTipoTributacao();

		if (COFINS_1.equals(tribut) || COFINS_2.equals(tribut)) {
			cofinsAliquota = tipoCofins;
		} else if (COFINS_3.equals(tribut)) {
			cofinsQuantidade = tipoCofins;
		} else if (COFINS_4.equals(tribut) || COFINS_6.equals(tribut)
				|| COFINS_7.equals(tribut) || COFINS_8.equals(tribut)
				|| COFINS_9.equals(tribut)) {
			cofinsNaoTributado = tipoCofins;
		} else if (COFINS_5.equals(tribut)) {
			cofinsST = tipoCofins;
		} else if (COFINS_99.equals(tribut)) {
			cofinsOutrasOperacoes = tipoCofins;
		} else if (COFINS_ST.equals(tribut)) {
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
