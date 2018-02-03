package br.com.svr.service.impl.mensagem.email;

import br.com.svr.service.entity.Pedido;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.mensagem.email.exception.MensagemEmailException;

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
