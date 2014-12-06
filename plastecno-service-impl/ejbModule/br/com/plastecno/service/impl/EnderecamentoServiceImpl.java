package br.com.plastecno.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.entity.Bairro;
import br.com.plastecno.service.entity.Cidade;
import br.com.plastecno.service.entity.Endereco;
import br.com.plastecno.service.entity.Pais;
import br.com.plastecno.service.entity.UF;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class EnderecamentoServiceImpl implements EnderecamentoService {

	@PersistenceContext(unitName="plastecno")
	private EntityManager entityManager;
	
	@Override
	public Endereco inserir(Endereco endereco) throws BusinessException {
		ValidadorInformacao.validar(endereco);
	
		/*
		 *  Vamos sempre atualizar o endereco enviado pois o cliente pode indicar que
		 *  o nome da rua foi alterado e o sistema nao reflete essa alteracao.
		 */
		Bairro bairro = endereco.getBairro();
		Cidade cidade = bairro.getCidade();
		Pais pais = cidade.getPais(); 
		
		pais.setId(this.pesquisarIdPaisByDescricao(pais.getDescricao()));
		cidade.setId(this.pesquisarIdCidadeByDescricao(cidade.getDescricao(), pais.getId()));
		bairro.setId(this.pesquisarIdBairroByDescricao(bairro.getDescricao(), cidade.getId()));
		/*
		 * No caso em que pais, cidade ou bairro existam, devemos fazer um merge, pois
		 * do contrario teremos um deatched obejct 
		 */
		endereco = this.entityManager.merge(endereco);
		this.inserirUF(cidade.getUf(), endereco.getCidade().getPais());
		return endereco;		
	}
	
	private void inserirUF(String sigla, Pais pais) {
		boolean isExistente = 
				QueryUtil.gerarRegistroUnico(
						this.entityManager.createQuery("select u from UF u where u.sigla = :sigla and u.pais.id = :idPais ")
						.setParameter("sigla", sigla)
						.setParameter("idPais", pais.getId()), UF.class, null) != null;


		if (!isExistente) {
			this.entityManager.merge(new UF(sigla, pais));
		}
	}

	@Override
	public boolean isCepExitente(String cep) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select e.cep from Endereco e where e.cep =:cep ")
				.setParameter("cep", cep), Boolean.class, false, true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see br.com.plastecno.service.CEPService#pesquisarBairroByCep(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Bairro> pesquisarBairroByCep(String cep) {
		StringBuilder select = new StringBuilder("select new Bairro(b.id, b.descricao, c.id, c.descricao) ");
		select.append("from Endereco e ")
		.append("inner join e.bairro b ")
		.append("inner join b.cidade c ")
		.append("where e.cep like :cep ")
		.append("group by b.id, b.descricao, c.id, c.descricao ")
		.append("order by c.descricao, b.descricao ");
		return this.entityManager.createQuery(select.toString())
				.setParameter("cep", cep+"%").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Bairro> pesquisarBairroById(List<Integer> listaIdBairro) {
		StringBuilder select = new StringBuilder("select new Bairro(b.id, b.descricao, c.id, c.descricao) ");
		select.append("from Bairro b ")
		.append("inner join b.cidade c ")
		.append("where b.id in (:listaIdBairro) ")
		.append("order by c.descricao desc, b.descricao desc ");
		return this.entityManager.createQuery(select.toString())
				.setParameter("listaIdBairro", listaIdBairro).getResultList();
	}

	@Override
	public Endereco pesquisarByCep(String cep) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select e from Endereco e where e.cep =:cep ")
				.setParameter("cep", cep), Endereco.class, null);
	}
	
	@SuppressWarnings("unchecked")
	private Integer pesquisarIdBairroByDescricao(String descricao, Integer idCidade) {
		List<Integer> lista = this.entityManager.createQuery(
				"select b.id from Bairro b where b.cidade.id = :idCidade and b.descricao = :descricao ")
				.setParameter("descricao", descricao)
				.setParameter("idCidade", idCidade).getResultList();
		return lista.isEmpty() ? null : lista.get(0);
	}
	
	@SuppressWarnings("unchecked")
	private Integer pesquisarIdCidadeByDescricao(String descricao, Integer idPais) {
		List<Integer> lista = this.entityManager.createQuery(
				"select c.id from Cidade c where c.pais.id = :idPais and c.descricao = :descricao ")
				.setParameter("descricao", descricao)
				.setParameter("idPais", idPais).getResultList();
		return lista.isEmpty() ? null : lista.get(0);
		
	}
	
	@SuppressWarnings("unchecked")
	private Integer pesquisarIdPaisByDescricao(String descricao) {
		List<Integer> lista = this.entityManager.createQuery("select p.id from Pais p where p.descricao = :descricao")
				.setParameter("descricao", descricao).getResultList();
		return lista.isEmpty() ? null : lista.get(0);
	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UF> pesquisarUF() {
		return this.entityManager.createQuery("select uf from UF uf ").getResultList();
	}
}
