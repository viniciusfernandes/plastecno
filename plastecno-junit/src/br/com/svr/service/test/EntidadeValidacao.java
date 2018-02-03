package br.com.svr.service.test;

import java.util.ArrayList;
import java.util.List;

import br.com.svr.service.constante.TipoDocumento;
import br.com.svr.service.validacao.annotation.InformacaoValidavel;

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

	@InformacaoValidavel(intervaloNumerico = { 1, 18 }, nomeExibicao = "Idade limite")
	private Integer idadeLimite;

	@InformacaoValidavel(intervaloComprimento = { 0, 12 }, tipoDocumento = TipoDocumento.INSCRICAO_ESTADUAL, nomeExibicao = "Inscricao estadual")
	private String inscricaoEstadual;

	@InformacaoValidavel(iteravel = true, nomeExibicao = "Lista de filhos")
	private List<EntidadeValidacaoSimples> listaFilho = new ArrayList<EntidadeValidacaoSimples>();

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 20 }, nomeExibicao = "Nome fantasia")
	private String nomeFantasia;

	@InformacaoValidavel(obrigatorio = true, numerico = true, estritamentePositivo = true, nomeExibicao = "Quantidade")
	private Integer quantidade;

	@InformacaoValidavel(trim = true, obrigatorio = false, intervaloComprimento = { 1, 10 }, nomeExibicao = "Razão Social")
	private String razaoSocial;

	@InformacaoValidavel(obrigatorio = true, tamanho = 4, nomeExibicao = "Senha")
	private String senha;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Entidade sobrinho")
	private EntidadeValidacaoSimples sobrinho;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1.11, 90 }, nomeExibicao = "Tarifa limite")
	private Double tarifaLimite;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Tipo da entidade")
	private EntidadeValidacaoTipo tipoEntidade;

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

	public Integer getIdadeLimite() {
		return idadeLimite;
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

	public Double getTarifaLimite() {
		return tarifaLimite;
	}

	public EntidadeValidacaoTipo getTipoEntidade() {
		return tipoEntidade;
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

	public void setIdadeLimite(Integer idadeLimite) {
		this.idadeLimite = idadeLimite;
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

	public void setTarifaLimite(Double tarifaLimite) {
		this.tarifaLimite = tarifaLimite;
	}

	public void setTipoEntidade(EntidadeValidacaoTipo tipoEntidade) {
		this.tipoEntidade = tipoEntidade;
	}

}
