package br.com.plastecno.service.impl.mensagem.email;

import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.mensagem.email.AnexoEmail;
import br.com.plastecno.service.mensagem.email.exception.MensagemEmailException;

public class OrcamentoEmailAlternaticoBuilder extends OrcamentoEmailBuilder {

	public OrcamentoEmailAlternaticoBuilder(Pedido pedido, AnexoEmail pdfPedido, AnexoEmail... anexos)
			throws MensagemEmailException {
		super(pedido, pdfPedido, anexos);
	}

	@Override
	public String gerarDestinatario() {
		return pedido.getVendedor().getEmailCopia();
	}

}
