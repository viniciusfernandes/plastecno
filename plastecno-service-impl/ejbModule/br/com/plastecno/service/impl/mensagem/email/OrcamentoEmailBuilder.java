package br.com.plastecno.service.impl.mensagem.email;

import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.mensagem.email.AnexoEmail;
import br.com.plastecno.service.mensagem.email.exception.MensagemEmailException;
import br.com.plastecno.util.NumeroUtils;

public class OrcamentoEmailBuilder extends PedidoEmailBuilder {

	public OrcamentoEmailBuilder(Pedido pedido, AnexoEmail pdfPedido, AnexoEmail... anexos)
			throws MensagemEmailException {
		super(pedido, pdfPedido, anexos);
		pdfPedido.setNome("Or�amento No. " + pedido.getId() + " " + pedido.getCliente().getNomeFantasia());
		pdfPedido.setDescricao("Or�amento realizado pela Plastecno");
	}

	@Override
	public String gerarConteudo() {
		return "Prezado " + pedido.getContato().getNome() + ", segue o or�amento para analise. Valor total de R$ "
				+ NumeroUtils.formatarValorMonetario(pedido.getValorPedido());
	}

	@Override
	public String gerarDestinatario() {
		return pedido.getContato().getEmail();
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
		return "Plastecno - Or�amento de Venda No. " + pedido.getId() + " - " + pedido.getCliente().getNomeFantasia();
	}

}
