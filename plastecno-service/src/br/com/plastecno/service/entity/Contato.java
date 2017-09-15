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
import br.com.plastecno.util.StringUtils;

@Entity
@Table(name = "tb_contato", schema = "vendas")
@Inheritance(strategy = InheritanceType.JOINED)
@InformacaoValidavel
public class Contato implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2455547533115716220L;

	@InformacaoValidavel(tipoDocumento = TipoDocumento.CPF, nomeExibicao = "CPF")
	private String cpf;

	@InformacaoValidavel(intervaloComprimento = { 0, 3 }, nomeExibicao = "DDD do contato")
	@Column(name = "ddd_1")
	private String ddd;

	@InformacaoValidavel(intervaloComprimento = { 0, 3 }, nomeExibicao = "DDD secundario do contato")
	@Column(name = "ddd_2")
	private String dddSecundario;

	@InformacaoValidavel(intervaloComprimento = { 0, 3 }, nomeExibicao = "DDI do contato")
	@Column(name = "ddi_1")
	private String ddi;

	@InformacaoValidavel(intervaloComprimento = { 0, 3 }, nomeExibicao = "DDI secundario do contato")
	@Column(name = "ddi_2")
	private String ddiSecundario;

	@InformacaoValidavel(intervaloComprimento = { 0, 50 }, nomeExibicao = "Departamento do contato")
	@Column(name = "departamento")
	private String departamento;

	@InformacaoValidavel(intervaloComprimento = { 0, 200 }, nomeExibicao = "Email do contato")
	private String email;

	@InformacaoValidavel(intervaloComprimento = { 0, 9 }, nomeExibicao = "FAX do contato")
	@Column(name = "fax_1")
	private String fax;

	@InformacaoValidavel(intervaloComprimento = { 0, 9 }, nomeExibicao = "FAX secundario do contato")
	@Column(name = "fax_2")
	private String faxSecundario;

	@Id
	@SequenceGenerator(name = "contatoSequence", sequenceName = "vendas.seq_contato_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contatoSequence")
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_logradouro_contato")
	@InformacaoValidavel(cascata = true, nomeExibicao = "Logradouro da Contato")
	private LogradouroContato logradouro;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 80 }, nomeExibicao = "Nome do contato")
	private String nome;

	@InformacaoValidavel(nomeExibicao = "Ramal do contato")
	@Column(name = "ramal_1")
	private String ramal;

	@InformacaoValidavel(nomeExibicao = "Ramal secundario do contato")
	@Column(name = "ramal_2")
	private String ramalSecundario;

	@InformacaoValidavel(intervaloComprimento = { 0, 100 })
	private String sobrenome;

	@InformacaoValidavel(intervaloComprimento = { 0, 10 }, nomeExibicao = "Telefone do contato")
	@Column(name = "telefone_1")
	private String telefone;

	@InformacaoValidavel(intervaloComprimento = { 0, 10 }, nomeExibicao = "Telefone secundario do contato")
	@Column(name = "telefone_2")
	private String telefoneSecundario;

	public Contato() {
	}

	public Contato(Contato contato) {
		if (contato == null) {
			return;
		}
		setNome(contato.getNome());
		setDepartamento(contato.getDepartamento());
		setEmail(contato.getEmail());

		setDdi(contato.getDdi());
		setDdd(contato.getDdd());
		setTelefone(contato.getTelefone());
		setFax(contato.getFax());
		setRamal(contato.getRamal());

		setDdiSecundario(contato.getDdiSecundario());
		setDddSecundario(contato.getDddSecundario());
		setTelefoneSecundario(contato.getTelefoneSecundario());
		setFaxSecundario(contato.getFaxSecundario());
		setRamalSecundario(contato.getRamalSecundario());
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Contato && this.id != null && this.id.equals(((Contato) o).id);
	}

	public String formatar() {
		String s = getNome();
		if (StringUtils.isNotEmpty(getEmail())) {
			s += " - " + getEmail();
		}

		if (StringUtils.isNotEmpty(getTelefone())) {
			s += " - " + getTelefoneFormatado();
		}
		return s;
	}

	private String formatarTelefone(String ddi, String ddd, String telefone, String ramal, String fax) {

		StringBuilder telefoneFormatado = new StringBuilder();

		if (ddi != null && !ddi.isEmpty()) {
			telefoneFormatado.append(ddi).append(" ");
		} else {telefoneFormatado.append(" / ");}

		if (ddd != null && !ddd.isEmpty()) {
			telefoneFormatado.append("(").append(ddd).append(") ");
		}

		// telefoneFormatado.append("(").append(ddi == null ? "" :
		// ddi).append(" / ").append(ddd == null ? "" : ddd).append(") ")
		telefoneFormatado.append(telefone == null ? "" : formatarTelefoneComHifen(telefone)).append(" / ")
				.append(ramal == null ? "" : ramal).append(" / ")
				.append(fax == null ? "" : formatarTelefoneComHifen(fax));

		return telefoneFormatado.toString();
	}

	private String formatarTelefoneComHifen(String telefone) {
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

	public String getCpf() {
		return cpf;
	}

	public String getDdd() {
		return ddd;
	}

	public String getDddSecundario() {
		return dddSecundario;
	}

	public String getDDDTelefone() {
		StringBuilder telefoneFormatado = new StringBuilder();

		telefoneFormatado.append("(").append(ddi == null ? "" : ddi);
		telefoneFormatado.append(" / ").append(ddd == null ? "" : ddd).append(") ");
		telefoneFormatado.append(telefone == null ? "" : formatarTelefoneComHifen(telefone));

		return telefoneFormatado.toString();
	}

	public String getDDDTelefoneFormatado() {
		if (this.telefone != null) {
			StringBuilder dddTelefone = new StringBuilder();
			dddTelefone.append(this.ddi).append("-").append(this.ddd).append("-").append(this.telefone);
			return dddTelefone.toString();
		}
		return "";
	}

	public String getDDDTelefoneSecundario() {
		StringBuilder telefoneFormatado = new StringBuilder();

		telefoneFormatado.append("(").append(ddiSecundario == null ? "" : ddiSecundario);
		telefoneFormatado.append(" / ").append(dddSecundario == null ? "" : dddSecundario).append(") ");
		telefoneFormatado.append(telefoneSecundario == null ? "" : formatarTelefoneComHifen(telefoneSecundario));

		return telefoneFormatado.toString();
	}

	public String getDDDTelefoneSecundarioFormatado() {
		if (this.telefoneSecundario != null) {
			StringBuilder dddTelefone = new StringBuilder();
			dddTelefone.append(this.ddiSecundario).append("-").append(this.dddSecundario).append("-")
					.append(this.telefoneSecundario);
			return dddTelefone.toString();
		}
		return "";
	}

	public String getDdi() {
		return ddi;
	}

	public String getDdiSecundario() {
		return ddiSecundario;
	}

	public String getDepartamento() {
		return departamento;
	}

	public String getEmail() {
		return email;
	}

	public String getFax() {
		return fax;
	}

	public String getFaxComHifen() {
		return formatarTelefoneComHifen(fax);
	}

	public String getFaxSecundario() {
		return faxSecundario;
	}

	public String getFaxSecundarioComHifen() {
		return formatarTelefoneComHifen(faxSecundario);
	}

	public Integer getId() {
		return id;
	}

	public LogradouroContato getLogradouro() {
		return logradouro;
	}

	public String getNome() {
		return nome;
	}

	public String getRamal() {
		return ramal;
	}

	public String getRamalSecundario() {
		return ramalSecundario;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public String getTelefone() {
		return telefone;
	}

	public String getTelefoneComHifen() {
		return formatarTelefoneComHifen(telefone);
	}

	public String getTelefoneFormatado() {
		if (this.isTelefoneVazio()) {
			return "";
		}
		return this.formatarTelefone(this.ddi, this.ddd, this.telefone, this.ramal, this.fax);
	}

	public String getTelefoneSecundario() {
		return telefoneSecundario;
	}

	public String getTelefoneSecundarioComHifen() {
		return formatarTelefoneComHifen(telefoneSecundario);
	}

	public String getTelefoneSecundarioFormatado() {
		if (this.isTelefoneSecundarioVazio()) {
			return "";
		}
		return this.formatarTelefone(this.ddiSecundario, this.dddSecundario, this.telefoneSecundario,
				this.ramalSecundario, this.faxSecundario);
	}

	@Override
	public int hashCode() {
		return this.id != null ? this.id.hashCode() : super.hashCode();
	}

	public boolean isTelefoneSecundarioVazio() {
		return this.telefoneSecundario == null;
	}

	public boolean isTelefoneVazio() {
		return this.telefone == null;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public void setDddSecundario(String dddSecundario) {
		this.dddSecundario = dddSecundario;
	}

	public void setDdi(String ddi) {
		this.ddi = ddi;
	}

	public void setDdiSecundario(String ddiSecundario) {
		this.ddiSecundario = ddiSecundario;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setFaxSecundario(String faxSecundario) {
		this.faxSecundario = faxSecundario;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLogradouro(LogradouroContato logradouro) {
		this.logradouro = logradouro;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}

	public void setRamalSecundario(String ramalSecundario) {
		this.ramalSecundario = ramalSecundario;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public void setTelefoneSecundario(String telefoneSecundario) {
		this.telefoneSecundario = telefoneSecundario;
	}
}
