package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class CobrancaNFe {
	@InformacaoValidavel(cascata = true, nomeExibicao = "Fatura da cobran�a")
	@XmlElement(name = "fat")
	private FaturaNFe faturaNFe;

	@InformacaoValidavel(iteravel = true, nomeExibicao = "Duplicatas da cobran�a")
	@XmlElement(name = "dup")
	private List<DuplicataNFe> listaDuplicata;

	public boolean contemDuplicata() {
		return listaDuplicata != null && listaDuplicata.size() > 0;
	}

	@XmlTransient
	public FaturaNFe getFaturaNFe() {
		return faturaNFe;
	}

	@XmlTransient
	public List<DuplicataNFe> getListaDuplicata() {
		return listaDuplicata;
	}

	public void setFaturaNFe(FaturaNFe faturaNFe) {
		this.faturaNFe = faturaNFe;
	}

	public void setListaDuplicata(List<DuplicataNFe> listaDuplicata) {
		this.listaDuplicata = listaDuplicata;
	}
}
