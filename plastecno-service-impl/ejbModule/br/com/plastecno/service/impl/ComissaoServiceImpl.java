package br.com.plastecno.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.dao.ComissaoDAO;
import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;

@Stateless
public class ComissaoServiceImpl implements ComissaoService {
	private ComissaoDAO comissaoDAO;

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	@EJB
	private MaterialService materialService;

	@EJB
	private PedidoService pedidoService;

	@EJB
	private UsuarioService usuarioService;

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

		Comissao comissaoAnterior = null;
		if (comissao.isComissaoVendedor()) {
			comissaoAnterior = pesquisarComissaoVigenteVendedor(comissao.getIdVendedor());
		} else {
			comissaoAnterior = pesquisarComissaoVigenteProduto(comissao.getIdMaterial(), comissao.getIdFormaMaterial());
		}

		if (comissaoAnterior != null) {
			comissaoAnterior.setDataFim(new Date());
			comissaoDAO.alterar(comissaoAnterior);
		}
		return comissaoDAO.inserir(comissao).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirComissaoProduto(FormaMaterial formaMaterial, Integer idMaterial, Double valorComissao)
			throws BusinessException {
		Comissao comissao = new Comissao(valorComissao, new Date());
		if (formaMaterial != null) {
			comissao.setIdFormaMaterial(formaMaterial.indexOf());
		}
		comissao.setIdMaterial(idMaterial);
		return inserir(comissao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirComissaoVendedor(Integer idVendedor, Double valorComissao) throws BusinessException {
		Comissao comissao = new Comissao(valorComissao, new Date());
		comissao.setIdVendedor(idVendedor);
		return inserir(comissao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Comissao pesquisarById(Integer idComissao) {
		return comissaoDAO.pesquisarById(idComissao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Comissao> pesquisarComissaoByIdVendedor(Integer idVendedor) {
		if (idVendedor == null) {
			return new ArrayList<Comissao>();
		}
		Usuario vendedor = usuarioService.pesquisarUsuarioResumidoById(idVendedor);
		if (vendedor == null) {
			return new ArrayList<Comissao>();
		}
		String nomeVendedor = vendedor.getNomeCompleto();
		List<Comissao> l = comissaoDAO.pesquisarComissaoByIdVendedor(idVendedor);
		for (Comissao comissao : l) {
			comissao.setNomeVendedor(nomeVendedor);
		}
		return l;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Comissao> pesquisarComissaoByProduto(FormaMaterial formaMaterial, Integer idMaterial) {
		List<Comissao> l = comissaoDAO.pesquisarComissaoByProduto(formaMaterial, idMaterial);
		Material m = null;
		StringBuilder descricao = null;
		for (Comissao c : l) {
			descricao = new StringBuilder();
			if (c.getIdFormaMaterial() != null) {
				descricao.append(FormaMaterial.get(c.getIdFormaMaterial()).getDescricao()).append(" - ");
			}
			if (c.getIdMaterial() != null) {
				m = materialService.pesquisarById(c.getIdMaterial());
				if (m != null) {
					descricao.append(m.getDescricao());
				}
			}
			c.setDescricaoProduto(descricao.toString());
		}
		return l;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Comissao pesquisarComissaoVigenteProduto(Integer idMaterial, Integer idFormaMaterial) {
		return comissaoDAO.pesquisarComissaoVigenteProduto(idMaterial, idFormaMaterial);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Comissao pesquisarComissaoVigenteVendedor(Integer idVendedor) {
		return comissaoDAO.pesquisarComissaoVigenteVendedor(idVendedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double pesquisarValorComissaoVigenteVendedor(Integer idVendedor) {
		Comissao comissao = comissaoDAO.pesquisarComissaoVigenteVendedor(idVendedor);
		return comissao == null ? 0d : comissao.getValor();

	}
}
