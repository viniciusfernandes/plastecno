package br.com.plastecno.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.dao.EnderecoDAO;
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

	private EnderecoDAO enderecoDAO;
	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	@PostConstruct
	public void init() {
		enderecoDAO = new EnderecoDAO(entityManager);
	}

	@Override
	public Endereco inserir(Endereco endereco) throws BusinessException {
		ValidadorInformacao.validar(endereco);

		/*
		 * Vamos sempre atualizar o endereco enviado pois o cliente pode indicar
		 * que o nome da rua foi alterado e o sistema nao reflete essa
		 * alteracao.
		 */
		Bairro bairro = endereco.getBairro();
		Cidade cidade = bairro.getCidade();
		Pais pais = cidade.getPais();

		pais.setId(enderecoDAO.pesquisarIdPaisByDescricao(pais.getDescricao()));
		cidade.setId(enderecoDAO.pesquisarIdCidadeByDescricao(cidade.getDescricao(), pais.getId()));
		bairro.setId(enderecoDAO.pesquisarIdBairroByDescricao(bairro.getDescricao(), cidade.getId()));
		/*
		 * No caso em que pais, cidade ou bairro existam, devemos fazer um
		 * merge, pois do contrario teremos um deatched obejct
		 */
		endereco = enderecoDAO.alterar(endereco);
		inserirUF(cidade.getUf(), endereco.getCidade().getPais());
		return endereco;
	}

	private void inserirUF(String sigla, Pais pais) {

		if (!enderecoDAO.isUFExistente(sigla, pais.getId())) {
			enderecoDAO.inserirUF(new UF(sigla, pais));
		}
	}

	@Override
	public boolean isCepExitente(String cep) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select e.cep from Endereco e where e.cep =:cep ").setParameter("cep",
						cep), Boolean.class, false, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.plastecno.service.CEPService#pesquisarBairroByCep(java.lang.String
	 * )
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Bairro> pesquisarBairroByCep(String cep) {
		StringBuilder select = new StringBuilder("select new Bairro(b.id, b.descricao, c.id, c.descricao) ");
		select.append("from Endereco e ").append("inner join e.bairro b ").append("inner join b.cidade c ")
				.append("where e.cep like :cep ").append("group by b.id, b.descricao, c.id, c.descricao ")
				.append("order by c.descricao, b.descricao ");
		return this.entityManager.createQuery(select.toString()).setParameter("cep", cep + "%").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Bairro> pesquisarBairroById(List<Integer> listaIdBairro) {
		StringBuilder select = new StringBuilder("select new Bairro(b.id, b.descricao, c.id, c.descricao) ");
		select.append("from Bairro b ").append("inner join b.cidade c ").append("where b.id in (:listaIdBairro) ")
				.append("order by c.descricao desc, b.descricao desc ");
		return this.entityManager.createQuery(select.toString()).setParameter("listaIdBairro", listaIdBairro)
				.getResultList();
	}

	@Override
	public Endereco pesquisarByCep(String cep) {
		return enderecoDAO.pesquisarByCep(cep);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<String> pesquisarCEPExistente(Set<String> listaCep) {
		if (listaCep == null || listaCep.isEmpty()) {
			return new ArrayList<String>();
		}
		return enderecoDAO.pesquisarCEPExistente(listaCep);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UF> pesquisarUF() {
		return this.entityManager.createQuery("select uf from UF uf ").getResultList();
	}
}
