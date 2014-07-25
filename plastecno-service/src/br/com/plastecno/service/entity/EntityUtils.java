package br.com.plastecno.service.entity;

import java.util.List;

import br.com.plastecno.service.constante.TipoLogradouro;

final class EntityUtils {
	static Logradouro getLogradouro(final List<? extends Logradouro> listaLogradouro, TipoLogradouro tipoLogradouro) {
		if (listaLogradouro == null  || listaLogradouro.isEmpty() || tipoLogradouro == null) {
			return null;
		}
		Logradouro logradouro = null; 
		for (Logradouro logr : listaLogradouro) {
			if (tipoLogradouro.equals(logr.getTipoLogradouro())) {
				logradouro = logr;
				break;
			}
		}
		return logradouro;
	}
}
