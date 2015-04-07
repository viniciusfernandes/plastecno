package br.com.plastecno.service.impl;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.dao.ComissaoDAO;
import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.exception.BusinessException;

@Stateless
public class ComissaoServiceImpl implements ComissaoService {
	private ComissaoDAO comissaoDAO;

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	@EJB
	private UsuarioService usuarioService;

	@EJB
	private MaterialService materialService;

	@PostConstruct
	public void init() {
		comissaoDAO = new ComissaoDAO(entityManager);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserir(Comissao comissao) throws BusinessException {
		boolean isVendedorNulo = comissao.getIdVendedor() == null;
		boolean isMaterialNulo = comissao.getIdMaterial() == null;
		boolean isFormaNulo = comissao.getIdFormaMaterial() == null;
		if (!isVendedorNulo && !usuarioService.isVendedorAtivo(comissao.getIdVendedor())) {
			throw new BusinessException("O vendedor de código No. " + comissao.getIdVendedor()
					+ " não existe no sistema ou não tem o perfil de vendedor");
		}

		if (!isMaterialNulo && !materialService.isMaterialExistente(comissao.getIdMaterial())) {
			throw new BusinessException("O material de código No. " + comissao.getIdVendedor() + " não existe no sistema");
		}

		boolean isInvalido = isFormaNulo && isMaterialNulo && isVendedorNulo;
		if (isInvalido) {
			throw new BusinessException("É obrigatório o preenchimento do vendedor, forma de material ou material");
		}

		Comissao comissaoAnterior = pesquisarComissaoVigente(comissao.getIdVendedor(), comissao.getIdMaterial(),
				comissao.getIdFormaMaterial());
		if (comissaoAnterior != null) {
			comissaoAnterior.setDataFim(new Date());
			comissaoDAO.alterar(comissaoAnterior);
		}
		return comissaoDAO.inserir(comissao).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Comissao pesquisarById(Integer idComissao) {
		return comissaoDAO.pesquisarById(idComissao);
	}

	@Override
	public Comissao pesquisarComissaoVigente(Integer idVendedor, Integer idMaterial, Integer idFormaMaterial) {
		return comissaoDAO.pesquisarComissaoVigente(idVendedor, idMaterial, idFormaMaterial);
	}
}
