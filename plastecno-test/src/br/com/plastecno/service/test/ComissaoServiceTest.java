package br.com.plastecno.service.test;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PerfilAcessoService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;

public class ComissaoServiceTest extends AbstractTest {
	private ComissaoService comissaoService;
	private MaterialService materialService;
	private UsuarioService usuarioService;
	private PerfilAcessoService perfilAcessoService;

	private Material gerarMaterial() {
		Representada representada = eBuilder.buildRepresentada();
		Material material = eBuilder.buildMaterial();
		List<Material> lista = materialService.pesquisarBySigla(material.getSigla());
		if (lista.isEmpty()) {
			material.addRepresentada(representada);
			try {
				material.setId(materialService.inserir(material));
			} catch (BusinessException e) {
				printMensagens(e);
			}
		} else {
			material = lista.get(0);
		}
		return material;
	}

	private Usuario gerarVendedor() {
		Usuario vendedor = eBuilder.buildVendedor();
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		for (PerfilAcesso perfil : vendedor.getListaPerfilAcesso()) {
			perfilAcessoService.inserir(perfil);
		}
		return vendedor;
	}

	@Override
	public void init() {
		comissaoService = ServiceBuilder.buildService(ComissaoService.class);
		materialService = ServiceBuilder.buildService(MaterialService.class);
		usuarioService = ServiceBuilder.buildService(UsuarioService.class);
		perfilAcessoService = ServiceBuilder.buildService(PerfilAcessoService.class);
	}

	@Test
	public void testInclusaoComissaoFormaMaterial() {
		Comissao comissao = new Comissao(0.1, new Date());
		comissao.setIdFormaMaterial(FormaMaterial.BQ.indexOf());
		try {
			comissaoService.inserir(comissao);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoComissaoInvalido() {
		Comissao comissao = new Comissao(0.1, new Date());
		boolean throwed = false;
		try {
			comissaoService.inserir(comissao);
		} catch (BusinessException e) {
			throwed = true;
		}
		Assert.assertTrue("Ao menos um dos atributos relacionados aos indices de pesquisa deve ser preenchido", throwed);
	}

	@Test
	public void testInclusaoComissaoMaterial() {
		Comissao comissao = new Comissao(0.1, new Date());
		comissao.setIdMaterial(gerarMaterial().getId());
		try {
			comissaoService.inserir(comissao);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoComissaoMaterialInexistente() {
		Comissao comissao = new Comissao(0.1, new Date());
		comissao.setIdMaterial(-1);
		boolean throwed = false;
		try {
			comissaoService.inserir(comissao);
		} catch (BusinessException e) {
			throwed = true;
		}
		Assert.assertTrue("Nao se pode cadastrar uma comissao para material inexistente", throwed);
	}

	@Test
	public void testInclusaoComissaoVendedor() {
		Comissao comissao = new Comissao(0.1, new Date());
		comissao.setIdVendedor(gerarVendedor().getId());
		try {
			comissaoService.inserir(comissao);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoComissaoVendedorInexistente() {
		Comissao comissao = new Comissao(0.1, new Date());
		comissao.setIdVendedor(-1);
		boolean throwed = false;
		try {
			comissaoService.inserir(comissao);
		} catch (BusinessException e) {
			throwed = true;
		}
		Assert.assertTrue("Nao se pode cadastrar uma comissao para vendedor inexistente", throwed);
	}

	@Test
	public void testInclusaoNovaVersaoComissaoVendedor() {
		Integer idVendedor = gerarVendedor().getId();
		Comissao c1 = new Comissao(0.1, TestUtils.gerarDataAnterior());
		c1.setIdVendedor(idVendedor);
		try {
			comissaoService.inserir(c1);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Comissao c2 = new Comissao(0.2, new Date());
		c2.setIdVendedor(idVendedor);
		try {
			comissaoService.inserir(c2);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Assert.assertNotEquals("As comissoes nao podem ser a mesmas depois do versionamento", c1.getId(), c2.getId());
		Assert.assertNotNull("A data final nao pode ser nula apos o versionamento", c1.getDataFim());
		Assert.assertNull("A data final deve ser nula apos o versionamento", c2.getDataFim());

		Comissao comissaoVigente = comissaoService.pesquisarComissaoVigente(idVendedor, null, null);
		Assert.assertEquals("As comissoes vigente deve ser a mesma que a ultima versao de comissao inserida",
				comissaoVigente.getId(), c2.getId());

	}
}
