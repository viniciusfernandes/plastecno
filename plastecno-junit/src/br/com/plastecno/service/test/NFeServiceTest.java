package br.com.plastecno.service.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.NFeServiceImpl;
import br.com.plastecno.service.impl.PedidoServiceImpl;
import br.com.plastecno.service.nfe.DadosNFe;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.IdentificacaoNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.test.builder.ServiceBuilder;

public class NFeServiceTest extends AbstractTest {

	private NFeService nFeService;

	private PedidoService pedidoService;

	private PedidoServiceTest pedidoServiceTest;

	public NFeServiceTest() {
		pedidoServiceTest = new PedidoServiceTest();
		nFeService = ServiceBuilder.buildService(NFeService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
	}

	private Integer gerarPedidoRevenda() {
		Pedido p = pedidoServiceTest.gerarPedidoRevendaComItem();
		Integer id = p.getId();
		try {
			pedidoService.enviarPedido(id, new byte[] {});
			return id;
		} catch (BusinessException e) {
			printMensagens(e);
			return null;
		}
	}

	@Test
	public void testX() {
		Integer idPedido = gerarPedidoRevenda();
		
		Cliente cliente = pedidoService.pesquisarClienteResumidoByIdPedido(idPedido);
		List<DuplicataNFe> listaDuplicata = nFeService.gerarDuplicataByIdPedido(idPedido);

		Assert.assertTrue("A lista de duplicatas deve conter ao menos 1 elemento para pedidos a prazo",
				listaDuplicata != null && listaDuplicata.size() >= 1);

		Object[] telefone = pedidoService.pesquisarTelefoneContatoByIdPedido(idPedido);

		IdentificacaoNFe i = new IdentificacaoNFe();
		IdentificacaoDestinatarioNFe iDest = new IdentificacaoDestinatarioNFe();
		DadosNFe d = new DadosNFe();
		d.setIdentificacaoNFe(i);
		d.setIdentificacaoDestinatarioNFe(iDest);
		NFe nFe = new NFe(d);

		try {
			nFeService.emitirNFeEntrada(nFe, idPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}
}
