package br.com.plastecno.validacao.algoritmo;

public class AlgoritmoValidacaoNCM implements AlgoritmoValidacaoDocumento {

	@Override
	public boolean isValido(String documento) {
		return documento != null && documento.matches("\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{2}");
	}

}
