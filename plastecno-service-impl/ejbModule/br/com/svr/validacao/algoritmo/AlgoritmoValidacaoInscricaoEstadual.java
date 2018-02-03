package br.com.svr.validacao.algoritmo;

public class AlgoritmoValidacaoInscricaoEstadual implements
		AlgoritmoValidacaoDocumento {

	@Override
	public boolean isValido(String documento) {
		return documento.length() <= 15;
	}

}
