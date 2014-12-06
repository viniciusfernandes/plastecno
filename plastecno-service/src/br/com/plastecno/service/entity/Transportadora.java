package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.constante.TipoDocumento;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_transportadora", schema="vendas")
@InformacaoValidavel
public class Transportadora implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3798079109436664973L;
	@Id
	@SequenceGenerator(name = "transportadoraSequence", sequenceName = "vendas.seq_transportadora_id", allocationSize=1, initialValue=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transportadoraSequence")
	private Integer id;
	
	@InformacaoValidavel(obrigatorio=true, intervalo={1, 150}, nomeExibicao="Nome fantasia da transportadora")
	@Column(name="nome_fantasia")
	private String nomeFantasia;
	
	@InformacaoValidavel(obrigatorio=true, intervalo={1, 150}, nomeExibicao="Razao social da transportadora")
	@Column(name="razao_social")
	private String razaoSocial;
	
	@InformacaoValidavel(tipoDocumento=TipoDocumento.CNPJ, nomeExibicao="CNPJ da transportadora")
	private String cnpj;
	
	@Column(name="insc_estadual")
	@InformacaoValidavel(intervalo={0, 12}, tipoDocumento=TipoDocumento.INSCRICAO_ESTADUAL, nomeExibicao="Inscricao estadual da transportadora")
	private String inscricaoEstadual;
	
	@Column(name="area_atuacao")
	@InformacaoValidavel(intervalo={0, 250}, nomeExibicao="Area de atuacao da transportadora")
	private String areaAtuacao;
	
	@InformacaoValidavel(intervalo={0, 250})
	private String site;
	
	@InformacaoValidavel(cascata=true, nomeExibicao="Logradouro da transportadora")
	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="id_logradouro")
	private Logradouro logradouro;

	private boolean ativo = true;
	
	@OneToMany(mappedBy="transportadora", fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@InformacaoValidavel(iteravel=true, nomeExibicao="Lista de contato da transportadora")
	private List<ContatoTransportadora> listaContato;
	
	public Transportadora () {}
	
	public Transportadora(Integer id, String nomeFantasia) {
		this.id = id;
		this.nomeFantasia = nomeFantasia;
	}

	public void addContato(ContatoTransportadora contato) {
		if (this.listaContato == null) {
			this.listaContato = new ArrayList<ContatoTransportadora>();
		}
		this.listaContato.add(contato);		
		contato.setTransportadora(this);
	}
	public void addContato(List<ContatoTransportadora> listaContato) {
		for (ContatoTransportadora contato : listaContato) {
			this.addContato(contato);	
		}
	}
	@Override
	public boolean equals (Object o) {
		return o instanceof Transportadora && this.id != null && this.id.equals(((Transportadora)o).id);
	}
	public String getAreaAtuacao() {
		return areaAtuacao;
	}
	public String getCnpj() {
		return cnpj;
	}
	public Contato getContatoPrincipal() {
		return this.listaContato != null && !this.listaContato.isEmpty() ? listaContato.get(0) : null;
	}
	public Integer getId() {
		return id;
	}
	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}
	public List<ContatoTransportadora> getListaContato() {
		return listaContato;
	}
	public Logradouro getLogradouro() {
		return logradouro;
	}
	
	public String getNomeFantasia() {
		return nomeFantasia;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public String getSite() {
		return site;
	}
	@Override
	public int hashCode () {
		return this.id != null ? this.id.hashCode() : -1;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public List<ContatoTransportadora> limparContato() {
		final List<ContatoTransportadora> lista = new ArrayList<ContatoTransportadora>(this.listaContato);
		this.listaContato.clear();
		return lista;
	}
	public void setAreaAtuacao(String areaAtuacao) {
		this.areaAtuacao = areaAtuacao;
	}
	
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}
	
	public void setListaContato(List<ContatoTransportadora> listaContato) {
		this.listaContato = listaContato;
	}
	
	public void setLogradouro(Logradouro logradouro) {
		this.logradouro = logradouro;
	}
	
	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}
	
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public void setSite(String site) {
		this.site = site;
	}
}
