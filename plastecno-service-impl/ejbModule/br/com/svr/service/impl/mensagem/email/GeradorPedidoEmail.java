package br.com.svr.service.impl.mensagem.email;

import java.util.HashMap;
import java.util.Map;

import br.com.svr.service.entity.Pedido;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.mensagem.email.MensagemEmail;
import br.com.svr.service.mensagem.email.exception.MensagemEmailException;

public final class GeradorPedidoEmail {

	private static final Map<TipoMensagemPedido, Class<? extends PedidoEmailBuilder>> mapaMensagem;
	static {
		mapaMensagem = new HashMap<TipoMensagemPedido, Class<? extends PedidoEmailBuilder>>();
		mapaMensagem.put(TipoMensagemPedido.VENDA, VendaEmailBuilder.class);
		mapaMensagem.put(TipoMensagemPedido.ORCAMENTO, OrcamentoEmailBuilder.class);
		mapaMensagem.put(TipoMensagemPedido.ORCAMENTO_ALTERNATIVO, OrcamentoEmailAlternaticoBuilder.class);
		mapaMensagem.put(TipoMensagemPedido.VENDA_CLIENTE, VendaClienteEmailBuilder.class);
		mapaMensagem.put(TipoMensagemPedido.COMPRA, CompraEmailBuilder.class);
	}

	public static MensagemEmail gerarMensagem(Pedido pedido, TipoMensagemPedido tipoMensagem, AnexoEmail pdfPedido,
			AnexoEmail... anexos) throws MensagemEmailException {
		try {
			return mapaMensagem.get(tipoMensagem).getConstructor(Pedido.class, AnexoEmail.class, AnexoEmail[].class)
					.newInstance(pedido, pdfPedido, anexos).gerarMensagemEmail();
		} catch (Exception e) {
			throw new MensagemEmailException("Falha ao tentar inicializar o construtor da mensagem de email", e);
		}
	}

	private GeradorPedidoEmail() {
	}
}