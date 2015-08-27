package br.com.plastecno.service.test.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.EmailService;
import br.com.plastecno.service.dao.GenericDAO;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.impl.EmailServiceImpl;
import br.com.plastecno.service.mensagem.email.MensagemEmail;

public class ServiceBuilder {

	@SuppressWarnings("unchecked")
	private static <T> T buildDAO(Class<T> daoClass) {
		try {
			DAOBuilder<T> daoBuilder = mapDAO.get(daoClass);
			if (daoBuilder == null) {
				daoBuilder = (DAOBuilder<T>) Class.forName(
						"br.com.plastecno.service.test.builder." + daoClass.getSimpleName() + "Builder").newInstance();
				mapDAO.put(daoClass, daoBuilder);
				return daoBuilder.build();
			}
			return daoBuilder.build();
		} catch (Exception e) {
			throw new IllegalStateException("Falha no build do DAO \"" + daoClass.getName() + "\"", e);
		}
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
			 * EFETUAR O BUILD DO ESTOQUESERVICE, ENTAO, PARA CONTORNAR UM DEADLOCK
			 * ENTRE OS BUILDS, JOGAMOS O OBJETO PEDIDOSERVICEIMPL EM MEMORIA, E ASSIM
			 * QUE O ESTOQUESERVICE FOR EFFETUAR O BUILD DO PEDIDOSSERVICE,
			 * VERIFICAMOS QUE ELE JA ESTA EM MEMORIA E RETORNAMOS ESSE OBJETO. SENDO
			 * QUE MANTEMOS TEMPORARIAMENTE ESSES OBJETOS EM MEMORIA POIS O MECANISMO
			 * DO MOCKIT DEVE SER EXECUTADO PARA CADA TESTE UNITARIO, POIS ESSE EH O
			 * CICLO DE VIDA DAS IMPLEMENTACOES MOCKADAS DOS METODOS. ELAS VALEM
			 * APENAS EM CADA TESTE UNITARIO.
			 */
			service = (T) Class.forName(serviceNameImpl).newInstance();
			mapTemporarioServices.put(classe, service);
		} catch (Exception e1) {
			throw new IllegalStateException("Nao foi possivel instanciar a implementacao do servico \"" + serviceNameImpl
					+ "\"", e1);
		}

		initDependencias(service);
		return service;
	}

	private static void initDependencias(Object service) {
		Field[] campos = service.getClass().getDeclaredFields();
		for (Field campo : campos) {
			if (campo.getName().endsWith("DAO") && !campo.getType().equals(GenericDAO.class)) {
				buildDAO(campo.getType());
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
			throw new IllegalStateException("Falha a execucao do metodo init do servico " + service.getClass().getName(), e);
		}

		for (Field campo : campos) {
			if (campo.isAnnotationPresent(EJB.class)) {
				inject(service, buildService(campo.getType()), campo.getName());
			}

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

	@SuppressWarnings("rawtypes")
	private final static Map<Class<?>, DAOBuilder> mapDAO = new HashMap<Class<?>, DAOBuilder>();

	/*
	 * ESSE ATRIBUTO FOI CRIADO PARA CONTORNAR O PROBLEMA DE REFERENCIAS CICLICAS
	 * ENTRE OS SERVICOS, POR EXEMPLO, PEDIDOSERVICE E ESTOQUE SERVICE. QUANDO
	 * VAMOS EFETUAR O BUILD DO PEDIDOSERVICE, TEMOS QUE EFETUAR O BUILD DO
	 * ESTOQUESERVICE, ENTAO, PARA CONTORNAR UM DEADLOCK ENTRE OS BUILDS, JOGAMOS
	 * O OBJETO PEDIDOSERVICEIMPL EM MEMORIA, E ASSIM QUE O ESTOQUESERVICE FOR
	 * EFFETUAR O BUILD DO PEDIDOSSERVICE, VERIFICAMOS QUE ELE JA ESTA EM MEMORIA
	 * E RETORNAMOS ESSE OBJETO. SENDO QUE MANTEMOS TEMPORARIAMENTE ESSES OBJETOS
	 * EM MEMORIA POIS O MECANISMO DO MOCKIT DEVE SER EXECUTADO PARA CADA TESTE
	 * UNITARIO, POIS ESSE EH O CICLO DE VIDA DAS IMPLEMENTACOES MOCKADAS DOS
	 * METODOS. ELAS VALEM APENAS EM CADA TESTE UNITARIO.
	 */
	private final static Map<Class<?>, Object> mapTemporarioServices = new HashMap<Class<?>, Object>();

	ServiceBuilder() {
	}

	private static void buildEmailService() {
		new MockUp<EmailServiceImpl>() {

			@Mock
			public void enviar(MensagemEmail mensagemEmail) throws NotificacaoException {
			}
		};

		mapTemporarioServices.put(EmailService.class, new EmailServiceImpl());
	}
}
