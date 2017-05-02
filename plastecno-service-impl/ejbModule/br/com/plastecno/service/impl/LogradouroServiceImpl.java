package br.com.plastecno.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.dao.LogradouroDAO;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.LogradouroEndereco;
import br.com.plastecno.service.entity.LogradouroPedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;

@Stateless
public class LogradouroServiceImpl implements LogradouroService {
	@EJB
	private EnderecamentoService enderecamentoService;
	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	private LogradouroDAO logradouroDAO;

	@PostConstruct
	public void init() {
		logradouroDAO = new LogradouroDAO(entityManager);
	}

	@Override
	public List<LogradouroCliente> inserir(List<LogradouroCliente> listaLogradouro) throws BusinessException {
		if (listaLogradouro == null) {
			return null;
		}

		if (listaLogradouro.isEmpty()) {
			return Collections.emptyList();
		}

		List<LogradouroCliente> lista = new ArrayList<>();
		for (LogradouroCliente logradouro : listaLogradouro) {
			lista.add(inserir(logradouro));
		}
		return lista;
	}

	@Override
	public <T extends Logradouro> T inserir(T logradouro) throws BusinessException {
		if (logradouro != null) {
			return (T) entityManager.merge(logradouro);
		}
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public LogradouroEndereco inserirBaseCep(LogradouroEndereco logradouro) throws BusinessException {
		if (logradouro != null) {

			/*
			 * Aqui vamos configuirar o endereco, pois o servico de inclusao de
			 * enderecos recuperar os IDS do bairro, cidade e pais.
			 */
			logradouro.addEndereco(enderecamentoService.inserir(logradouro.recuperarEndereco()));
			return (LogradouroEndereco) logradouroDAO.alterar(logradouro);
		}
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<? extends LogradouroEndereco> pesquisar(Integer id, Class<? extends LogradouroEndereco> classe) {
		String nomeTipoLogradouro = classe.getSimpleName();
		StringBuilder select = new StringBuilder();
		select.append("select c from ").append(nomeTipoLogradouro).append(" c inner join c.")
				.append(nomeTipoLogradouro.replace("Logradouro", "").toLowerCase()).append(" l where l.id =:id");

		return entityManager.createQuery(select.toString()).setParameter("id", id).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public <T extends LogradouroEndereco> List<T> pesquisarAusentes(Integer id, Collection<T> listaLogradouro, Class<T> classe) {
		String nomeTipoLogradouro = classe.getSimpleName();
		StringBuilder select = new StringBuilder();
		select.append("select l from ").append(nomeTipoLogradouro).append(" l where l.")
				.append(nomeTipoLogradouro.replace("Logradouro", "").toLowerCase()).append(".id =:id ");

		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			select.append(" and l not in (:listaLogradouro) ");
		}

		final Query query = this.entityManager.createQuery(select.toString()).setParameter("id", id);

		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			query.setParameter("listaLogradouro", listaLogradouro);
		}
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisarCodigoIBGEByCEP(String cep) {
		if (cep == null) {
			return null;
		}
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createNativeQuery(
										"select c.cod_ibge from enderecamento.tb_cidade c inner join enderecamento.tb_endereco e on e.id_cidade = c.id_cidade and e.cep = :cep")
								.setParameter("cep", cep), String.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisarCodigoIBGEByIdCidade(Integer idCidade) {
		if (idCidade == null) {
			return null;
		}
		return logradouroDAO.pesquisarCodigoIBGEByIdCidade(idCidade);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.plastecno.service.LogradouroService#removerAusentes(java.lang.
	 * Integer, java.util.Collection, java.lang.Class)
	 */
	@Override
	public <T extends LogradouroEndereco> void removerAusentes(Integer id, Collection<T> listaLogradouro, Class<T> classe) {
		List<? extends LogradouroEndereco> listaLogradouroCadastrado = this.pesquisarAusentes(id, listaLogradouro, classe);
		for (LogradouroEndereco logradouro : listaLogradouroCadastrado) {
			this.entityManager.remove(logradouro);
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarListaLogradouroPreenchida(List<LogradouroCliente> listaLogradouro) throws BusinessException {
		Set<TipoLogradouro> lLogAusente = new HashSet<TipoLogradouro>();
		lLogAusente.add(TipoLogradouro.COBRANCA);
		lLogAusente.add(TipoLogradouro.ENTREGA);
		lLogAusente.add(TipoLogradouro.FATURAMENTO);

		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			listaLogradouro.forEach(t -> {
				lLogAusente.remove(t.getTipoLogradouro());
			});
		}

		if (lLogAusente.isEmpty()) {
			return;
		}

		List<String> listaMensagem = new ArrayList<String>();
		lLogAusente.forEach(t -> listaMensagem.add("É obrigatorio logradouro do tipo " + t));
		throw new InformacaoInvalidaException(listaMensagem);
	}
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarListaLogradouroPreenchidaXXX(List<LogradouroPedido> listaLogradouro) throws BusinessException {
		Set<TipoLogradouro> lLogAusente = new HashSet<TipoLogradouro>();
		lLogAusente.add(TipoLogradouro.COBRANCA);
		lLogAusente.add(TipoLogradouro.ENTREGA);
		lLogAusente.add(TipoLogradouro.FATURAMENTO);

		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			listaLogradouro.forEach(t -> {
				lLogAusente.remove(t.getTipoLogradouro());
			});
		}

		if (lLogAusente.isEmpty()) {
			return;
		}

		List<String> listaMensagem = new ArrayList<String>();
		lLogAusente.forEach(t -> listaMensagem.add("É obrigatorio logradouro do tipo " + t));
		throw new InformacaoInvalidaException(listaMensagem);
	}

}
