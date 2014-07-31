package br.com.plastecno.validacao.algoritmo;

public class AlgoritmoValidacaoInscricaoEstadual implements
		AlgoritmoValidacaoDocumento {

	@Override
	public boolean isValido(String documento) {
		return documento.length() <= 15;
	}

}
