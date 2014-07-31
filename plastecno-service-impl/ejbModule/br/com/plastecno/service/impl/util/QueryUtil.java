package br.com.plastecno.service.impl.util;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

public class QueryUtil {
	public static <T> T gerarRegistroUnico(Query query, Class<T> classe, T casoPadrao, T casoMultiplos) {
		try {
			return classe.cast(query.getSingleResult());
		} catch (NoResultException e) {
			return casoPadrao;
		} catch (NonUniqueResultException e) {
			return casoMultiplos;
		}
	}
	
	public static <T> T gerarRegistroUnico(Query query, Class<T> classe, T casoPadrao) {
		return gerarRegistroUnico(query, classe, casoPadrao, casoPadrao);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> paginar(Query query, Integer indiceRegistroInicial, Integer numeroMaximoRegistros){
		if(indiceRegistroInicial != null && indiceRegistroInicial >= 0 
				&& numeroMaximoRegistros != null && numeroMaximoRegistros >= 0) {
			query.setFirstResult(indiceRegistroInicial).setMaxResults(numeroMaximoRegistros);
		}
		return query.getResultList();
	}
}
