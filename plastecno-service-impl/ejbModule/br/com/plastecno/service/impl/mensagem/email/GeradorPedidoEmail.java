package br.com.plastecno.service.impl.mensagem.email;

import java.util.HashMap;
import java.util.Map;

import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.mensagem.email.MensagemEmail;
import br.com.plastecno.service.mensagem.email.exception.MensagemEmailException;

public final class GeradorPedidoEmail {

	private final Pedido pedido;
	private final byte[] arquivoAnexado;
	private final Map<TipoMensagemPedido, Class<? extends PedidoEmailBuilder>> mapaMensagem; 
	
	public GeradorPedidoEmail (Pedido pedido, byte[] arquivoAnexado) {
		this.pedido = pedido;
		this.arquivoAnexado = arquivoAnexado;
		this.mapaMensagem = new HashMap<TipoMensagemPedido, Class<? extends PedidoEmailBuilder>>();
		
		this.mapaMensagem.put(TipoMensagemPedido.VENDA, VendaEmailBuilder.class);
		this.mapaMensagem.put(TipoMensagemPedido.ORCAMENTO, OrcamentoEmailBuilder.class);
		this.mapaMensagem.put(TipoMensagemPedido.VENDA_CLIENTE, VendaClienteEmailBuilder.class);
	}
	
	public MensagemEmail gerarMensagem(TipoMensagemPedido tipoMensagem) throws MensagemEmailException {
		try {
			return this.mapaMensagem.get(tipoMensagem).getConstructor(Pedido.class, byte[].class)
					.newInstance(this.pedido, this.arquivoAnexado).gerarMensagemEmail();
		} catch (Exception e) {
			throw new MensagemEmailException("Falha ao tentar inicializar o construtor da mensagem de email", e);
		}
	}
}