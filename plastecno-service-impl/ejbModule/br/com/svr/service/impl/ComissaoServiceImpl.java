package br.com.svr.service.impl;

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

import br.com.svr.service.ComissaoService;
import br.com.svr.service.MaterialService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.constante.FormaMaterial;
import br.com.svr.service.constante.TipoPedido;
import br.com.svr.service.dao.ComissaoDAO;
import br.com.svr.service.entity.Comissao;
import br.com.svr.service.entity.Material;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.BusinessException;

@Stateless
public class ComissaoServiceImpl implements ComissaoService {
	private ComissaoDAO comissaoDAO;

	@PersistenceContext(name = "svr")
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
		if (isVendedorNulo) {
			throw new BusinessException("O id do vendedor não deve ser nulo.");
		}

		if (!isVendedorNulo && !usuarioService.isVendedorAtivo(comissao.getIdVendedor())) {
			Usuario u = usuarioService.pesquisarUsuarioResumidoById(comissao.getIdVendedor());
			throw new BusinessException("O vendedor " + u.getNomeCompleto() + " não está ativo no sistema.");
		}

		if (!isMaterialNulo && !materialService.isMaterialExistente(comissao.getIdMaterial())) {
			throw new BusinessException("O material de código No. " + comissao.getIdVendedor()
					+ " não existe no sistema");
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
	public Integer inserirComissaoProduto(FormaMaterial formaMaterial, Integer idMaterial, Double comissaoRevenda)
			throws BusinessException {
		Comissao comissao = new Comissao(comissaoRevenda, new Date());
		if (formaMaterial != null) {
			comissao.setIdFormaMaterial(formaMaterial.indexOf());
		}
		comissao.setIdMaterial(idMaterial);
		return inserir(comissao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirComissaoRevendaVendedor(Integer idVendedor, Double comissaoRevenda) throws BusinessException {
		return inserirComissaoVendedor(idVendedor, comissaoRevenda, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirComissaoVendedor(Integer idVendedor, Double comissaoRevenda, Double comissaoRepresentacao)
			throws BusinessException {
		Comissao comissao = new Comissao(comissaoRevenda, comissaoRepresentacao, new Date());
		comissao.setIdVendedor(idVendedor);
		return inserir(comissao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double pesquisarAliquotaComissaoByIdVendedor(Integer idVendedor, TipoPedido tipoPedido) {
		return comissaoDAO.pesquisarAliquotaComissaoByIdVendedor(idVendedor, tipoPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Comissao pesquisarAliquotaComissaoVigenteVendedor(Integer idVendedor) {
		return comissaoDAO.pesquisarComissaoVigenteVendedor(idVendedor);
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
}
