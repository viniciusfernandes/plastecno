package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import static br.com.plastecno.service.nfe.constante.TipoTributacaoPIS.*;
import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;

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
		if (tipoPis == null) {
			return;
		}

		TipoTributacaoPIS tribut = tipoPis.getTipoTributacao();
		if (PIS_1.equals(tribut) || PIS_2.equals(tribut)) {
			this.pisAliquota = tipoPis;
		} else if (PIS_3.equals(tribut)) {
			this.pisQuantidade = tipoPis;
		} else if (PIS_4.equals(tribut) || PIS_6.equals(tribut)
				|| PIS_7.equals(tribut) || PIS_8.equals(tribut)
				|| PIS_9.equals(tribut)) {
			this.pisNaoTributado = tipoPis;
		} else if (PIS_99.equals(tribut)) {
			this.pisOutrasOperacoes = tipoPis;
		}
	}

}
