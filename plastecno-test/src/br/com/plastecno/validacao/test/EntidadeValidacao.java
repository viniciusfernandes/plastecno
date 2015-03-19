package br.com.plastecno.validacao.test;

import java.util.ArrayList;
import java.util.List;

import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class EntidadeValidacao {
	@InformacaoValidavel(tipoDocumento = TipoDocumento.CNPJ, nomeExibicao = "CNPJ")
	private String cnpj;

	@InformacaoValidavel(tipoDocumento = TipoDocumento.CPF, nomeExibicao = "CPF do cliente")
	private String cpf;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\D+@hotmail", nomeExibicao = "Email")
	private String email;

	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Entidade filha")
	private EntidadeValidacaoSimples filha;

	@InformacaoValidavel(relacionamentoObrigatorio = true, cascata = true, nomeExibicao = "Herdado da entidade")
	private EntidadeValidacaoHeranca herdado;

	@InformacaoValidavel(obrigatorio = true, numerico = true, positivo = true, nomeExibicao = "Idade")
	private Integer idade;

	@InformacaoValidavel(intervalo = { 0, 12 }, tipoDocumento = TipoDocumento.INSCRICAO_ESTADUAL, nomeExibicao = "Inscricao estadual")
	private String inscricaoEstadual;

	@InformacaoValidavel(iteravel = true, nomeExibicao = "Lista de filhos")
	private List<EntidadeValidacaoSimples> listaFilho = new ArrayList<EntidadeValidacaoSimples>();

	@InformacaoValidavel(obrigatorio = true, intervalo = { 1, 20 }, nomeExibicao = "Nome fantasia")
	private String nomeFantasia;

	@InformacaoValidavel(obrigatorio = true, numerico = true, estritamentePositivo = true, nomeExibicao = "Quantidade")
	private Integer quantidade;

	@InformacaoValidavel(obrigatorio = false, intervalo = { 1, 10 }, nomeExibicao = "Razão Social")
	private String razaoSocial;

	@InformacaoValidavel(obrigatorio = true, tamanho = 4, nomeExibicao = "Senha")
	private String senha;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Entidade sobrinho")
	private EntidadeValidacaoSimples sobrinho;

	public void addFilho(EntidadeValidacaoSimples filho) {
		listaFilho.add(filho);
	}

	public String getCnpj() {
		return cnpj;
	}

	public String getCpf() {
		return cpf;
	}

	public String getEmail() {
		return email;
	}

	public EntidadeValidacaoSimples getFilha() {
		return filha;
	}

	public EntidadeValidacaoHeranca getHerdado() {
		return herdado;
	}

	public Integer getIdade() {
		return idade;
	}

	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public List<EntidadeValidacaoSimples> getListaFilho() {
		return listaFilho;
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

	public String getSenha() {
		return senha;
	}

	public EntidadeValidacaoSimples getSobrinho() {
		return sobrinho;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFilha(EntidadeValidacaoSimples filha) {
		this.filha = filha;
	}

	public void setHerdado(EntidadeValidacaoHeranca herdado) {
		this.herdado = herdado;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	public void setListaFilho(List<EntidadeValidacaoSimples> listaFilho) {
		this.listaFilho = listaFilho;
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

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setSobrinho(EntidadeValidacaoSimples sobrinho) {
		this.sobrinho = sobrinho;
	}

}
