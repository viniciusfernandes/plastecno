package br.com.plastecno.service.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.AutenticacaoService;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.EmailService;
import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.PerfilAcessoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.dao.ComissaoDAO;
import br.com.plastecno.service.dao.EnderecoDAO;
import br.com.plastecno.service.dao.ItemEstoqueDAO;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.ItemReservadoDAO;
import br.com.plastecno.service.dao.LimiteMinimoEstoqueDAO;
import br.com.plastecno.service.dao.MaterialDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.dao.PerfilAcessoDAO;
import br.com.plastecno.service.dao.RepresentadaDAO;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.impl.EmailServiceImpl;
import br.com.plastecno.service.mensagem.email.MensagemEmail;
import br.com.plastecno.service.test.builder.DAOBuilder;

class ServiceBuilder {

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
	private static final ServiceBuilder SERVICE_BUILDER = new ServiceBuilder();

	@SuppressWarnings("unchecked")
	static <T> T buildService(Class<T> classe) {
		T service = (T) mapTemporarioServices.get(classe);
		if (service != null) {
			return service;
		}
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
			mapTemporarioServices.put(classe, Class.forName(serviceNameImpl).newInstance());
		} catch (Exception e1) {
			throw new IllegalStateException("Nao foi possivel instanciar a implementacao do servico \"" + serviceNameImpl
					+ "\"");
		}

