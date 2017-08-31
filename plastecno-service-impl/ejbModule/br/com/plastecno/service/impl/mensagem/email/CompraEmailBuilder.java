package br.com.plastecno.service.impl.mensagem.email;

import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.mensagem.email.exception.MensagemEmailException;
import br.com.plastecno.util.StringUtils;

public class CompraEmailBuilder extends PedidoEmailBuilder {

	public CompraEmailBuilder(Pedido pedido, byte[] arquivoPedido, byte[]... arquivoAnexo)
			throws MensagemEmailException {
		super(pedido, arquivoPedido, arquivoAnexo);
		setNomeArquivo("Pedido No. " + pedido.getId() + " " + pedido.getCliente().getNomeFantasia());
		setDescricaoArquivo("Pedido de comprado por " + pedido.getComprador().getNome());
	}

	@Override
	public String gerarConteudo() {
		return "Segue o pedido de compra efetuado por "
				+ pedido.getComprador().getNome()
				+ (StringUtils.isNotEmpty(pedido.getObservacao()) ? "\n\nFavor considerar as seguintes observações:\n"
						+ pedido.getObservacao() : "");
	}

	@Override
	public String gerarDestinatario() {
		// Aqui estamos enviando o email para a representada (fornecedor) e para
		// os emails cadastrados no cliente (para acompanhamento das compras)
		return pedido.getRepresentada().getEmail() + ";" + pedido.getCliente().getEmail();
	}

	@Override
	public String gerarRemetente() {
		return pedido.getComprador().getEmail();
	}

	@Override
	public String gerarTitulo() {
		return "Plastecno - Pedido de Compra No: " + pedido.getId() + " - " + pedido.getComprador().getNome();
	}
}
