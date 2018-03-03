package br.com.svr.service.validacao;

import java.util.HashMap;
import java.util.Map;

public class ValidadorDocumento {
	private static final Map<TipoDocumento, AlgoritmoValidacaoDocumento> mapa;

	static {
		mapa = new HashMap<TipoDocumento, AlgoritmoValidacaoDocumento>();
		mapa.put(TipoDocumento.CNPJ, new AlgoritmoValidacaoCNPJ());
		mapa.put(TipoDocumento.CPF, new AlgoritmoValidacaoCPF());
		mapa.put(TipoDocumento.INSCRICAO_ESTADUAL, new AlgoritmoValidacaoInscricaoEstadual());
		mapa.put(TipoDocumento.NCM, new AlgoritmoValidacaoNCM());
	}

	public static boolean isValido(TipoDocumento tipoDocumento, String documento) {
		return ValidadorDocumento.mapa.get(tipoDocumento).isValido(documento);
	}

}