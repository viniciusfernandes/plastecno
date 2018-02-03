package br.com.svr.service.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.svr.service.ClienteService;
import br.com.svr.service.EnderecamentoService;
import br.com.svr.service.constante.TipoLogradouro;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.Endereco;
import br.com.svr.service.entity.LogradouroCliente;
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
		Cliente c = eBuilder.buildClienteVendedor();
		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoClienteLogradouroInexistente() {
		LogradouroCliente l = eBuilder.buildLogradouroCliente(TipoLogradouro.FATURAMENTO);
		l.setCep("09922333");
		l.setEndereco("Rua Nova Petropolis");
		l.setBairro("Centro");

		Cliente c = eBuilder.buildClienteVendedor();
		c.addLogradouro(l);

		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Endereco e = enderecamentoService.pesquisarByCep(l.getCep());
		assertNotNull("O endereco do cliente nao existe na tabela de logradouro e deveria ter sido incluido", e);
	}

	@Test
	public void testInclusaoClienteLogradouroExistente() {
		LogradouroCliente l = eBuilder.buildLogradouroCliente(TipoLogradouro.FATURAMENTO);
		l.setCep("09922333");
		l.setEndereco("Rua Nova Petropolis");
		l.setBairro("Centro");
		Endereco end = null;
		try {
			end = enderecamentoService.inserir(l.gerarEndereco());
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Cliente c = eBuilder.buildClienteVendedor();
		c.addLogradouro(l);

		try {
			clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Endereco e = enderecamentoService.pesquisarByCep(l.getCep());
		assertTrue("O endereco do cliente ja existia existe na tabela de logradouro e deve ser igual ao pesquisado",
				e == end);
	}

}
