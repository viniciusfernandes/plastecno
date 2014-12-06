package br.com.plastecno.service.impl.mensagem.email;

import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.mensagem.email.AnexoEmail;
import br.com.plastecno.service.mensagem.email.MensagemEmail;
import br.com.plastecno.service.mensagem.email.exception.MensagemEmailException;

abstract class PedidoEmailBuilder {
	final Pedido pedido;
	final byte[] arquivoAnexo;
	public PedidoEmailBuilder(Pedido pedido, byte[] arquivoAnexo) throws MensagemEmailException {
		
		if (pedido == null || arquivoAnexo == null) {
			throw new MensagemEmailException("O pedido e o arquivo em anexo são obrigatorios");
		}
		
		this.pedido = pedido;
		this.arquivoAnexo = arquivoAnexo;
	}

	public abstract AnexoEmail gerarArquivoAnexo();
	public abstract String gerarConteudo();
	public abstract String gerarDestinatario();
	public final MensagemEmail gerarMensagemEmail() {
		final MensagemEmail email = new MensagemEmail(gerarTitulo(), gerarRemetente(), gerarDestinatario(), gerarConteudo());
		email.addAnexo(gerarArquivoAnexo());
		return email;
	}
	public abstract String gerarRemetente();
	
	public abstract String gerarTitulo();

}
