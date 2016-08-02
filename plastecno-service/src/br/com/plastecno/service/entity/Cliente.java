package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.plastecno.service.constante.TipoCliente;
import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_cliente", schema = "vendas")
@InformacaoValidavel
public class Cliente implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4628886058991048859L;

	@InformacaoValidavel(intervalo = { 1, 15 }, tipoDocumento = TipoDocumento.CNPJ, nomeExibicao = "CNPJ do cliente")
	private String cnpj;

	@InformacaoValidavel(tipoDocumento = TipoDocumento.CPF, nomeExibicao = "CPF do cliente")
	private String cpf;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_ultimo_contato ")
	private Date dataUltimoContato;

	@Transient
	private String dataUltimoContatoFormatada;

	@Column(name = "documento_estrangeiro")
	@InformacaoValidavel(intervalo = { 0, 15 }, nomeExibicao = "Documento Estrangeiro")
	private String documentoEstrangeiro;

	@InformacaoValidavel(padrao = ".+@.+\\..{2,}", nomeExibicao = "Email do cliente")
	private String email;

	@Column(name = "email_cobranca")
	@InformacaoValidavel(padrao = ".+@.+\\..{2,}", nomeExibicao = "Email de cobrança do cliente")
	private String emailCobranca;

	@Id
	@SequenceGenerator(name = "clienteSequence", sequenceName = "vendas.seq_cliente_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clienteSequence")
	private Integer id;

	@Column(name = "informacoes_adicionais")
	private String informacoesAdicionais;

	@Column(name = "insc_estadual")
	@InformacaoValidavel(intervalo = { 0, 15 }, tipoDocumento = TipoDocumento.INSCRICAO_ESTADUAL, nomeExibicao = "Inscrição estadual do Cliente")
	private String inscricaoEstadual;

	/*
	 * Tivemos que implementar como um Set pois o hibernate tem uma limitacao em
	 * fazer multiplos fetchs com o tipo list e collection. Isso apareceu na
	 * geracao do relatorio de cliente-regiao
	 */
	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@InformacaoValidavel(iteravel = true, nomeExibicao = "Lista de contato do cliente")
	private Set<ContatoCliente> listaContato;

	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@InformacaoValidavel(iteravel = true, nomeExibicao = "Lista de logradouro do cliente")
	private List<LogradouroCliente> listaLogradouro;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tb_cliente_tb_transportadora", schema = "vendas", joinColumns = { @JoinColumn(name = "id_cliente", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "id_transportadora", referencedColumnName = "id") })
	private List<Transportadora> listaRedespacho;

	@InformacaoValidavel(obrigatorio = true, intervalo = { 1, 150 }, nomeExibicao = "Nome fantasia do cliente")
	@Column(name = "nome_fantasia")
	private String nomeFantasia;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ramo_atividade")
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Ramo de atividade do cliente")
	private RamoAtividade ramoAtividade;

	@InformacaoValidavel(obrigatorio = true, intervalo = { 1, 150 }, nomeExibicao = "Razao social do cliente")
	@Column(name = "razao_social")
	private String razaoSocial;

	private String site;

	@Enumerated
	@Column(name = "id_tipo_cliente")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo de cliente")
	private TipoCliente tipoCliente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vendedor", referencedColumnName = "id", nullable = false)
	private Usuario vendedor;

	public Cliente() {
	}

	/*
	 * Construtor para popular o picklist da tela de vendedor
	 */
	public Cliente(Integer id, String nomeFantasia) {
		this.id = id;
		this.nomeFantasia = nomeFantasia;
	}

	public Cliente(Integer id, String nomeFantasia, String razaoSozial) {
		this(id, nomeFantasia);
		this.razaoSocial = razaoSozial;
	}

	public Cliente(Integer id, String nomeFantasia, String razaoSozial,
			String cnpj, String cpf, String inscricaoEstadual, String email) {
		this(id, nomeFantasia, razaoSozial);
		this.cnpj = cnpj;
		this.cpf = cpf;
		this.inscricaoEstadual = inscricaoEstadual;
		this.email = email;
	}

	public void addContato(ContatoCliente contatoCliente) {
		if (this.listaContato == null) {
			this.setListaContato(new HashSet<ContatoCliente>());
		}
		this.listaContato.add(contatoCliente);
		contatoCliente.setCliente(this);
	}

	public void addContato(List<ContatoCliente> listaContato) {

		if (listaContato == null) {
			return;
		}

		for (ContatoCliente contatoCliente : listaContato) {
			this.addContato(contatoCliente);
		}
	}

	public void addLogradouro(List<LogradouroCliente> listaLogradouro) {
		if (listaLogradouro == null) {
			return;
		}

		for (LogradouroCliente logradouroCliente : listaLogradouro) {
			this.addLogradouro(logradouroCliente);
		}
	}

	public void addLogradouro(LogradouroCliente logradouroCliente) {
		if (this.listaLogradouro == null) {
			this.setListaLogradouro(new ArrayList<LogradouroCliente>());
		}
		this.listaLogradouro.add(logradouroCliente);
		logradouroCliente.setCliente(this);
	}

	public void addRedespacho(List<Transportadora> listaRedespacho) {
		if (listaRedespacho == null) {
			return;
		}

		for (Transportadora transportadora : listaRedespacho) {
			this.addRedespacho(transportadora);
		}
	}

	public void addRedespacho(Transportadora transportadora) {
		if (this.listaRedespacho == null) {
			this.listaRedespacho = new ArrayList<Transportadora>();
		}
		this.listaRedespacho.add(transportadora);
	}

	public String getCnpj() {
		return cnpj;
	}

	public Contato getContatoPrincipal() {
		return this.isListaContatoPreenchida() ? this.listaContato.iterator()
				.next() : null;
	}

	public String getCpf() {
		return cpf;
	}

	public Date getDataUltimoContato() {
		return dataUltimoContato;
	}

	public String getDataUltimoContatoFormatada() {
		return dataUltimoContatoFormatada;
	}

	public String getDocumento() {
		return this.isJuridico() ? this.cnpj : this.cpf;
	}

	public String getDocumentoEstrangeiro() {
		return documentoEstrangeiro;
	}

	public String getEmail() {
		return email;
	}

	public String getEmailCobranca() {
		return emailCobranca;
	}

	public Integer getId() {
		return id;
	}

	public String getInformacoesAdicionais() {
		return informacoesAdicionais;
	}

	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public Set<ContatoCliente> getListaContato() {
		return listaContato;
	}

	public List<LogradouroCliente> getListaLogradouro() {
		return listaLogradouro;
	}

	public List<Transportadora> getListaRedespacho() {
		return listaRedespacho;
	}

	public Logradouro getLogradouro(TipoLogradouro tipoLogradouro) {
		return EntityUtils.getLogradouro(listaLogradouro, tipoLogradouro);
	}

	public Logradouro getLogradouroFaturamento() {
		return this.getLogradouro(TipoLogradouro.FATURAMENTO);
	}

	public String getNomeCompleto() {
		return this.getNomeFantasia() + " - " + this.getRazaoSocial();
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public RamoAtividade getRamoAtividade() {
		return ramoAtividade;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public String getSite() {
		return site;
	}

	public TipoCliente getTipoCliente() {
		return tipoCliente;
	}

	public Usuario getVendedor() {
		return vendedor;
	}

	public boolean isJuridico() {
		return this.cnpj != null;
	}

	public boolean isListaContatoPreenchida() {
		return this.listaContato != null && !this.listaContato.isEmpty();
	}

	public boolean isListaLogradouroPreenchida() {
		return this.listaLogradouro != null && !this.listaLogradouro.isEmpty();
	}

	public boolean isNovo() {
		return id == null;
	}

	public boolean isRevendedor() {
		return TipoCliente.REVENDEDOR.equals(tipoCliente);
	}

	public void limparListaLogradouro() {
		if (this.listaLogradouro != null) {
			this.listaLogradouro.clear();
		}

	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void setDataUltimoContato(Date dataUltimoContato) {
		this.dataUltimoContato = dataUltimoContato;
	}

	public void setDataUltimoContatoFormatada(String dataUltimoContatoFormatada) {
		this.dataUltimoContatoFormatada = dataUltimoContatoFormatada;
	}

	public void setDocumentoEstrangeiro(String documentoEstrangeiro) {
		this.documentoEstrangeiro = documentoEstrangeiro;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEmailCobranca(String emailCobranca) {
		this.emailCobranca = emailCobranca;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	public void setListaContato(Set<ContatoCliente> listaContato) {
		this.listaContato = listaContato;
	}

	public void setListaLogradouro(List<LogradouroCliente> listalogradouro) {
		this.listaLogradouro = listalogradouro;
	}

	public void setListaRedespacho(List<Transportadora> listaRedespacho) {
		this.listaRedespacho = listaRedespacho;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public void setRamoAtividade(RamoAtividade ramoAtividade) {
		this.ramoAtividade = ramoAtividade;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setTipoCliente(TipoCliente tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

	public void setVendedor(Usuario vendedor) {
		this.vendedor = vendedor;
	}
}
