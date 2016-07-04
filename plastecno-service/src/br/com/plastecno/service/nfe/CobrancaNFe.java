package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class CobrancaNFe {
	@XmlElement(name = "fat")
	private FaturaNFe faturaNFe;
	@XmlElement(name="dup")
	private DuplicataNFe duplicataNFe;
}
