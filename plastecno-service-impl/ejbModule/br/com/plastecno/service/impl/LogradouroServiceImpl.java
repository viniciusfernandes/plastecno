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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.LogradouroDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;

@Stateless
public class LogradouroServiceImpl implements LogradouroService {

	@PersistenceContext(unitName="plastecno")
	private EntityManager entityManager;
	
	@EJB
	private EnderecamentoService enderecamentoService;
	
	private LogradouroDAO logradouroDAO ;
	@PostConstruct
	public void init() {
		logradouroDAO= new LogradouroDAO(entityManager);
	}
	
	@Override
	public <T extends Logradouro> List<T> inserir(List<T> listaLogradouro) throws BusinessException {
		if(listaLogradouro == null) {
			return null;
		}
		
		if(listaLogradouro.isEmpty()) {
			return Collections.emptyList();
		}

		List<T> lista = new ArrayList<T>();
		for (T logradouro : listaLogradouro) {
			lista.add(this.inserir(logradouro));
		}
		return lista;
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.plastecno.service.LogradouroService#inserir(br.com.plastecno.service.entity.Logradouro)
	 */
	@Override
	public <T extends Logradouro> T inserir(T logradouro) throws BusinessException {
		if (logradouro != null) {
			
			/*
			 * Aqui vamos configuirar o endereco, pois o servico de inclusao de enderecos
			 * recuperar os IDS do bairro, cidade e pais.
			 */
			logradouro.addEndereco(enderecamentoService.inserir(logradouro.recuperarEndereco()));
			return  (T) logradouroDAO.alterar(logradouro);
		}
		return null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<? extends Logradouro> pesquisar (Integer id, Class<? extends Logradouro> classe) {
		String nomeTipoLogradouro = classe.getSimpleName();
		StringBuilder select = new StringBuilder();
		select.append("select c from ")
		.append(nomeTipoLogradouro)
		.append(" c inner join c.")
		.append(nomeTipoLogradouro.replace("Logradouro", "").toLowerCase())
		.append(" l where l.id =:id");
		
		return this.entityManager.createQuery(select.toString())
				.setParameter("id", id).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Logradouro> List<T> pesquisarAusentes(Integer id, Collection<T> listaLogradouro, Class<T> classe) {
		String nomeTipoLogradouro = classe.getSimpleName();
		StringBuilder select = new StringBuilder();
		select.append("select l from ")
		.append(nomeTipoLogradouro)
		.append(" l where l.")
		.append(nomeTipoLogradouro.replace("Logradouro", "").toLowerCase())
		.append(".id =:id ");
		
		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			select.append(" and l not in (:listaLogradouro) ");
		}
		
		final Query query = this.entityManager.createQuery(select.toString())
				.setParameter("id", id);
		
		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			query.setParameter("listaLogradouro", listaLogradouro);
		}
		return query.getResultList();
	}
	
	@Override
	public <T extends Logradouro> T pesquisarById(Integer idLogradouro, Class<T> classe) {
		String nomeTipoLogradouro = classe.getSimpleName();
		StringBuilder select = new StringBuilder();
		select.append("select c from ")
		.append(nomeTipoLogradouro)
		.append(" c where c.id = :idLogradouro ");
		return QueryUtil.gerarRegistroUnico(this.entityManager.createQuery(select.toString()).setParameter("idLogradouro", idLogradouro), 
				classe, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.plastecno.service.LogradouroService#removerAusentes(java.lang.Integer, java.util.Collection, java.lang.Class)
	 */
	@Override
	public <T extends Logradouro> void removerAusentes(Integer id, Collection<T> listaLogradouro, Class<T> classe) {
		List<? extends Logradouro> listaLogradouroCadastrado = this.pesquisarAusentes(id, listaLogradouro, classe);
		for (Logradouro logradouro : listaLogradouroCadastrado) {
			this.entityManager.remove(logradouro);
		}
		
	}
	
	@Override
	public void validarListaLogradouroPreenchida(Collection<? extends Logradouro> listaLogradouro) throws BusinessException {
		Set<TipoLogradouro> listaTipoLogradouroNaoPreenchido = new HashSet<TipoLogradouro>();
		listaTipoLogradouroNaoPreenchido.add(TipoLogradouro.COBRANCA);
		listaTipoLogradouroNaoPreenchido.add(TipoLogradouro.ENTREGA);
		listaTipoLogradouroNaoPreenchido.add(TipoLogradouro.FATURAMENTO);

		if (listaLogradouro != null) {
			for (Logradouro logradouro : listaLogradouro) {
				listaTipoLogradouroNaoPreenchido.remove(logradouro
						.getTipoLogradouro());
			}
		}

		if (!listaTipoLogradouroNaoPreenchido.isEmpty()) {
			List<String> listaMensagem = new ArrayList<String>();
			for (TipoLogradouro tipo : listaTipoLogradouroNaoPreenchido) {
				listaMensagem.add("É obrigatorio logradouro do tipo " + tipo);
			}
			throw new InformacaoInvalidaException(listaMensagem);
		}
	}
}
