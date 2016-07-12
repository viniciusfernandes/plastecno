package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ICMS {
	@XmlElement(name = "ICMS00")
	private ICMSIntegral ICMSIntegral;
	@XmlElement(name = "ICMS10")
	private ICMSST ICMSST;

	public void setICMSIntegral(ICMSIntegral iCMSIntegral) {
		ICMSIntegral = iCMSIntegral;
	}

	public void setICMSST(ICMSST iCMSST) {
		ICMSST = iCMSST;
	}

}