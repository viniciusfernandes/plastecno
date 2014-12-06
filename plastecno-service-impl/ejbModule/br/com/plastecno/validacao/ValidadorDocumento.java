package br.com.plastecno.validacao;

import java.util.HashMap;
import java.util.Map;

import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.validacao.algoritmo.AlgoritmoValidacaoCNPJ;
import br.com.plastecno.validacao.algoritmo.AlgoritmoValidacaoCPF;
import br.com.plastecno.validacao.algoritmo.AlgoritmoValidacaoDocumento;
import br.com.plastecno.validacao.algoritmo.AlgoritmoValidacaoInscricaoEstadual;

public class ValidadorDocumento {
	public static boolean isValido(TipoDocumento tipoDocumento, String documento) {
		return ValidadorDocumento.mapa.get(tipoDocumento).isValido(documento);
	}
	private static final Map<TipoDocumento, AlgoritmoValidacaoDocumento> mapa;

	static {
		mapa = new HashMap<TipoDocumento, AlgoritmoValidacaoDocumento>();
		mapa.put(TipoDocumento.CNPJ, new AlgoritmoValidacaoCNPJ());
		mapa.put(TipoDocumento.CPF, new AlgoritmoValidacaoCPF());
		mapa.put(TipoDocumento.INSCRICAO_ESTADUAL, new AlgoritmoValidacaoInscricaoEstadual());
	}
	

}