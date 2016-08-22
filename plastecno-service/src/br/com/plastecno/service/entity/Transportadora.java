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
@Table(name = "tb_transportadora", schema = "vendas")
@InformacaoValidavel
public class Transportadora implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3798079109436664973L;
	@Column(name = "area_atuacao")
	@InformacaoValidavel(intervaloComprimento = { 0, 250 }, nomeExibicao = "Area de atuacao da transportadora")
	private String areaAtuacao;

	private boolean ativo = true;

	@InformacaoValidavel(tipoDocumento = TipoDocumento.CNPJ, nomeExibicao = "CNPJ da transportadora")
	private String cnpj;

	@Id
	@SequenceGenerator(name = "transportadoraSequence", sequenceName = "vendas.seq_transportadora_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transportadoraSequence")
	private Integer id;

	@Column(name = "insc_estadual")
	@InformacaoValidavel(intervaloComprimento = { 0, 12 }, tipoDocumento = TipoDocumento.INSCRICAO_ESTADUAL, nomeExibicao = "Inscricao estadual da transportadora")
	private String inscricaoEstadual;

	@OneToMany(mappedBy = "transportadora", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@InformacaoValidavel(iteravel = true, nomeExibicao = "Lista de contato da transportadora")
	private List<ContatoTransportadora> listaContato;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Logradouro da transportadora")
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_logradouro")
	private Logradouro logradouro;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 150 }, nomeExibicao = "Nome fantasia da transportadora")
	@Column(name = "nome_fantasia")
	private String nomeFantasia;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 150 }, nomeExibicao = "Razao social da transportadora")
	@Column(name = "razao_social")
	private String razaoSocial;

	@InformacaoValidavel(intervaloComprimento = { 0, 250 })
	private String site;

	public Transportadora() {
	}

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
	public boolean equals(Object o) {
		return o instanceof Transportadora && this.id != null
				&& this.id.equals(((Transportadora) o).id);
	}

	public String getAreaAtuacao() {
		return areaAtuacao;
	}

	public String getCnpj() {
		return cnpj;
	}

	public Contato getContatoPrincipal() {
		return this.listaContato != null && !this.listaContato.isEmpty() ? listaContato
				.get(0) : null;
	}

	public String getEndereco() {
		if (logradouro == null) {
			return "";
		}
		return logradouro.getEndereco();
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

	public String getMunicipio() {
		if (logradouro == null) {
			return "";
		}
		return logradouro.getCidade();
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

	public String getUf() {
		if (logradouro == null) {
			return "";
		}
		return logradouro.getUf();
	}

	@Override
	public int hashCode() {
		return this.id != null ? this.id.hashCode() : -1;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public List<ContatoTransportadora> limparContato() {
		final List<ContatoTransportadora> lista = new ArrayList<ContatoTransportadora>(
				this.listaContato);
		this.listaContato.clear();
		return lista;
	}

	public void setAreaAtuacao(String areaAtuacao) {
		this.areaAtuacao = areaAtuacao;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public void setCidade(String cidade) {
		if (logradouro == null) {
			logradouro = new Logradouro();
		}
		logradouro.setCidade(cidade);
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setEndereco(String endereco) {
		if (logradouro == null) {
			logradouro = new Logradouro();
		}
		logradouro.setEndereco(endereco);
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

	public void setUf(String uf) {
		if (logradouro == null) {
			logradouro = new Logradouro();
		}
		logradouro.setUf(uf);
	}
}
