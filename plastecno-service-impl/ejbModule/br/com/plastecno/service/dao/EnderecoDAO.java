package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.Endereco;
import br.com.plastecno.service.entity.UF;
import br.com.plastecno.service.impl.util.QueryUtil;

public class EnderecoDAO extends GenericDAO<Endereco> {

	public EnderecoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void inserirUF(UF uf) {
		entityManager.merge(uf);
	}
	public boolean isUFExistente(String sigla, Integer idPais) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select u from UF u where u.sigla = :sigla and u.pais.id = :idPais ")
						.setParameter("sigla", sigla).setParameter("idPais", idPais), UF.class, null) != null;

	}

	public Endereco pesquisarByCep(String cep) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select e from Endereco e where e.cep =:cep ").setParameter("cep", cep),
				Endereco.class, null);
	}

	public List<String> pesquisarCEPExistente(List<String> listaCep) {
		return entityManager.createQuery("select e.cep from Endereco e where e.cep in(:listaCep)", String.class)
				.setParameter("listaCep", listaCep).getResultList();

	}

	@SuppressWarnings("unchecked")
	public Integer pesquisarIdBairroByDescricao(String descricao, Integer idCidade) {
		List<Integer> lista = this.entityManager
				.createQuery("select b.id from Bairro b where b.cidade.id = :idCidade and b.descricao = :descricao ")
				.setParameter("descricao", descricao).setParameter("idCidade", idCidade).getResultList();
		return lista.isEmpty() ? null : lista.get(0);
	}

	@SuppressWarnings("unchecked")
	public Integer pesquisarIdCidadeByDescricao(String descricao, Integer idPais) {
		List<Integer> lista = this.entityManager
				.createQuery("select c.id from Cidade c where c.pais.id = :idPais and c.descricao = :descricao ")
				.setParameter("descricao", descricao).setParameter("idPais", idPais).getResultList();
		return lista.isEmpty() ? null : lista.get(0);

	}

	@SuppressWarnings("unchecked")
	public Integer pesquisarIdPaisByDescricao(String descricao) {
		List<Integer> lista = this.entityManager.createQuery("select p.id from Pais p where p.descricao = :descricao")
				.setParameter("descricao", descricao).getResultList();
		return lista.isEmpty() ? null : lista.get(0);

	}

}
