package br.com.svr.service.test.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ServiceBuilder {
	private static EntityManager em = null;

	/*
	 * ESSE ATRIBUTO FOI CRIADO PARA CONTORNAR O PROBLEMA DE REFERENCIAS
	 * CICLICAS ENTRE OS SERVICOS, POR EXEMPLO, PEDIDOSERVICE E ESTOQUE SERVICE.
	 * QUANDO VAMOS EFETUAR O BUILD DO PEDIDOSERVICE, TEMOS QUE EFETUAR O BUILD
	 * DO ESTOQUESERVICE, ENTAO, PARA CONTORNAR UM DEADLOCK ENTRE OS BUILDS,
	 * JOGAMOS O OBJETO PEDIDOSERVICEIMPL EM MEMORIA, E ASSIM QUE O
	 * ESTOQUESERVICE FOR EFFETUAR O BUILD DO PEDIDOSSERVICE, VERIFICAMOS QUE
	 * ELE JA ESTA EM MEMORIA E RETORNAMOS ESSE OBJETO. SENDO QUE MANTEMOS
	 * TEMPORARIAMENTE ESSES OBJETOS EM MEMORIA POIS O MECANISMO DO MOCKIT DEVE
	 * SER EXECUTADO PARA CADA TESTE UNITARIO, POIS ESSE EH O CICLO DE VIDA DAS
	 * IMPLEMENTACOES MOCKADAS DOS METODOS. ELAS VALEM APENAS EM CADA TESTE
	 * UNITARIO.
	 */
	private final static Map<Class<?>, Object> mapTemporarioServices = new HashMap<Class<?>, Object>();

	private static void buildEmailService() {
	
	}

	@SuppressWarnings("unchecked")
	public static <T> T buildService(Class<T> classe) {
		T service = (T) mapTemporarioServices.get(classe);
		if (service != null) {
			return service;
		}

		buildEmailService();

		String serviceNameImpl = classe.getName().replace("service", "service.impl") + "Impl";
		try {
			/*
			 * ESSE ATRIBUTO FOI CRIADO PARA CONTORNAR O PROBLEMA DE REFERENCIAS
			 * CICLICAS ENTRE OS SERVICOS, POR EXEMPLO, PEDIDOSERVICE E ESTOQUE
			 * SERVICE. QUANDO VAMOS EFETUAR O BUILD DO PEDIDOSERVICE, TEMOS QUE
			 * EFETUAR O BUILD DO ESTOQUESERVICE, ENTAO, PARA CONTORNAR UM
			 * DEADLOCK ENTRE OS BUILDS, JOGAMOS O OBJETO PEDIDOSERVICEIMPL EM
			 * MEMORIA, E ASSIM QUE O ESTOQUESERVICE FOR EFFETUAR O BUILD DO
			 * PEDIDOSSERVICE, VERIFICAMOS QUE ELE JA ESTA EM MEMORIA E
			 * RETORNAMOS ESSE OBJETO. SENDO QUE MANTEMOS TEMPORARIAMENTE ESSES
			 * OBJETOS EM MEMORIA POIS O MECANISMO DO MOCKIT DEVE SER EXECUTADO
			 * PARA CADA TESTE UNITARIO, POIS ESSE EH O CICLO DE VIDA DAS
			 * IMPLEMENTACOES MOCKADAS DOS METODOS. ELAS VALEM APENAS EM CADA
			 * TESTE UNITARIO.
			 */
			service = (T) Class.forName(serviceNameImpl).newInstance();
			mapTemporarioServices.put(classe, service);
		} catch (Exception e1) {
			throw new IllegalStateException("Nao foi possivel instanciar a implementacao do servico \""
					+ serviceNameImpl + "\"", e1);
		}

		initDependencias(service);
		return service;
	}

	public static void config(EntityManager em) {
		if (ServiceBuilder.em == null) {
			ServiceBuilder.em = em;
		}
	}

	private static void initDependencias(Object service) {
		Field[] campos = service.getClass().getDeclaredFields();
		for (Field campo : campos) {
			if (campo.isAnnotationPresent(EJB.class)) {
				inject(service, buildService(campo.getType()), campo.getName());
			}
			// Injetando o entity manager
			if (campo.isAnnotationPresent(PersistenceContext.class)) {
				if (em == null) {
					throw new IllegalStateException("O entity manager esta nulo e nao pode ser injetado no servico "
							+ service.getClass().getName() + ". Execute o metodo ServiceBuilder.config(EntityManager).");
				}
				inject(service, em, campo.getName());
			}

		}

		try {
			// Executando os metodos init da implementacao dos servicos
			Method[] metodos = service.getClass().getMethods();
			for (Method metodo : metodos) {
				if (metodo.isAnnotationPresent(PostConstruct.class)) {
					metodo.invoke(service, (Object[]) null);
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("Falha a execucao do metodo init do servico "
					+ service.getClass().getName(), e);
		}

	}

	private static void inject(Object service, Object dependencia, String nomeCampo) {
		try {
			Field campo = service.getClass().getDeclaredField(nomeCampo);

			campo.setAccessible(true);
			campo.set(service, dependencia);
			campo.setAccessible(false);
		} catch (Exception e) {
			throw new IllegalArgumentException("Falha ao injetar a dependencia para o servico \""
					+ service.getClass().getName() + "\". Campo com problemas eh \"" + nomeCampo);
		}
	}

	ServiceBuilder() {
	}
}
