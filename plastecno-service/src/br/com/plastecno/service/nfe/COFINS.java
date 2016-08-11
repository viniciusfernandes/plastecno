package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import static br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS.*;
import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;

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
	private COFINSGeral tipoConfins;

	public COFINSGeral getTipoConfins() {
		return tipoConfins;
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
		}
		this.tipoConfins = tipoCofins;
	}

	public void setTipoConfins(COFINSGeral tipoConfins) {
		this.tipoConfins = tipoConfins;
	}
}
