package br.com.plastecno.service.nfe;

public class IPI {
	private String classeEnquadramento;
	private String CNPJProdutor;
	private String codigoSeloControle;
	private Integer quantidadeSeloControle;

	public String getClasseEnquadramento() {
		return classeEnquadramento;
	}

	public String getCNPJProdutor() {
		return CNPJProdutor;
	}

	public String getCodigoSeloControle() {
		return codigoSeloControle;
	}

	public Integer getQuantidadeSeloControle() {
		return quantidadeSeloControle;
	}

	public void setClasseEnquadramento(String classeEnquadramento) {
		this.classeEnquadramento = classeEnquadramento;
	}

	public void setCNPJProdutor(String cNPJProdutor) {
		CNPJProdutor = cNPJProdutor;
	}

	public void setCodigoSeloControle(String codigoSeloControle) {
		this.codigoSeloControle = codigoSeloControle;
	}

	public void setQuantidadeSeloControle(Integer quantidadeSeloControle) {
		this.quantidadeSeloControle = quantidadeSeloControle;
	}

}
