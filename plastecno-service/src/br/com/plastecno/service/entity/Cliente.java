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

import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_cliente", schema="vendas")
@InformacaoValidavel
public class Cliente implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4628886058991048859L;
	@Id
	@SequenceGenerator(name = "clienteSequence", sequenceName = "vendas.seq_cliente_id", allocationSize=1, initialValue=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clienteSequence")
	private Integer id;
	
	@InformacaoValidavel(obrigatorio=true, intervalo={1, 150}, nomeExibicao="Nome fantasia do cliente")
	@Column(name="nome_fantasia")
	private String nomeFantasia;
	
	@InformacaoValidavel(obrigatorio=true, intervalo={1, 150}, nomeExibicao="Razao social do cliente")
	@Column(name="razao_social")
	private String razaoSocial;
	
	@InformacaoValidavel(tipoDocumento=TipoDocumento.CNPJ, nomeExibicao="CNPJ do cliente")
	private String cnpj;
	
	@InformacaoValidavel(tipoDocumento=TipoDocumento.CPF, nomeExibicao="CPF do cliente")
	private String cpf;
	
	@Column(name="insc_estadual")
	@InformacaoValidavel(intervalo={0, 15}, tipoDocumento=TipoDocumento.INSCRICAO_ESTADUAL, nomeExibicao="Inscrição estadual do Cliente")
	private String inscricaoEstadual;
	
	@Temporal(TemporalType.DATE)
	@Column(name="data_ultimo_contato ")
	private Date dataUltimoContato;
	
	@Column(name="informacoes_adicionais")
	private String informacoesAdicionais;
	
	@InformacaoValidavel(padrao=".+@.+\\..{2,}", nomeExibicao="Email do cliente")
	private String email;
	private String site;
	
	@Column(name="prospeccao_finalizada")
	private Boolean prospeccaoFinalizada;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_ramo_atividade")
	@InformacaoValidavel(relacionamentoObrigatorio=true, nomeExibicao="Ramo de atividade do cliente")
	private RamoAtividade ramoAtividade;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_vendedor", referencedColumnName="id", nullable=false)
	@InformacaoValidavel(relacionamentoObrigatorio=true, nomeExibicao="Vendedor do cliente")
	private Usuario vendedor;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="tb_cliente_tb_transportadora", schema="vendas", 
			joinColumns={@JoinColumn(name = "id_cliente", referencedColumnName = "id")},
			inverseJoinColumns={@JoinColumn(name = "id_transportadora", referencedColumnName = "id")})
	private List<Transportadora> listaRedespacho;
	

	/*
	 * Tivemos que implementar como um Set pois o hibernate tem uma limitacao em fazer multiplos 
	 * fetchs com o tipo list e collection. Isso apareceu na geracao do relatorio de cliente-regiao
	 */
	@OneToMany(mappedBy="cliente", fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@InformacaoValidavel(iteravel=true, nomeExibicao="Lista de contato do cliente")
	private Set<ContatoCliente> listaContato;
	
	@OneToMany(mappedBy="cliente", fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@InformacaoValidavel(iteravel=true, nomeExibicao="Lista de logradouro do cliente")
	private List<LogradouroCliente> listaLogradouro;
	
	@Transient
	private String dataUltimoContatoFormatada;
	
	public Cliente () {}
	
	/*
	 * Construtor para popular o picklist da tela de vendedor
	 */
	public Cliente(Integer id, String nomeFantasia) {
		this.id = id;
		this.nomeFantasia = nomeFantasia;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNomeFantasia() {
		return nomeFantasia;
	}
	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}
	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}
	
	public String getInformacoesAdicionais() {
		return informacoesAdicionais;
	}
	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public RamoAtividade getRamoAtividade() {
		return ramoAtividade;
	}
	public void setRamoAtividade(RamoAtividade ramoAtividade) {
		this.ramoAtividade = ramoAtividade;
	}
	public List<Transportadora> getListaRedespacho() {
		return listaRedespacho;
	}
	public void setListaRedespacho(List<Transportadora> listaRedespacho) {
		this.listaRedespacho = listaRedespacho;
	}
	public Set<ContatoCliente> getListaContato() {
		return listaContato;
	}
	public void setListaContato(Set<ContatoCliente> listaContato) {
		this.listaContato = listaContato;
	}
	public Usuario getVendedor() {
		return vendedor;
	}
	public void setVendedor(Usuario vendedor) {
		this.vendedor = vendedor;
	}
	public Date getDataUltimoContato() {
		return dataUltimoContato;
	}
	public void setDataUltimoContato(Date dataUltimoContato) {
		this.dataUltimoContato = dataUltimoContato;
	}
	
	public List<LogradouroCliente> getListaLogradouro() {
		return listaLogradouro;
	}
	
	public void setListaLogradouro(List<LogradouroCliente> listalogradouro) {
		this.listaLogradouro = listalogradouro;
	}
	
	public void addLogradouro(LogradouroCliente logradouroCliente) {
		if(this.listaLogradouro == null) {
			this.setListaLogradouro(new ArrayList<LogradouroCliente>());
		}
		this.listaLogradouro.add(logradouroCliente);
		logradouroCliente.setCliente(this);
	}
	
	public void addLogradouro(List<LogradouroCliente> listaLogradouro) {
		if (listaLogradouro == null) {
			return;
		}
		
		for (LogradouroCliente logradouroCliente : listaLogradouro) {
			this.addLogradouro(logradouroCliente);		
		}
	}
	
	public void addContato(ContatoCliente contatoCliente) {
		if(this.listaContato == null) {
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
	
	public void addRedespacho(Transportadora transportadora) {
		if(this.listaRedespacho == null) {
			this.listaRedespacho = new ArrayList<Transportadora>();
		}
		this.listaRedespacho.add(transportadora);
	}
	
	public void addRedespacho(List<Transportadora> listaRedespacho) {
		if (listaRedespacho == null) {
			return;
		}
		
		for (Transportadora transportadora : listaRedespacho) {
			this.addRedespacho(transportadora);		
		}
	}
	
	public boolean isJuridico() {
		return this.cnpj != null;
	}
	
	public String getDocumento() {
		return this.isJuridico() ? this.cnpj : this.cpf;
	}
	
	public Contato getContatoPrincipal() {
		return this.isListaContatoPreenchida() ? this.listaContato.iterator().next() : null;
	}

	public String getDataUltimoContatoFormatada() {
		return dataUltimoContatoFormatada;
	}

	public void setDataUltimoContatoFormatada(String dataUltimoContatoFormatada) {
		this.dataUltimoContatoFormatada = dataUltimoContatoFormatada;
	}
	
	
	public void setProspeccaoFinalizada(Boolean prospeccaoFinalizada) {
		this.prospeccaoFinalizada = prospeccaoFinalizada;
	}
	
	public Boolean getProspeccaoFinalizada() {
		return prospeccaoFinalizada;
	}
	
	public boolean isProspectado() {
		return this.prospeccaoFinalizada != null && this.prospeccaoFinalizada.booleanValue();
	}

	public String getNomeCompleto() {
		return this.getNomeFantasia()+" - "+this.getRazaoSocial();
	}
	

	public Logradouro getLogradouro(TipoLogradouro tipoLogradouro) {
		return EntityUtils.getLogradouro(listaLogradouro, tipoLogradouro);
	}
	
	public Logradouro getLogradouroFaturamento () {
		return this.getLogradouro(TipoLogradouro.FATURAMENTO);
	}
	
	public boolean isListaContatoPreenchida() {
		return this.listaContato != null && !this.listaContato.isEmpty();
	}
	
	public void limparListaLogradouro(){
		if (this.listaLogradouro != null){
			this.listaLogradouro.clear();	
		}
		
	}
	
	public boolean isListaLogradouroPreenchida() {
		return this.listaLogradouro != null && !this.listaLogradouro.isEmpty();
	}
}
