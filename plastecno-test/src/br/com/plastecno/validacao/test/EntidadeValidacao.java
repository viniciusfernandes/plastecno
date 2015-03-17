package br.com.plastecno.validacao.test;

import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class EntidadeValidacao {
	@InformacaoValidavel(tipoDocumento = TipoDocumento.CNPJ, nomeExibicao = "CNPJ")
	private String cnpj;

	@InformacaoValidavel(tipoDocumento = TipoDocumento.CPF, nomeExibicao = "CPF do cliente")
	private String cpf;

	@InformacaoValidavel(obrigatorio = true, numerico = true, positivo = true, nomeExibicao = "Idade")
	private Integer idade;

	@InformacaoValidavel(intervalo = { 0, 12 }, tipoDocumento = TipoDocumento.INSCRICAO_ESTADUAL, nomeExibicao = "Inscricao estadual")
	private String inscricaoEstadual;

	@InformacaoValidavel(obrigatorio = true, intervalo = { 1, 20 }, nomeExibicao = "Nome fantasia")
	private String nomeFantasia;

	@InformacaoValidavel(obrigatorio = true, numerico = true, estritamentePositivo = true, nomeExibicao = "Quantidade")
	private Integer quantidade;

	@InformacaoValidavel(obrigatorio = false, intervalo = { 1, 10 }, nomeExibicao = "Razão Social")
	private String razaoSocial;

	public String getCnpj() {
		return cnpj;
	}

	public String getCpf() {
		return cpf;
	}

	public Integer getIdade() {
		return idade;
	}

	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

}
