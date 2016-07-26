package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class PIS {

	@XmlElement(name = "PISAliq")
	private PISGeral pisAliquota;

	@XmlElement(name = "PISNT")
	private PISGeral pisNaoTributado;

	@XmlElement(name = "PISOutr")
	private PISGeral pisOutrasOperacoes;

	@XmlElement(name = "PISQtde")
	private PISGeral pisQuantidade;

	public void setTipoPis(PISGeral tipoPis) {
		if (tipoPis == null || tipoPis.getCodigoSituacaoTributaria() == null) {
			return;
		}

		Integer codigo = tipoPis.getCodigoSituacaoTributaria();
		if (codigo.equals(1) || codigo.equals(2)) {
			this.pisAliquota = tipoPis;
		} else if (codigo.equals(3)) {
			this.pisQuantidade = tipoPis;
		} else if (codigo.equals(4) || codigo.equals(6) || codigo.equals(7)
				|| codigo.equals(8) || codigo.equals(9)) {
			this.pisNaoTributado = tipoPis;
		} else if (codigo.equals(99)) {
			this.pisOutrasOperacoes = tipoPis;
		}
	}

}
