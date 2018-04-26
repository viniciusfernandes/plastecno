package br.com.svr.service.test;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.com.svr.service.ComissaoService;
import br.com.svr.service.constante.FormaMaterial;
import br.com.svr.service.entity.Comissao;
import br.com.svr.service.entity.Material;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.test.builder.ServiceBuilder;

public class ComissaoServiceTest extends AbstractTest {
	private ComissaoService comissaoService;

	public ComissaoServiceTest() {
		comissaoService = ServiceBuilder.buildService(ComissaoService.class);
	}

	@Test
	public void testInclusaoComissaoFormaMaterial() {
		Comissao comissao = new Comissao(0.1, new Date());
		Usuario vend = gPedido.gerarVendedor();
		comissao.setIdVendedor(vend.getId());
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
		Representada revend = gRepresentada.gerarRevendedor();
		Material mat = gPedido.gerarMaterial(revend);
		Usuario vend = gPedido.gerarVendedor();

		comissao.setIdVendedor(vend.getId());
		comissao.setIdMaterial(mat.getId());

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
		comissao.setIdVendedor(gPedido.gerarVendedor().getId());
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
		Integer idVendedor = gPedido.gerarVendedor().getId();
		Comissao c1 = new Comissao(0.1, TestUtils.gerarDataOntem());
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

		Comissao comissaoVigente = comissaoService.pesquisarComissaoVigenteVendedor(idVendedor);
		Assert.assertEquals("As comissoes vigente deve ser a mesma que a ultima versao de comissao inserida",
				comissaoVigente.getId(), c2.getId());

	}
}
