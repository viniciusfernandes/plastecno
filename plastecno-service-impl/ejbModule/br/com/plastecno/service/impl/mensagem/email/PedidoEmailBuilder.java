package br.com.plastecno.service.impl.mensagem.email;

import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.mensagem.email.AnexoEmail;
import br.com.plastecno.service.mensagem.email.MensagemEmail;
import br.com.plastecno.service.mensagem.email.exception.MensagemEmailException;

abstract class PedidoEmailBuilder {
	final byte[][] anexos;
	private String descricaoArquivo;
	private String nomeArquivo;
	final byte[] pdfPedido;
	final Pedido pedido;

	public PedidoEmailBuilder(Pedido pedido, byte[] pdfPedido, byte[]... anexos) throws MensagemEmailException {

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

	private AnexoEmail[] gerarArquivoAnexo() {
		final AnexoEmail[] anexoEmail = new AnexoEmail[getTotalAnexos()];
		anexoEmail[0] = new AnexoEmail(pdfPedido, "application/pdf", nomeArquivo + ".pdf", descricaoArquivo);
		if (contemAnexos()) {
			for (int i = 0; i < anexoEmail.length; i++) {
				anexoEmail[i + 1] = new AnexoEmail(pdfPedido, null, "teste" + i + ".pdf", null);
			}
		}

		return anexoEmail;
	}

	public abstract String gerarConteudo();

	public abstract String gerarDestinatario();

	public final MensagemEmail gerarMensagemEmail() {
		final MensagemEmail email = new MensagemEmail(gerarTitulo(), gerarRemetente(), gerarDestinatario(),
				gerarConteudo());
		email.addAnexo(gerarArquivoAnexo());
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

	public int getTotalAnexos() {
		// Retornamos 1 pois isso indica que existe ao menos o anexo do pedido.
		return contemAnexos() ? anexos.length + 1 : 1;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

}
