package br.com.plastecno.service.mensagem.email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MensagemEmail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -861137251424068538L;
	private final String remetente;
	private final String destinatario;
	private final String titulo;
	private final String conteudo;
	private List<AnexoEmail> listaAnexo;
	
	public MensagemEmail(String titutlo, String remetente, String destinatario, String conteudo) {
		this.titulo = titutlo;
		this.remetente = remetente;
		this.destinatario = destinatario;
		this.conteudo = conteudo;
	}
	
	public void addAnexo(AnexoEmail anexoEmail) {
		if (this.listaAnexo == null) {
			this.listaAnexo = new ArrayList<AnexoEmail>();
		}
		this.listaAnexo.add(anexoEmail);
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
