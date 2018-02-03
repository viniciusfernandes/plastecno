package br.com.svr.service.mensagem.email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MensagemEmail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -861137251424068538L;
	private final String conteudo;
	private final String destinatario;

	private final String destinatarioCc;
	private List<AnexoEmail> listaAnexo;
	private final String remetente;
	private final String titulo;

	public MensagemEmail(String titutlo, String remetente, String destinatario, String destinatarioCc, String conteudo) {
		this.titulo = titutlo;
		this.remetente = remetente;
		this.destinatario = destinatario;
		this.destinatarioCc = destinatarioCc;
		this.conteudo = conteudo;
	}

	public void addAnexo(AnexoEmail... anexoEmail) {
		if (anexoEmail == null || anexoEmail.length <= 0) {
			return;
		}
		if (listaAnexo == null) {
			listaAnexo = new ArrayList<AnexoEmail>();
		}
		for (AnexoEmail a : anexoEmail) {
			if (a == null) {
				continue;
			}
			listaAnexo.add(a);
		}
	}

	public boolean contemAnexo() {
		return this.listaAnexo != null && !this.listaAnexo.isEmpty();
	}

	public String getConteudo() {
		return conteudo;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public String getDestinatarioCc() {
		return destinatarioCc;
	}

	public List<AnexoEmail> getListaAnexo() {
		return listaAnexo;
	}

	public String getRemetente() {
		return remetente;
	}

	public String getTitulo() {
		return titulo;
	}
}
