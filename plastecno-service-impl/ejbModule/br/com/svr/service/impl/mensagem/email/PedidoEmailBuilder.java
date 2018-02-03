package br.com.svr.service.impl.mensagem.email;

import br.com.svr.service.entity.Pedido;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.mensagem.email.MensagemEmail;
import br.com.svr.service.mensagem.email.exception.MensagemEmailException;

abstract class PedidoEmailBuilder {
	final AnexoEmail[] anexos;
	private String descricaoArquivo;
	private String nomeArquivo;
	final AnexoEmail pdfPedido;
	final Pedido pedido;

	public PedidoEmailBuilder(Pedido pedido, AnexoEmail pdfPedido, AnexoEmail... anexos) throws MensagemEmailException {

		if (pedido == null || pdfPedido == null) {
			throw new MensagemEmailException("O pedido e o arquivo em anexo são obrigatorios");
		}

		this.pedido = pedido;
		this.pdfPedido = pdfPedido;
		this.anexos = anexos;
	}

	public boolean contemAnexos() {
		return anexos != null && anexos.length > 0;
	}

	public abstract String gerarConteudo();

	public abstract String gerarDestinatario();

	public abstract String gerarDestinatarioCc();

	public final MensagemEmail gerarMensagemEmail() {
		final MensagemEmail email = new MensagemEmail(gerarTitulo(), gerarRemetente(), gerarDestinatario(),
				gerarDestinatarioCc(), gerarConteudo());
		// Confuigurando o tipo de arquivo a ser enviado e sua extensao, o que
		// eh comum a todos os pedidos.
		pdfPedido.setNome(pdfPedido.getNome() + ".pdf");
		pdfPedido.setTipoAnexo("application/pdf");
		email.addAnexo(pdfPedido);
		email.addAnexo(anexos);
		return email;
	}

	public abstract String gerarRemetente();

	public abstract String gerarTitulo();

	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

}
