package br.com.svr.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import br.com.svr.service.NegociacaoService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.constante.SituacaoPedido;
import br.com.svr.service.constante.TipoVenda;
import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.crm.IndicadorCliente;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.test.builder.ServiceBuilder;

public class NegociacaoServiceTest extends AbstractTest {
	private NegociacaoService negociacaoService;
	private PedidoService pedidoService;

	public NegociacaoServiceTest() {
		negociacaoService = ServiceBuilder.buildService(NegociacaoService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
	}

	@Test
	public void testAceiteNegociacao() {
		Pedido o = null;
		try {
			o = gPedido.gerarOrcamentoComItem();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		List<Negociacao> lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Deve existir apenas 1 negociacao por orcamento incluido.", (Integer) 1, (Integer) lNeg.size());

		Negociacao n = lNeg.get(0);
		try {
			negociacaoService.aceitarNegocicacaoEOrcamentoByIdNegociacao(n.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Nao deve existir negociacao apos o orcamento aceito.", (Integer) 0, (Integer) lNeg.size());
	}

	@Test
	public void testAceiteOrcamentoENegociacao() {
		Pedido o = null;
		try {
			o = gPedido.gerarOrcamentoComItem();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		List<Negociacao> lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Deve existir apenas 1 negociacao por orcamento incluido.", (Integer) 1, (Integer) lNeg.size());

		try {
			pedidoService.aceitarOrcamentoENegociacaoByIdOrcamento(o.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Nao deve existir negociacao apos o orcamento aceito.", (Integer) 0, (Integer) lNeg.size());
	}

	@Test
	public void testCancelamentoNegociacao() {
		Pedido o = null;
		try {
			o = gPedido.gerarOrcamentoComItem();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		try {
			pedidoService.enviarPedido(o.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Negociacao n = negociacaoService.pesquisarNegociacaoByIdOrcamento(o.getId());
		assertNotNull("Todo orcamento enviado deve ter uma negociacao", n);

		TipoNaoFechamento tpNFechamento = TipoNaoFechamento.FORMA_PAGAMENTO;
		try {
			negociacaoService.cancelarNegocicacao(n.getId(), tpNFechamento);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		n = negociacaoService.pesquisarNegociacaoByIdOrcamento(o.getId());
		assertEquals("O tipo de nao fechamento na negociacao dever ser igual ao que foi cadastrado.", tpNFechamento,
				n.getTipoNaoFechamento());

		SituacaoPedido stOrc = pedidoService.pesquisarSituacaoPedidoById(o.getId());
		assertEquals("O orcamento deve ser cancelado apos o cancelamento da negociacao",
				SituacaoPedido.ORCAMENTO_CANCELADO, stOrc);
	}

	@Test
	public void testCancelamentoOrcamentoENegociacao() {
		Pedido o = null;
		try {
			o = gPedido.gerarOrcamentoComItem();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		try {
			pedidoService.enviarPedido(o.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Negociacao n = negociacaoService.pesquisarNegociacaoByIdOrcamento(o.getId());
		assertNotNull("Todo orcamento enviado deve ter uma negociacao", n);

		try {
			pedidoService.cancelarOrcamentoRemoverNegociacao(o.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		n = negociacaoService.pesquisarNegociacaoByIdOrcamento(o.getId());
		assertNull("Os orcamentos cancelados nao devem conter negociacao no sistema.", n);
	}

	@Test
	public void testCriacaoIndicadorAceiteOrcamento() {
		Pedido orc = null;
		try {
			orc = gPedido.gerarOrcamentoComItem();
		} catch (BusinessException e) {
			printMensagens(e);
		}
		IndicadorCliente ind = negociacaoService.pesquisarIndicadorByIdCliente(orc.getCliente().getId());
		assertNotNull("Na inclusao de um orcamento deve ser criado um indicador do cliente", ind);
		assertEquals("Na inclusao de um orcamento quantidade de orcamentos deve ser igual a 1", (Integer) 1,
				(Integer) ind.getQuantidadeOrcamentos());
		assertEquals("Na inclusao de um orcamento quantidade de vendas deve ser igual a 0", (Long) 0L,
				(Long) ind.getQuantidadeVendas());

		Integer idPed = null;
		try {
			idPed = pedidoService.aceitarOrcamento(orc.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ind = recarregarEntidade(IndicadorCliente.class, ind.getId());
		assertEquals("Apos o aceite de um orcamento quantidade de orcamentos deve ser igual a 1", (Integer) 1,
				(Integer) ind.getQuantidadeOrcamentos());
		assertEquals("Apos o aceite de um orcamento quantidade de vendas deve ser igual a 0", (Long) 0L,
				(Long) ind.getQuantidadeVendas());

		try {
			pedidoService.enviarPedido(idPed, new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ind = recarregarEntidade(IndicadorCliente.class, ind.getId());
		assertEquals("Apos o envio de um pedido quantidade de orcamentos deve ser igual a 1", (Integer) 1,
				(Integer) ind.getQuantidadeOrcamentos());
		assertEquals("Apos o envio de um pedido quantidade de vendas deve ser igual a 1", (Long) 1L,
				(Long) ind.getQuantidadeVendas());
	}

	@Test
	public void testCriacaoIndicadorClienteInclusaoItemOrcamento() {
		Pedido orc = gPedido.gerarOrcamento();
		IndicadorCliente ind = negociacaoService.pesquisarIndicadorByIdCliente(orc.getCliente().getId());
		assertNotNull("Na inclusao orcamento o indicador do cliente deve ser criado", ind);

		ItemPedido i = gPedido.gerarItemPedido(orc.getRepresentada());
		try {
			pedidoService.inserirItemPedido(orc.getId(), i);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ind = negociacaoService.pesquisarIndicadorByIdCliente(orc.getCliente().getId());
		assertEquals("Na inclusao de um item de orcamento o indice de valor deve ser 0", (Double) 0d,
				(Double) ind.getIndiceConversaoValor());

		assertEquals("Na inclusao de um item de orcamento o indice de quantidade deve ser 1", (Double) 0d,
				(Double) ind.getIndiceConversaoQuantidade());
	}

	@Test
	public void testCriacaoIndicadorClienteMudancaValoresItemOrcamento() {
		Pedido orc = gPedido.gerarOrcamento();
		ItemPedido i = gPedido.gerarItemPedido(orc.getRepresentada());
		i.setQuantidade(100);
		i.setTipoVenda(TipoVenda.PECA);
		i.setPrecoVenda(10d);

		try {
			pedidoService.inserirItemPedido(orc.getId(), i);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		orc = recarregarEntidade(Pedido.class, orc.getId());
		IndicadorCliente ind = negociacaoService.pesquisarIndicadorByIdCliente(orc.getCliente().getId());
		assertEquals(
				"Na inclusao do primeiro item de orcamento o valor de orcamentos deve ser igual ao valor do orcamento sem frete",
				(Double) orc.calcularValorPedidoIPISemFrete(), (Double) ind.getValorOrcamentos());

		assertEquals("Na inclusao do primeiro item de orcamento o indice de quantidade deve ser 1", (Integer) 1,
				(Integer) ind.getQuantidadeOrcamentos());

		// reduzindo a quantidade do item para verificar o tratamento da reducao
		// do valor do orcamento.
		i.setQuantidade(1);
		try {
			pedidoService.inserirItemPedido(orc.getId(), i);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		orc = recarregarEntidade(Pedido.class, orc.getId());
		ind = recarregarEntidade(IndicadorCliente.class, ind.getId());

		assertEquals(
				"Na reducao do valor do item o valor anterior deve ser subtraido do valor dos orcamentos do indicador",
				(Double) orc.calcularValorPedidoIPISemFrete(), (Double) ind.getValorOrcamentos());

		ItemPedido i2 = i.clone();
		i2.setQuantidade(20);
		i2.setPrecoVenda(20d);
		i2.setAliquotaIPI(0d);
		i2.setTipoVenda(TipoVenda.PECA);

		double valOrc = ind.getValorOrcamentos() + 400d;

		try {
			pedidoService.inserirItemPedido(orc.getId(), i2);
		} catch (BusinessException e) {

			printMensagens(e);
		}
		ind = recarregarEntidade(IndicadorCliente.class, ind.getId());
		assertEquals("Na inclusao de novo item seu valor deve ser adicionado ao valor dos orcamentos do indicador",
				(Double) valOrc, (Double) ind.getValorOrcamentos());

		try {
			pedidoService.enviarPedido(orc.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals("No envio do orcamento os valores e quantidades de orcamentos devem permanecer os mesmos.",
				(Double) valOrc, (Double) ind.getValorOrcamentos());
		assertEquals("No envio do orcamento os valores e quantidades de orcamentos devem permanecer os mesmos.",
				(Integer) 1, (Integer) ind.getQuantidadeOrcamentos());

		assertEquals("No envio do orcamento os valores e quantidades de pedidos devem ser zero.", (Double) 0d,
				(Double) ind.getValorVendas());
		assertEquals("No envio do orcamento os valores e quantidades de pedidos devem ser zero.", (Long) 0L,
				(Long) ind.getQuantidadeVendas());
	}

	@Test
	public void testCriacaoIndicadorClientePedidoRevendaSemOrcamento() {
		Pedido p = gPedido.gerarPedidoRevenda();
		IndicadorCliente ind = negociacaoService.pesquisarIndicadorByIdCliente(p.getCliente().getId());
		assertNull("Na inclusao de pedido de revenda nao pode existir indicador do cliente", ind);

		ItemPedido i = gPedido.gerarItemPedido(p.getRepresentada());
		try {
			pedidoService.inserirItemPedido(p.getId(), i);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		ind = negociacaoService.pesquisarIndicadorByIdCliente(p.getCliente().getId());
		assertNull("Na inclusao do item do pedido de revenda nao pode existir indicador do cliente", ind);

		try {
			recarregarEntidade(Pedido.class, p.getId());
			pedidoService.enviarPedido(p.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ind = negociacaoService.pesquisarIndicadorByIdCliente(p.getCliente().getId());
		assertNotNull("No envio do pedido de revenda o indicador do cliente deve ser criado", ind);

		assertEquals("No envio do primeiro pedido do cliente o indice de valor deve ser 1", (Double) 1d,
				(Double) ind.getIndiceConversaoValor());
		assertEquals("No envio do primeiro pedido do cliente o indice de quantidade deve ser 1", (Double) 1d,
				(Double) ind.getIndiceConversaoValor());
	}

	@Test
	public void testInclusaoNegociacao() {
		Pedido o = null;
		try {
			o = gPedido.gerarOrcamentoComItem();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		Integer idNeg = null;
		try {
			idNeg = negociacaoService.inserirNegociacao(o.getId(), o.getCliente().getId(), o.getVendedor().getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Negociacao n = negociacaoService.pesquisarById(idNeg);

		assertEquals("A negociacao criada deve estar em aberto.", SituacaoNegociacao.ABERTO, n.getSituacaoNegociacao());
		assertEquals("A categoria da negociacao criada deve ser uma proposta ao cliente.",
				CategoriaNegociacao.PROPOSTA_CLIENTE, n.getCategoriaNegociacao());
		assertEquals("O vendedor da negociacao deve ser o mesmo do orcamento.", o.getVendedor().getId(),
				n.getIdVendedor());
		assertEquals("O id do orcamento da negociacao deve ser o mesmo do orcamento.", o.getId(), n.getOrcamento()
				.getId());
		assertEquals("O tipo de nao fechamento da negociacao deve ser OK na inclusao.", TipoNaoFechamento.OK,
				n.getTipoNaoFechamento());

	}

	@Test
	public void testInclusaoOrcamentoInclusaoNegociacao() {
		Pedido o = null;
		try {
			o = gPedido.gerarOrcamentoComItem();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		assertEquals("Para a inclusao de uma negociacao deve-se ter um orcamento em digitacao.",
				SituacaoPedido.ORCAMENTO_DIGITACAO, o.getSituacaoPedido());

		List<Negociacao> lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());

		assertEquals("Para cada orcamento incluido deve-se ter apenas uma negociacao incluida", new Integer(1),
				(Integer) lNeg.size());

		Negociacao n = recarregarEntidade(Negociacao.class, lNeg.get(0).getId());

		assertEquals("A negociacao criada deve estar em aberto.", SituacaoNegociacao.ABERTO, n.getSituacaoNegociacao());
		assertEquals("A categoria da negociacao criada deve ser uma proposta ao cliente.",
				CategoriaNegociacao.PROPOSTA_CLIENTE, n.getCategoriaNegociacao());
		assertEquals("O vendedor da negociacao deve ser o mesmo do orcamento.", o.getVendedor().getId(),
				n.getIdVendedor());
		assertEquals("O id do orcamento da negociacao deve ser o mesmo do orcamento.", o.getId(), n.getOrcamento()
				.getId());
		assertEquals("O tipo de nao fechamento da negociacao deve ser OK na inclusao.", TipoNaoFechamento.OK,
				n.getTipoNaoFechamento());

	}

	@Test
	public void testRecalculoIndicadorClienteEnvioPedido() {
		Pedido orc = null;
		try {
			orc = gPedido.gerarOrcamentoComItem();
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Negociacao n = negociacaoService.pesquisarNegociacaoByIdOrcamento(orc.getId());
		IndicadorCliente indCli = negociacaoService.pesquisarIndicadorByIdCliente(orc.getCliente().getId());

		assertNotNull("Apos inserido um orcamento deve existir uma negociacao", n);
		assertNotNull("Apos inserido um orcamento deve existir um indicador do cliente", indCli);

		double indValNeg = n.getIndiceConversaoValor();
		double indQtdeNeg = n.getIndiceConversaoQuantidade();

		double indVal = indCli.getIndiceConversaoValor();
		double indQtde = indCli.getIndiceConversaoQuantidade();

		assertTrue("Os valores dos indices de valor devem ser igual apos a inclusao de um orcamento",
				indValNeg == indVal);
		assertTrue("Os valores dos indices de quantidade devem ser igual apos a inclusao de um orcamento",
				indQtdeNeg == indQtde);

		Integer idPed = null;
		try {
			idPed = negociacaoService.aceitarNegocicacaoEOrcamentoByIdNegociacao(n.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPed, new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testRecalculoIndiceConversao() {

		Pedido o = null;
		try {
			o = gPedido.gerarOrcamentoComItem();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		Integer idCliente = pedidoService.pesquisarIdClienteByIdPedido(o.getId());

		IndicadorCliente indCli = negociacaoService.pesquisarIndicadorByIdCliente(idCliente);
		assertNotNull("Na inclusao de um orcamento deve ser criado um indicador do cliente", indCli);

		double indVal = indCli.getIndiceConversaoValor();
		double indQtde = indCli.getIndiceConversaoQuantidade();

		assertEquals("Na inclusao de um orcamento sem pedidos cadastrados o indice de valor deve ser 0", (Double) 0d,
				(Double) indVal);
		assertEquals("Na inclusao de um orcamento sem pedidos cadastrados o indice de quantidade deve ser 0",
				(Double) 0d, (Double) indQtde);

		List<Negociacao> lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Deve existir apenas 1 negociacao por orcamento incluido.", (Integer) 1, (Integer) lNeg.size());

		Negociacao n = lNeg.get(0);
		Integer idPedido = null;
		try {
			idPedido = negociacaoService.aceitarNegocicacaoEOrcamentoByIdNegociacao(n.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		indCli = negociacaoService.pesquisarIndicadorByIdCliente(idCliente);
		assertNotNull("No aceite de uma negociacao ja deveria existir no sistema apos a inclusao de um orcamento",
				indCli);

		recarregarEntidade(Pedido.class, idPedido);

		try {
			// Aqui estamos enviando o pedido para que seja refeito o calculo do
			// indice.
			pedidoService.enviarPedido(idPedido, new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		idCliente = pedidoService.pesquisarIdClienteByIdPedido(idPedido);
		indCli = negociacaoService.pesquisarIndicadorByIdCliente(idCliente);

		assertNotNull("No envio do pedido deve ser gerado um indice de conversao", indCli);
		assertEquals("Apos o envio de pedido sem alteracao de preco o indice de conversao deve ser 1", (Double) 1d,
				(Double) indCli.getIndiceConversaoValor());

		int idIdxConv = indCli.getId();
		double idxValorAntigo = indCli.getIndiceConversaoValor();

		try {
			// Aqui estamos reenviando o pedido para que seja atualizado o
			// calculo do
			// indice, mas sem alteracao do valor dos itens, pois isso pode ser
			// exigido pelo cliente.
			pedidoService.enviarPedido(idPedido, new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		indCli = negociacaoService.pesquisarIndicadorByIdCliente(idCliente);
		assertEquals(
				"O cliente ja tem um indice de conversao, entao apos o envio do pedido o indice de conversao deve ser o mesmo",
				(Integer) idIdxConv, indCli.getId());
		assertEquals("Apos o reenvio do pedido sem alteracao de valores os valores do indice devem ser os mesmos",
				(Double) idxValorAntigo, (Double) indCli.getIndiceConversaoValor());

		// Aqui estamos efetuando a alteracao da quanitdade dos itens para que
		// altere o valor do indice de conversao apos o reenvio do pedido.
		List<ItemPedido> lItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		for (ItemPedido i : lItem) {
			i.setQuantidade(i.getQuantidade() * 2);
			try {
				pedidoService.inserirItemPedido(idPedido, i);
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}
		try {
			pedidoService.enviarPedido(idPedido, new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		indCli = negociacaoService.pesquisarIndicadorByIdCliente(idCliente);
		assertEquals("Apos o reenvio do pedido sem alteracao o indice de valor deve ser o mesmo", (Double) 1d,
				(Double) indCli.getIndiceConversaoValor());
		assertEquals("Apos o reenvio do pedido sem alteracao o indice de quantidae deve ser o mesmo", (Double) 1d,
				(Double) indCli.getIndiceConversaoQuantidade());
	}
}
