package br.com.svr.service.impl.mensagem.email;

import static br.com.svr.service.impl.mensagem.email.TipoMensagemPedido.MENSAGEM_COMPRA;
import static br.com.svr.service.impl.mensagem.email.TipoMensagemPedido.MENSAGEM_ORCAMENTO;
import static br.com.svr.service.impl.mensagem.email.TipoMensagemPedido.MENSAGEM_ORCAMENTO_ALTERNATIVO;
import static br.com.svr.service.impl.mensagem.email.TipoMensagemPedido.MENSAGEM_VENDA;
import static br.com.svr.service.impl.mensagem.email.TipoMensagemPedido.MENSAGEM_VENDA_CLIENTE;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.mensagem.email.MensagemEmail;
import br.com.svr.service.mensagem.email.exception.MensagemEmailException;

public final class GeradorPedidoEmail {

	public static MensagemEmail gerarMensagem(Pedido pedido, TipoMensagemPedido tipoMensagem, AnexoEmail pdfPedido,
			AnexoEmail... anexos) throws MensagemEmailException {

		PedidoEmailBuilder emailBuilder = null;
		if (MENSAGEM_VENDA.equals(tipoMensagem)) {
			emailBuilder = new VendaEmailBuilder(pedido, pdfPedido, anexos);
		} else if (MENSAGEM_ORCAMENTO.equals(tipoMensagem)) {
			emailBuilder = new OrcamentoEmailBuilder(pedido, pdfPedido, anexos);
		} else if (MENSAGEM_ORCAMENTO_ALTERNATIVO.equals(tipoMensagem)) {
			emailBuilder = new OrcamentoEmailAlternaticoBuilder(pedido, pdfPedido, anexos);
		} else if (MENSAGEM_VENDA_CLIENTE.equals(tipoMensagem)) {
			emailBuilder = new VendaClienteEmailBuilder(pedido, pdfPedido, anexos);
		} else if (MENSAGEM_COMPRA.equals(tipoMensagem)) {
			emailBuilder = new CompraEmailBuilder(pedido, pdfPedido, anexos);
		}
		if (emailBuilder == null) {
			throw new MensagemEmailException(
					"O tipo de mensagem de email nao foi configurado no sistema ou esta em branco.");
		}
		return emailBuilder.gerarMensagemEmail();
	}

	private GeradorPedidoEmail() {
	}
}