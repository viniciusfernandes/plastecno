package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class CobrancaNFe {
	@InformacaoValidavel(cascata = true, nomeExibicao = "Fatura da cobran�a")
	@XmlElement(name = "fat")
	private FaturaNFe faturaNFe;

	@InformacaoValidavel(iteravel = true, nomeExibicao = "Duplicatas da cobran�a")
	@XmlElement(name = "dup")
	private List<DuplicataNFe> listaDuplicata;

	public void setFaturaNFe(FaturaNFe faturaNFe) {
		this.faturaNFe = faturaNFe;
	}

	public void setListaDuplicata(List<DuplicataNFe> listaDuplicata) {
		this.listaDuplicata = listaDuplicata;
	}
}