		String metodoName = "build" + classe.getSimpleName();
		Method method = null;
		try {
			method = ServiceBuilder.class.getDeclaredMethod(metodoName);
			try {
				method.setAccessible(true);
				service = (T) method.invoke(SERVICE_BUILDER, (Object[]) null);
				/*
				 * REMOVENDO OS OBJETOS EM MEMORIA POIS O MECANISMO DO MOCKIT DEVE SER
				 * EXECUTADO PARA CADA TESTE UNITARIO, POIS ESSE EH O CICLO DE VIDA DAS
				 * IMPLEMENTACOES MOCKADAS DOS METODOS. ELAS VALEM APENAS EM CADA TESTE
				 * UNITARIO.
				 */
				mapTemporarioServices.remove(classe);
				return service;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new TestUtilException("Falha na execucao do metodo de inicializacao do servico \"" + "build"
						+ classe.getSimpleName() + "\"", e);

			}
		} catch (SecurityException | NoSuchMethodException e) {
			throw new TestUtilException("Falha na localizacao do metodo de inicializacao do servico \"" + "build"
					+ classe.getSimpleName() + "\"", e);
		} finally {
			if (method != null) {
				method.setAccessible(false);
			}
		}
	}

	ServiceBuilder() {
	}

	@SuppressWarnings("unused")
	private AutenticacaoService buildAutenticacaoService() {
		AutenticacaoService autenticacaoService = getServiceImpl(AutenticacaoService.class);
		inject(autenticacaoService, buildDAO(UsuarioDAO.class), "usuarioDAO");
		return autenticacaoService;
	}

	@SuppressWarnings("unused")
	private ClienteService buildClienteService() {
		ClienteService clienteService = getServiceImpl(ClienteService.class);
		inject(clienteService, buildDAO(ClienteDAO.class), "clienteDAO");
		inject(clienteService, buildService(LogradouroService.class), "logradouroService");
		inject(clienteService, buildService(EnderecamentoService.class), "enderecamentoService");
		clienteService.isEmailExistente(1, "");
		return clienteService;
	}

	@SuppressWarnings("unused")
	private ComissaoService buildComissaoService() {
		ComissaoService comissaoService = getServiceImpl(ComissaoService.class);
		inject(comissaoService, buildDAO(ComissaoDAO.class), "comissaoDAO");
		inject(comissaoService, buildService(UsuarioService.class), "usuarioService");
		inject(comissaoService, buildService(MaterialService.class), "materialService");
		return comissaoService;
	}

	@SuppressWarnings("unchecked")
	private <T> T buildDAO(Class<T> daoClass) {
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
			throw new IllegalStateException("Falha no build do DAO \"" + daoClass.getName() + "\"");
		}
	}

	@SuppressWarnings("unused")
	private EmailService buildEmailService() {
		EmailService emailService = getServiceImpl(EmailService.class);
		new MockUp<EmailServiceImpl>() {
			@Mock
			void enviar(MensagemEmail mensagemEmail) throws NotificacaoException {
			}
		};

		return emailService;
	}

	@SuppressWarnings("unused")
	private EnderecamentoService buildEnderecamentoService() {
		EnderecamentoService enderecamentoService = getServiceImpl(EnderecamentoService.class);
		inject(enderecamentoService, buildDAO(EnderecoDAO.class), "enderecoDAO");
		return enderecamentoService;
	}

	@SuppressWarnings("unused")
	private EstoqueService buildEstoqueService() {
		EstoqueService estoqueService = getServiceImpl(EstoqueService.class);
		inject(estoqueService, buildDAO(ItemEstoqueDAO.class), "itemEstoqueDAO");
		inject(estoqueService, buildDAO(PedidoDAO.class), "pedidoDAO");
		inject(estoqueService, buildDAO(ItemReservadoDAO.class), "itemReservadoDAO");
		inject(estoqueService, buildDAO(LimiteMinimoEstoqueDAO.class), "limiteMinimoEstoqueDAO");
		inject(estoqueService, buildService(PedidoService.class), "pedidoService");
		return estoqueService;
	}

	@SuppressWarnings("unused")
	private LogradouroService buildLogradouroService() {
		LogradouroService logradouroService = getServiceImpl(LogradouroService.class);
		inject(logradouroService, buildService(EnderecamentoService.class), "enderecamentoService");
		return logradouroService;
	}

	@SuppressWarnings("unused")
	private MaterialService buildMaterialService() {
		MaterialService materialService = getServiceImpl(MaterialService.class);
		inject(materialService, buildDAO(MaterialDAO.class), "materialDAO");
		inject(materialService, buildService(RepresentadaService.class), "representadaService");
		return materialService;
	}

	@SuppressWarnings("unused")
	private PedidoService buildPedidoService() {
		PedidoService pedidoService = getServiceImpl(PedidoService.class);
		inject(pedidoService, buildDAO(PedidoDAO.class), "pedidoDAO");
		inject(pedidoService, buildDAO(ItemPedidoDAO.class), "itemPedidoDAO");
		inject(pedidoService, buildService(UsuarioService.class), "usuarioService");
		inject(pedidoService, buildService(ClienteService.class), "clienteService");
		inject(pedidoService, buildService(LogradouroService.class), "logradouroService");
		inject(pedidoService, buildService(EmailService.class), "emailService");
		inject(pedidoService, buildService(MaterialService.class), "materialService");
		inject(pedidoService, buildService(RepresentadaService.class), "representadaService");
		inject(pedidoService, buildService(EstoqueService.class), "estoqueService");
		inject(pedidoService, buildService(ComissaoService.class), "comissaoService");
		return pedidoService;
	}

	@SuppressWarnings("unused")
	private PerfilAcessoService buildPerfilAcessoService() {
		PerfilAcessoService perfilAcessoService = getServiceImpl(PerfilAcessoService.class);
		inject(perfilAcessoService, buildDAO(PerfilAcessoDAO.class), "perfilAcessoDAO");
		return perfilAcessoService;
	}

	@SuppressWarnings("unused")
	private RepresentadaService buildRepresentadaService() {
		RepresentadaService representadaService = getServiceImpl(RepresentadaService.class);
		inject(representadaService, buildDAO(RepresentadaDAO.class), "representadaDAO");
		inject(representadaService, buildService(LogradouroService.class), "logradouroService");
		return representadaService;
	}

	@SuppressWarnings("unused")
	private UsuarioService buildUsuarioService() {
		UsuarioService usuarioService = getServiceImpl(UsuarioService.class);
		inject(usuarioService, buildDAO(UsuarioDAO.class), "usuarioDAO");
		inject(usuarioService, buildService(LogradouroService.class), "logradouroService");
		inject(usuarioService, buildService(AutenticacaoService.class), "autenticacaoService");

		return usuarioService;
	}

	@SuppressWarnings("unchecked")
	private <T> T getServiceImpl(Class<T> classe) {
		return (T) mapTemporarioServices.get(classe);
	}

	private void inject(Object service, Object dependencia, String nomeCampo) {
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
}
