package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ICMS {
	@XmlElement(name = "ICMS00")
	private ICMSIntegral icms00;

	@XmlElement(name = "ICMS10")
	private ICMSIntegral icms10;

	
}