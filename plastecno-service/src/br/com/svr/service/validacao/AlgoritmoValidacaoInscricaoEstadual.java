package br.com.svr.service.validacao;

public class AlgoritmoValidacaoInscricaoEstadual implements
		AlgoritmoValidacaoDocumento {

	@Override
	public boolean isValido(String documento) {
		return documento.length() <= 15;
	}

}
