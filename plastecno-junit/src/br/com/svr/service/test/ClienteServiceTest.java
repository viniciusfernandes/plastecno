package br.com.svr.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import br.com.svr.service.ClienteService;
import br.com.svr.service.EnderecamentoService;
import br.com.svr.service.constante.TipoLogradouro;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.Endereco;
import br.com.svr.service.entity.LogradouroCliente;
import br.com.svr.service.entity.RamoAtividade;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.test.builder.ServiceBuilder;

public class ClienteServiceTest extends AbstractTest {
	private ClienteService clienteService;
	private EnderecamentoService enderecamentoService;

	public ClienteServiceTest() {
		clienteService = ServiceBuilder.buildService(ClienteService.class);
		enderecamentoService = ServiceBuilder.buildService(EnderecamentoService.class);
	}

	@Test
	public void testInclusaoCliente() {
		RamoAtividade ramo = gPedido.gerarRamoAtividade();
		Cliente c = eBuilder.buildCliente();
		c.setRamoAtividade(ramo);

		Usuario vend = gPedido.gerarVendedor();
		c.setVendedor(vend);
		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoClienteComAlteracaoLogradouro() {
		RamoAtividade ramo = gPedido.gerarRamoAtividade();
		Cliente c = eBuilder.buildCliente();
		c.setRamoAtividade(ramo);
		c.setListaLogradouro(null);

		Usuario vend = gPedido.gerarVendedor();
		c.setVendedor(vend);

		LogradouroCliente lFat = eBuilder.buildLogradouroCliente(TipoLogradouro.FATURAMENTO);
		c.addLogradouro(lFat);

		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<LogradouroCliente> lLogr = clienteService.pesquisarLogradouroCliente(c.getId());
		assertEquals("O cliente contem apenas 1 logradouro nessa inclusao", (Integer) 1, (Integer) lLogr.size());

		// Editando o enderedo de faturamento
		lFat = lLogr.get(0);
		lFat.setCep("09943555");
		lFat.setEndereco("Avenida Tuiuti Ferraz");
		c.setListaLogradouro(null);
		c.addLogradouro(lFat);
		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		lLogr = clienteService.pesquisarLogradouroCliente(c.getId());
		assertEquals("O cliente contem apenas 1 logradouro nessa inclusao", (Integer) 1, (Integer) lLogr.size());

		LogradouroCliente lCobr = eBuilder.buildLogradouroCliente(TipoLogradouro.COBRANCA);
		c = clienteService.pesquisarById(c.getId());
		// AQui o cliente tem um logradouro de faturamento e quando for inviado
		// ao banco de dados ele tera 1 logr de faturamento. Totalizando 2
		// logradouros.
		c.addLogradouro(lCobr);

		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		lLogr = clienteService.pesquisarLogradouroCliente(c.getId());
		assertEquals("O cliente contem apenas 2 logradouro nessa inclusao.", (Integer) 2, (Integer) lLogr.size());

		LogradouroCliente lCom = eBuilder.buildLogradouroCliente(TipoLogradouro.COMERCIAL);
		c = clienteService.pesquisarById(c.getId());
		// Limpando a lista de logradouros e inserindo novos, mas no sistema
		// permanecerao os antigos.
		c.setListaLogradouro(null);
		c.addLogradouro(lCom);

		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		lLogr = clienteService.pesquisarLogradouroCliente(c.getId());
		assertEquals("O cliente contem apenas 3 logradouro nessa inclusao.", (Integer) 3, (Integer) lLogr.size());

	}

	@Test
	public void testInclusaoClienteLogradouroExistente() {
		LogradouroCliente l = eBuilder.buildLogradouroCliente(TipoLogradouro.FATURAMENTO);
		l.setCep("09922333");
		l.setEndereco("Rua Nova Petropolis");
		l.setBairro("Centro");

		Endereco end = l.gerarEndereco();
		try {
			// Gerando o endereco na base do sistema;
			end = enderecamentoService.inserir(end);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Cliente c = eBuilder.buildCliente();
		c.setListaContato(null);
		c.setRamoAtividade(gPedido.gerarRamoAtividade());
		c.setVendedor(gPedido.gerarVendedor());
		c.addLogradouro(l);

		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Endereco e = enderecamentoService.pesquisarByCep(l.getCep());
		assertNotNull(
				"O endereco do cliente ja existia existe na tabela de logradouro e deve ser retornado na pesquisa", e);
	}

	@Test
	public void testInclusaoClienteLogradouroInexistente() {
		LogradouroCliente l = eBuilder.buildLogradouroCliente(TipoLogradouro.FATURAMENTO);
		l.setCep("09922333");
		l.setEndereco("Rua Nova Petropolis");
		l.setBairro("Centro");

		Cliente c = eBuilder.buildCliente();
		c.setListaContato(null);
		c.setRamoAtividade(gPedido.gerarRamoAtividade());
		c.setVendedor(gPedido.gerarVendedor());
		c.addLogradouro(l);

		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Endereco e = enderecamentoService.pesquisarByCep(l.getCep());
		assertNotNull("O endereco do cliente nao existe na tabela de logradouro e deveria ter sido incluido", e);
	}

}
