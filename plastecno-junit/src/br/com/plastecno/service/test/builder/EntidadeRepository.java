package br.com.plastecno.service.test.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.GenericDAO;

public class EntidadeRepository {
	private static final Map<Class<?>, Set<Object>> mapaEntidades = new HashMap<Class<?>, Set<Object>>();

	private static final EntidadeRepository repository = new EntidadeRepository();

	public static EntidadeRepository getInstance() {
		return repository;
	}

	private EntidadeRepository() {
		initGenericDAO();
	}

	<T> void alterarEntidadeAtributoById(Class<T> classe, Integer id, String nomeAtributo, Object valorAtributo) {
		T t = pesquisarEntidadeById(classe, id);
		if (t == null) {
			return;
		}
		Field f = null;
		try {
			f = classe.getDeclaredField(nomeAtributo);
			f.setAccessible(true);
			f.set(t, valorAtributo);
		} catch (Exception e) {
			throw new IllegalStateException("Falha na procura do " + classe.getName() + "." + nomeAtributo, e);
		} finally {
			if (f != null) {
				f.setAccessible(false);
			}
		}
	}

	public void clear() {
		mapaEntidades.clear();
	}

	<T> boolean contemEntidade(Class<T> classe, String nomeAtributo, Object valorAtributo, Object valorIdEntidade) {
		return pesquisarEntidadeByAtributo(classe, nomeAtributo, valorAtributo, valorIdEntidade) != null;
	}

	private Integer gerarId() {
		return (int) (9999 * Math.random());
	}

	private void initGenericDAO() {
		new MockUp<GenericDAO<Object>>() {

			@Mock
			Object alterar(Object t) {
				try {
					Method m = t.getClass().getMethod("getId");
					Object id = m.invoke(t, (Object[]) null);
					if (id == null) {
						inserir(t);
					}
				} catch (NoSuchMethodException e) {
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}

				inserirEntidade(t);
				return t;
			}

			@Mock
			Object flush(Object t) {
				return inserir(t);
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

			@Mock
			Object remover(Object t) {
				return mapaEntidades.get(t.getClass()).remove(t);
			}
		};

	}

	void inserirEntidade(Object entidade) {
		if (!mapaEntidades.containsKey(entidade.getClass())) {
			mapaEntidades.put(entidade.getClass(), new HashSet<Object>());
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

	<T> List<T> pesquisarEntidadeByAtributo(Class<T> classe, String nomeAtributo, Object valorAtributo) {
		List<T> todos = pesquisarTodos(classe);
		List<T> lista = new ArrayList<T>();
		for (T t : todos) {
			try {
				Field field = classe.getDeclaredField(nomeAtributo);
				field.setAccessible(true);
				try {
					Object valor = field.get(t);
					if (valor != null && valor.equals(valorAtributo)) {
						lista.add(t);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new IllegalArgumentException("Falha no acesso o valor do atributo \"" + nomeAtributo
							+ "\" da entidade cujo valor eh \"" + valorAtributo + "\"", e);
				}
			} catch (NoSuchFieldException | SecurityException e) {
				throw new IllegalArgumentException("A entidade do tipo " + classe + " nao possui o atributo \"" + nomeAtributo
						+ "\"", e);
			}
		}
		return lista;
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
		Set<Object> listaEntidade = mapaEntidades.get(classe);
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
		Set<T> s = (Set<T>) mapaEntidades.get(classe);
		List<T> l = new ArrayList<T>();
		if (s != null) {
			l.addAll(s);
		}
		return l;
	}

	void print() {
		for (Set<Object> lista : mapaEntidades.values()) {
			for (Object object : lista) {
				System.out.println("Entidade: " + object);
			}
		}
	}

}
