package br.com.svr.service.test.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Id;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.dao.GenericDAO;

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

	@SuppressWarnings("unchecked")
	<T> Long contar(Class<T> classe, Predicate<T> predicate) {
		return gerarStream(classe, predicate).count();
	}

	<T> boolean contemEntidade(Class<T> classe, String nomeAtributo, Object valorAtributo, Object valorIdEntidade) {
		return pesquisarEntidadeByAtributo(classe, nomeAtributo, valorAtributo, valorIdEntidade) != null;
	}

	@SuppressWarnings("unchecked")
	<T> List<T> filtrar(Class<T> classe, Predicate<T>... predicates) {
		return gerarStream(classe, predicates).collect(Collectors.toList());
	}

	private Integer gerarId() {
		return (int) (9999 * Math.random());
	}

	private <T, R> Stream<T> gerarStream(Class<T> classe, Predicate<T>... predicates) {
		Set<T> lEnt = (Set<T>) mapaEntidades.get(classe);

		List<T> l = new ArrayList<T>();
		if (lEnt != null && !lEnt.isEmpty()) {
			l.addAll(lEnt);
		} else {
			return l.stream();
		}

		Stream<T> st = l.stream();
		for (Predicate<T> p : predicates) {
			st = st.filter(p);
		}
		return st;
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
				Field[] campos = t.getClass().getDeclaredFields();
				boolean isId = false;
				for (Field f : campos) {
					// Devemos verificar se o campos esta anotado como chave
					// primaria pois nem todas as entidades possuem essa chave
					// nomeada como ID, por exemplo a NFe.
					if ((isId = f.isAnnotationPresent(Id.class)) && f.getName().equals("id")) {
						try {
							f.setAccessible(true);
							f.set(t, gerarId());
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new IllegalArgumentException(e);

						} finally {
							f.setAccessible(false);
						}
					} else if (isId) {
						try {
							f.setAccessible(true);
							if (f.get(t) == null) {
								throw new IllegalArgumentException("A chave primaria do campo " + f.getName()
										+ " nao pode estar em branco e deve ser gerada manualmente.");
							}
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new IllegalArgumentException(e);
						} finally {
							f.setAccessible(false);
						}
					}
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
			<T> T remover(T t) {
				if (t == null) {
					return null;
				}
				Field[] campos = t.getClass().getDeclaredFields();
				Field id = null;
				for (Field f : campos) {
					if (f.isAnnotationPresent(Id.class)) {
						id = f;
						break;
					}
				}

				if (id == null) {
					throw new IllegalStateException("A classe " + t.getClass().getName()
							+ " nao possui um campo com anotacao @Id");
				}

				List<? extends Object> l = repository.pesquisarTodos(t.getClass());
				id.setAccessible(true);
				Object idVal;
				try {
					idVal = id.get(t);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new IllegalStateException("Nao foi possivel acessar o ID da classe " + t.getClass().getName()
							+ " para a remocao do repositorio", e);
				} finally {
					id.setAccessible(false);
				}

				if (idVal == null) {
					id.setAccessible(false);
					return null;
				}

				T remov = null;

				for (Object obj : l) {
					id.setAccessible(true);
					try {
						if (idVal.equals(id.get(obj))) {
							mapaEntidades.get(t.getClass()).remove(obj);
							id.setAccessible(false);
							remov = (T) obj;
							break;
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new IllegalStateException("Nao foi possivel acessar o ID da classe "
								+ obj.getClass().getName(), e);
					} finally {
						id.setAccessible(false);
					}
					id.setAccessible(false);
				}

				return remov;
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
				throw new IllegalArgumentException("A entidade do tipo " + classe + " nao possui o atributo \""
						+ nomeAtributo + "\"", e);
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
			throw new IllegalArgumentException("A entidade do tipo " + classe + " nao possui o atributo \""
					+ nomeAtributo + "\"", e);
		}
	}

	@SuppressWarnings("unchecked")
	<T> T pesquisarEntidadeById(Class<T> classe, Integer id) {
		if (id == null || !mapaEntidades.containsKey(classe)) {
			return null;
		}

		Object idObj = null;
		Set<Object> listaEntidade = mapaEntidades.get(classe);
		for (Object o : listaEntidade) {
			try {

				Field[] campos = o.getClass().getDeclaredFields();
				for (Field f : campos) {
					if (f.isAnnotationPresent(Id.class)) {
						f.setAccessible(true);
						idObj = f.get(o);
						f.setAccessible(false);

						if (id.equals(idObj)) {
							return (T) o;
						}
					}
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
			throw new IllegalArgumentException("A entidade do tipo " + classe + " nao possui o atributo \""
					+ nomeAtributo + "\"", e);
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

	<T> void removerEntidade(Class<T> classe, Integer id) {
		T t = repository.pesquisarEntidadeById(classe, id);
		mapaEntidades.get(classe).remove(t);
	}

}
