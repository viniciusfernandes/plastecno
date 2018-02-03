package br.com.svr.service.impl.mensagem.email;

import br.com.svr.service.entity.Pedido;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.mensagem.email.exception.MensagemEmailException;
import br.com.svr.util.StringUtils;

public class VendaEmailBuilder extends PedidoEmailBuilder {

	public VendaEmailBuilder(Pedido pedido, AnexoEmail pdfPedido, AnexoEmail... anexos) throws MensagemEmailException {
		super(pedido, pdfPedido, anexos);
		pdfPedido.setNome("Pedido No. " + pedido.getId() + " " + pedido.getCliente().getNomeFantasia());
		pdfPedido.setDescricao("Pedido de venda realizado com sucesso.");
	}

	@Override
	public String gerarConteudo() {
		return "Segue o pedido de venda para o cliente "
				+ pedido.getCliente().getNomeCompleto()
				+ (StringUtils.isNotEmpty(pedido.getObservacao()) ? "\n\nFavor considerar as seguintes observações:\n"
						+ pedido.getObservacao() : "");
	}

	@Override
	public String gerarDestinatario() {
		return pedido.getRepresentada().getEmail();
	}

	@Override
	public String gerarDestinatarioCc() {
		return pedido.getVendedor().getEmailCopia();
	}

	@Override
	public String gerarRemetente() {
		return pedido.getVendedor().getEmail();
	}

	@Override
	public String gerarTitulo() {
		return "Pedido de Venda No: " + pedido.getId() + " - " + pedido.getCliente().getNomeFantasia();
	}
}
