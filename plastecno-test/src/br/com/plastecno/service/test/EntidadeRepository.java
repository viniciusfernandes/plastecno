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

	void print() {
		for (List<Object> lista : mapaEntidades.values()) {
			for (Object object : lista) {
				System.out.println("Entidade: " + object);
			}
		}
		System.out.println();
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
			<T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo,
					Object nomeIdEntidade, Object valorIdEntidade) {
				return contemEntidade(classe, nomeAtributo, valorAtributo, valorIdEntidade);
			}

			@Mock
			<T> T pesquisarById(Class<T> classe, Integer id) {
				return pesquisarEntidadeById(classe, id);
			}

			@Mock
			<K> K pesquisarCampoById(Class<Object> classe, Integer id, String nomeCampo, Class<K> retorno) {
				return repository.pesquisarEntidadeAtributoById(classe, id, nomeCampo, retorno);
			}
		};

	}

	void inserirEntidade(Object entidade) {
		if (!mapaEntidades.containsKey(entidade.getClass())) {
			mapaEntidades.put(entidade.getClass(), new ArrayList<Object>());
		}
		mapaEntidades.get(entidade.getClass()).add(entidade);
	}

	@SuppressWarnings("unchecked")
	<T, K> K pesquisarEntidadeAtributoById(Class<T> classe, Integer id, String nomeCampo, Class<K> retorno) {
		T o = pesquisarEntidadeById(classe, id);
		if (o == null) {
			return null;
		}
		Object valor = null;
		Field campo = null;
		try {
			campo = classe.getDeclaredField(nomeCampo);
			campo.setAccessible(true);
			valor = campo.get(o);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException("Falha na procura do " + classe.getName() + "." + nomeCampo, e);
		} finally {
			if (campo != null) {
				campo.setAccessible(true);
			}
		}
		return (K) valor;
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
		if (id == null || !mapaEntidades.containsKey(classe)) {
			return null;
		}

		Integer idObj = null;
		List<Object> listaEntidade = mapaEntidades.get(classe);
		for (Object o : listaEntidade) {
			try {
				idObj = (Integer) o.getClass().getMethod("getId", (Class[]) null).invoke(o, (Object[]) null);
				if (id.equals(idObj)) {
					return (T) o;
				}
			} catch (Exception e) {
				throw new IllegalStateException(e);
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
		List<T> l = (List<T>) mapaEntidades.get(classe);
		if (l == null) {
			l = new ArrayList<T>();
		}
		return l;
	}

}
