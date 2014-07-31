package br.com.plastecno.service.impl.mensagem.email;

import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.mensagem.email.AnexoEmail;
import br.com.plastecno.service.mensagem.email.exception.MensagemEmailException;
import br.com.plastecno.util.StringUtils;

public class VendaEmailBuilder extends PedidoEmailBuilder {

	public VendaEmailBuilder(Pedido pedido, byte[] arquivoAnexo) throws MensagemEmailException {
		super(pedido, arquivoAnexo);
	}

	@Override
	public String gerarTitulo() {
		return "Plastecno - Pedido de Venda No: "+pedido.getId()+" - "+pedido.getCliente().getNomeFantasia();
	}

	@Override
	public String gerarRemetente() {
		return pedido.getVendedor().getEmail();
	}

	@Override
	public String gerarDestinatario() {
		return pedido.getRepresentada().getEmail();
	}

	@Override
	public String gerarConteudo() {
		return "Segue o pedido de venda para o cliente "+pedido.getCliente().getNomeCompleto()
				+ (StringUtils.isNotEmpty(pedido.getObservacao()) 
						? "\n\nFavor considerar as seguintes observações:\n"+pedido.getObservacao() 
							: "");
	}

	@Override
	public AnexoEmail gerarArquivoAnexo() {
		return new AnexoEmail(arquivoAnexo, "application/pdf", 
				"Pedido No. "+pedido.getId()+" "+pedido.getCliente().getNomeFantasia()+".pdf", "Pedido de venda realizado pela Plastecno");
	}
}
