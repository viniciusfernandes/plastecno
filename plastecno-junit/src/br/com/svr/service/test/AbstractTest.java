package br.com.svr.service.test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import br.com.svr.service.ServiceUtils;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.entity.ConfiguracaoSistema;
import br.com.svr.service.entity.PerfilAcesso;
import br.com.svr.service.entity.RamoAtividade;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.test.builder.EntidadeBuilder;
import br.com.svr.service.test.builder.ServiceBuilder;
import br.com.svr.service.test.gerador.GeradorPedido;
import br.com.svr.service.test.gerador.GeradorRepresentada;

public abstract class AbstractTest {
	private static EntityManager em;
	private static EntityManagerFactory emf;

	protected static GeradorPedido gPedido = null;
	protected static GeradorRepresentada gRepresentada = null;

	private static ServiceUtils serviceUtils;

	// Essa anotacao obriga que o metodo seja estatico.
	@BeforeClass
	public static void init() throws FileNotFoundException {
		if (emf != null) {
			return;
		}
		try {
			emf = Persistence.createEntityManagerFactory("svr");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		em = emf.createEntityManager();
		ServiceBuilder.config(em);

		serviceUtils = ServiceBuilder.buildService(ServiceUtils.class);

		gPedido = GeradorPedido.getInstance();
		gRepresentada = GeradorRepresentada.getInstance();

		initParametrosSistema();
		initPerfilAcesso();
		initRamoAtividade();
	}

	private static void initParametrosSistema() {
		List<ConfiguracaoSistema> l = em.createQuery("from ConfiguracaoSistema", ConfiguracaoSistema.class)
				.getResultList();
		if (!l.isEmpty()) {
			return;
		}
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		em.merge(new ConfiguracaoSistema("DIAS_INATIVIDADE_CLIENTE", "90"));
		em.merge(new ConfiguracaoSistema("NOME_SERVIDOR_SMTP", "smtp.plastecno.com.br"));
		em.merge(new ConfiguracaoSistema("PORTA_SERVIDOR_SMTP", "587"));
		em.merge(new ConfiguracaoSistema("SSL_HABILITADO_PARA_SMTP", "false"));
		em.merge(new ConfiguracaoSistema("PERCENTUAL_COFINS", "3"));
		em.merge(new ConfiguracaoSistema("PERCENTUAL_PIS", "0.65"));
		em.merge(new ConfiguracaoSistema("REGIME_TRIBUTACAO", "3"));
		em.merge(new ConfiguracaoSistema("DIRETORIO_XML_NFE", "C:\\NFe"));
		em.merge(new ConfiguracaoSistema("CNAE", "4689399"));
		em.merge(new ConfiguracaoSistema("CODIGO_MUNICIPIO_GERADOR_ICMS", "3550308"));
		em.merge(new ConfiguracaoSistema("EXPIRACAO_REGISTRO_ESTOQUE_MESES", "4"));

		em.getTransaction().commit();
		em.getTransaction().begin();
	}

	private static void initPerfilAcesso() {
		List<PerfilAcesso> l = em.createQuery("from PerfilAcesso", PerfilAcesso.class).getResultList();
		if (!l.isEmpty()) {
			return;
		}
		List<TipoAcesso> ltp = Arrays.asList(TipoAcesso.values());
		Collections.sort(ltp, new Comparator<TipoAcesso>() {

			@Override
			public int compare(TipoAcesso o1, TipoAcesso o2) {
				return ((Integer) o1.indexOf()).compareTo((Integer) o2.indexOf());
			}
		});

		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}

		PerfilAcesso perfil = null;
		for (TipoAcesso t : ltp) {
			perfil = new PerfilAcesso();
			perfil.setDescricao(t.toString());
			em.merge(perfil);
		}
		em.getTransaction().commit();
		em.getTransaction().begin();
	}

	private static void initRamoAtividade() {
		List<RamoAtividade> l = em.createQuery("from RamoAtividade", RamoAtividade.class).getResultList();
		if (!l.isEmpty()) {
			return;
		}
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		RamoAtividade r = new RamoAtividade();
		r.setDescricao("Industria Belica");
		r.setSigla("IB");
		try {
			em.merge(r);
		} catch (Exception e) {
			throw new IllegalStateException("Falha na geracao de ramo de atividade no inicio dos testes", e);
		}

		em.getTransaction().commit();
		em.getTransaction().begin();
	}

	protected EntidadeBuilder eBuilder = EntidadeBuilder.getInstance();

	@After
	public void afterTest() {
		em.getTransaction().rollback();
		em.clear();
	}

	@Before
	public void beforeTest() {
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}

	}

	protected <T> T pesquisarPrimeiroObjeto(Class<T> classe) {
		List<T> l = em.createQuery("from " + classe.getSimpleName(), classe).getResultList();
		return l.size() <= 0 ? null : l.get(0);
	}

	protected void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
		if (exception.getCause() != null) {
			exception.getCause().printStackTrace();
		}
	}

	protected <T> T recarregarEntidade(Class<T> classe, Integer id) {
		return serviceUtils.recarregarEntidade(classe, id);
	}

}
