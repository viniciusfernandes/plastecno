package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class CobrancaNFe {
	@XmlElement(name = "fat")
	private FaturaNFe faturaNFe;
	
	@XmlElement(name="dup")
	private List<DuplicataNFe> listaDuplicata;

	public void setFaturaNFe(FaturaNFe faturaNFe) {
		this.faturaNFe = faturaNFe;
	}

	public void setListaDuplicata(List<DuplicataNFe> listaDuplicata) {
		this.listaDuplicata = listaDuplicata;
	}
}
