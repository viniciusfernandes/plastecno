package br.com.plastecno.service.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.GenericDAO;

class EntidadeRepository {
	public static EntidadeRepository getInstance() {
		return repository;
	}

	private static final Map<Class<?>, List<Object>> mapaEntidades = new HashMap<Class<?>, List<Object>>();

	private static final EntidadeRepository repository = new EntidadeRepository();

	private EntidadeRepository() {

	}

	void clear() {
		mapaEntidades.clear();
	}

	<T> boolean contemEntidade(Class<T> classe, String nomeAtributo, Object valorAtributo, Object valorIdEntidade) {
		return pesquisarEntidadeByAtributo(classe, nomeAtributo, valorAtributo, valorIdEntidade) != null;
	}

	private Integer gerarId() {
		return (int) (9999 * Math.random());
	}

	void init() {
		initGenericDAO();
	}

	private void initGenericDAO() {
		new MockUp<GenericDAO<Object>>() {

			@Mock
			Object alterar(Object t) {
				inserirEntidade(t);
				return t;
			}

			@Mock
			Object inserir(Object t) {
				try {
					Method m = t.getClass().getMethod("setId", Integer.class);
					m.invoke(t, gerarId());
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
				inserirEntidade(t);
				return t;
			}
			@Mock
			<T> T pesquisarById(Class<T> classe, Integer id) {
				return pesquisarEntidadeById(classe, id);
			}
			@Mock
			<T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo,
					Object nomeIdEntidade, Object valorIdEntidade) {
				return contemEntidade(classe, nomeAtributo, valorAtributo, valorIdEntidade);
			}
		};

	}

	void inserirEntidade(Object entidade) {
		if (!mapaEntidades.containsKey(entidade.getClass())) {
			mapaEntidades.put(entidade.getClass(), new ArrayList<Object>());
		}
		mapaEntidades.get(entidade.getClass()).add(entidade);
	}

	<T> T pesquisarEntidadeByAtributo(Class<T> classe, String nomeAtributo, Object valorAtributo, Object valorIdEntidade) {
		T entidade = pesquisarEntidadeById(classe, (Integer) valorIdEntidade);
		if (entidade == null) {
			return null;
		}
		try {
			Field field = classe.getDeclaredField(nomeAtributo);
			field.setAccessible(true);
			try {
				Object valor = field.get(entidade);
				return valor != null && valor.equals(valorAtributo) ? entidade : null;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalArgumentException("Falha no acesso o valor do atributo \"" + nomeAtributo
						+ "\" da entidade cujo valor eh \"" + valorAtributo + "\"", e);
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalArgumentException("A entidade do tipo " + classe + " nao possui o atributo \"" + nomeAtributo
					+ "\"", e);
		}
	}

	@SuppressWarnings("unchecked")
	<T> T pesquisarEntidadeById(Class<T> classe, Integer id) {
		if (!mapaEntidades.containsKey(classe)) {
			return null;
		}
		Integer idObj = null;
		for (Object o : mapaEntidades.get(classe)) {
			try {
				idObj = (Integer) o.getClass().getMethod("getId", (Class[]) null).invoke(o, (Object[]) null);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			if (idObj != null) {
				return (T) o;
			}
		}
		return null;
	}

	<T> List<T> pesquisarEntidadeByRelacionamento(Class<T> classe, String nomeAtributo, Object valorAtributo) {
		List<T> entidadeLista = pesquisarTodos(classe);
		if (entidadeLista == null || entidadeLista.isEmpty()) {
			return new ArrayList<T>();
		}
		try {
			Field field = classe.getDeclaredField(nomeAtributo);
			field.setAccessible(true);
			try {
				List<T> novaLista = new ArrayList<T>();
				Object valor = null;
				boolean ambosNulos = false;
				boolean valorIgual = false;
				for (T entidade : entidadeLista) {
					valor = field.get(entidade);
					valorIgual = valor != null && valor.equals(valorAtributo);
					ambosNulos = valor == null && valorAtributo == null;
					if (valorIgual || ambosNulos) {
						novaLista.add(entidade);
					}
				}
				return novaLista;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalArgumentException("Falha no acesso o valor do atributo \"" + nomeAtributo
						+ "\" da entidade cujo valor eh \"" + valorAtributo + "\"", e);
			}
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalArgumentException("A entidade do tipo " + classe + " nao possui o atributo \"" + nomeAtributo
					+ "\"", e);
		}
	}

	@SuppressWarnings("unchecked")
	<T> List<T> pesquisarTodos(Class<T> classe) {
		return (List<T>) mapaEntidades.get(classe);
	}

}
