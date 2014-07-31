package br.com.plastecno.service.impl.mensagem.email;

import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.mensagem.email.exception.MensagemEmailException;

public class VendaClienteEmailBuilder extends VendaEmailBuilder {

	public VendaClienteEmailBuilder(Pedido pedido, byte[] arquivoAnexo) throws MensagemEmailException {
		super(pedido, arquivoAnexo);
	}

	@Override
	public String gerarDestinatario() {
		return pedido.getContato().getEmail();
	}
}
