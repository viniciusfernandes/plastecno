package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_contato", schema="vendas")
@Inheritance(strategy=InheritanceType.JOINED)
@InformacaoValidavel
public class Contato implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1884771619205961167L;
	@Id
	@SequenceGenerator(name="contatoSequence", sequenceName="vendas.seq_contato_id", initialValue=1, allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="contatoSequence")
	private Integer id;
	
	@InformacaoValidavel(obrigatorio=true, intervalo={1, 20}, nomeExibicao="Nome do contato")
	private String nome;
	
	@InformacaoValidavel(intervalo={0, 100})
	private String sobrenome;
	
	@InformacaoValidavel(tipoDocumento=TipoDocumento.CPF, nomeExibicao="CPF")
	private String cpf;
	
	@InformacaoValidavel(intervalo={0, 200}, nomeExibicao="Email do contato")
	private String email;
	
	@InformacaoValidavel(intervalo={0, 3}, nomeExibicao="DDI do contato")
	@Column(name="ddi_1")
	private String ddi;
	
	@InformacaoValidavel(intervalo={0, 3}, nomeExibicao="DDD do contato")
	@Column(name="ddd_1")
	private String ddd;
	
	@InformacaoValidavel(intervalo={0, 10}, nomeExibicao="Telefone do contato")
	@Column(name="telefone_1")
	private String telefone;
	
	@InformacaoValidavel(nomeExibicao="Ramal do contato")
	@Column(name="ramal_1")
	private String ramal;
	
	@InformacaoValidavel(intervalo={0, 9}, nomeExibicao="FAX do contato")
	@Column(name="fax_1")
	private String fax;
	
	@InformacaoValidavel(intervalo={0, 3}, nomeExibicao="DDI secundario do contato")
	@Column(name="ddi_2")
	private String ddiSecundario;
	
	@InformacaoValidavel(intervalo={0, 3}, nomeExibicao="DDD secundario do contato")
	@Column(name="ddd_2")
	private String dddSecundario;
	
	@InformacaoValidavel(intervalo={0, 10}, nomeExibicao="Telefone secundario do contato")
	@Column(name="telefone_2")
	private String telefoneSecundario;
	
	@InformacaoValidavel(nomeExibicao="Ramal secundario do contato")
	@Column(name="ramal_2")
	private String ramalSecundario;
	
	@InformacaoValidavel(intervalo={0, 9}, nomeExibicao="FAX secundario do contato")
	@Column(name="fax_2")
	private String faxSecundario;
	
	@InformacaoValidavel(intervalo={0, 50}, nomeExibicao="Departamento do contato")
	@Column(name="departamento")
	private String departamento;
	
	@InformacaoValidavel(cascata=true, nomeExibicao="Logradouro do contato")
	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="id_logradouro")
	private Logradouro logradouro;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDdi() {
		return ddi;
	}

	public void setDdi(String ddi) {
		this.ddi = ddi;
	}

	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getTelefone() {
		return telefone;
	}
	
	public String getDDDTelefone() {
		StringBuilder telefoneFormatado = new StringBuilder();
		
		telefoneFormatado.append("(").append(ddi == null ? "" : ddi);
		telefoneFormatado.append(" / ").append(ddd == null ? "" : ddd).append(") ");
		telefoneFormatado.append(telefone == null ? "" : formatarTelefoneComHifen(telefone));
		
		return telefoneFormatado.toString();
	}
	
	public String getDDDTelefoneSecundario() {
		StringBuilder telefoneFormatado = new StringBuilder();
		
		telefoneFormatado.append("(").append(ddiSecundario == null ? "" : ddiSecundario);
		telefoneFormatado.append(" / ").append(dddSecundario == null ? "" : dddSecundario).append(") ");
		telefoneFormatado.append(telefoneSecundario == null ? "" : formatarTelefoneComHifen(telefoneSecundario));
		
		return telefoneFormatado.toString();
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getDdiSecundario() {
		return ddiSecundario;
	}

	public void setDdiSecundario(String ddiSecundario) {
		this.ddiSecundario = ddiSecundario;
	}

	public String getDddSecundario() {
		return dddSecundario;
	}

	public void setDddSecundario(String dddSecundario) {
		this.dddSecundario = dddSecundario;
	}

	public String getTelefoneSecundario() {
		return telefoneSecundario;
	}

	public void setTelefoneSecundario(String telefoneSecundario) {
		this.telefoneSecundario = telefoneSecundario;
	}

	public String getRamalSecundario() {
		return ramalSecundario;
	}

	public void setRamalSecundario(String ramalSecundario) {
		this.ramalSecundario = ramalSecundario;
	}

	public String getFaxSecundario() {
		return faxSecundario;
	}

	public void setFaxSecundario(String faxSecundario) {
		this.faxSecundario = faxSecundario;
	}

	public Logradouro getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(Logradouro logradouro) {
		this.logradouro = logradouro;
	}

	public boolean isTelefoneVazio() {
		return this.telefone == null; 
	}
	
	public boolean isTelefoneSecundarioVazio() {
		return this.telefoneSecundario == null; 
	}

	public String getTelefoneFormatado () {
		if (this.isTelefoneVazio()) {
			return "";
		}
		return this.formatarTelefone(this.ddi, this.ddd, this.telefone, this.ramal, this.fax);
	}
	
	public String getTelefoneSecundarioFormatado () {
		if (this.isTelefoneSecundarioVazio()) {
			return "";
		}
		return this.formatarTelefone(this.ddiSecundario, this.dddSecundario, this.telefoneSecundario, this.ramalSecundario, this.faxSecundario);
	}
	
	public String getDDDTelefoneFormatado() {
		if (this.telefone != null) {
			StringBuilder dddTelefone = new StringBuilder();
			dddTelefone.append(this.ddi).append("-").append(this.ddd)
			.append("-").append(this.telefone);
			return dddTelefone.toString();
		}
		return "";
	}
	
	public String getDDDTelefoneSecundarioFormatado() {
		if (this.telefoneSecundario != null) {
			StringBuilder dddTelefone = new StringBuilder();
			dddTelefone.append(this.ddiSecundario).append("-").append(this.dddSecundario)
			.append("-").append(this.telefoneSecundario);
			return dddTelefone.toString();
		}
		return "";
	}
	
	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	@Override
	public boolean equals (Object o) {
		return o instanceof Contato && this.id != null && this.id.equals(((Contato)o).id);
	}
	@Override
	public int hashCode () {
		return this.id != null ? this.id.hashCode() : super.hashCode();
	}
	
	public String getTelefoneComHifen(){
		return formatarTelefoneComHifen(telefone);
	}

	public String getTelefoneSecundarioComHifen(){
		return formatarTelefoneComHifen(telefoneSecundario);
	}
	
	public String getFaxComHifen(){
		return formatarTelefoneComHifen(fax);
	}
	
	public String getFaxSecundarioComHifen(){
		return formatarTelefoneComHifen(faxSecundario);
	}
	
	
	private String formatarTelefone (String ddi, String ddd, String telefone, String ramal, String fax) {
		
		StringBuilder telefoneFormatado = new StringBuilder();
		telefoneFormatado.append("(")
		.append(ddi == null ? "" : ddi).append(" / ")
		.append(ddd == null ? "" : ddd).append(") ")
		.append(telefone == null ? "" : formatarTelefoneComHifen(telefone)).append(" / ")
		.append(ramal == null ? "" : ramal).append(" / ").append(fax == null ? "" : formatarTelefoneComHifen(fax));
		
		return telefoneFormatado.toString();
	}
	
	private String formatarTelefoneComHifen(String telefone){
		if (telefone == null || telefone.length() < 5 || telefone.contains("-")) {
			return telefone;
		}
		
		StringBuilder formatado = new StringBuilder();
		int index = telefone.length() - 4;
		
		formatado.append(telefone.substring(0, index));
		formatado.append("-");
		formatado.append(telefone.substring(index, telefone.length()));
		
		return formatado.toString();
	}
}
