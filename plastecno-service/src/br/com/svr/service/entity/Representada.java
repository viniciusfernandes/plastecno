package br.com.svr.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.svr.service.constante.TipoApresentacaoIPI;
import br.com.svr.service.constante.TipoRelacionamento;
import br.com.svr.service.validacao.InformacaoValidavel;
import br.com.svr.service.validacao.TipoDocumento;

@Entity
@Table(name = "tb_representada", schema = "vendas")
@InformacaoValidavel
public class Representada implements Serializable {

	private static final long serialVersionUID = -6187905223269978645L;

	@InformacaoValidavel(positivo = true, nomeExibicao = "Aliquota de ICMS")
	@Column(name = "aliquota_icms")
	private Double aliquotaICMS;

	private boolean ativo = true;

	@InformacaoValidavel(tipoDocumento = TipoDocumento.CNPJ, nomeExibicao = "CNPJ")
	private String cnpj;

	@InformacaoValidavel(obrigatorio = true, numerico = true, nomeExibicao = "Comissão da representada")
	private double comissao = 0;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 150 }, nomeExibicao = "Email para envio dos pedidos")
	@Column(name = "email")
	private String email;

	@Id
	@SequenceGenerator(name = "representadaSequence", sequenceName = "vendas.seq_representada_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "representadaSequence")
	private Integer id;

	@Column(name = "insc_estadual")
	@InformacaoValidavel(intervaloComprimento = { 0, 12 }, tipoDocumento = TipoDocumento.INSCRICAO_ESTADUAL, nomeExibicao = "Inscricao estadual")
	private String inscricaoEstadual;

	@OneToMany(mappedBy = "representada", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@InformacaoValidavel(iteravel = true, nomeExibicao = "Lista de contato da representada")
	private List<ContatoRepresentada> listaContato;

	@ManyToMany(mappedBy = "listaRepresentada")
	private List<Material> listaMaterial;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Logradouro da representada")
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "id_logradouro_representada")
	private LogradouroRepresentada logradouro;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 150 }, nomeExibicao = "Nome fantasia")
	@Column(name = "nome_fantasia")
	private String nomeFantasia;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 150 }, nomeExibicao = "Razao social")
	@Column(name = "razao_social")
	private String razaoSocial;

	@InformacaoValidavel(intervaloComprimento = { 1, 150 }, nomeExibicao = "Site da representada")
	@Column(name = "site")
	private String site;

	private String telefone;

	@Column(name = "id_tipo_apresentacao_ipi")
	@Enumerated(EnumType.ORDINAL)
	private TipoApresentacaoIPI tipoApresentacaoIPI = TipoApresentacaoIPI.NUNCA;

	@Column(name = "id_tipo_relacionamento")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo de relacionamento")
	private TipoRelacionamento tipoRelacionamento;

	public Representada() {
	}

	public Representada(Integer id, String nomeFantasia) {
		this(id, nomeFantasia, null);
	}

	public Representada(Integer id, String nomeFantasia, String email) {
		this.id = id;
		this.nomeFantasia = nomeFantasia;
		this.email = email;
	}

	public Representada(Integer id, String nomeFantasia, TipoApresentacaoIPI tipoApresentacaoIPI,
			TipoRelacionamento tipoRelacionamento) {
		this.id = id;
		this.nomeFantasia = nomeFantasia;
		this.tipoApresentacaoIPI = tipoApresentacaoIPI;
		this.tipoRelacionamento = tipoRelacionamento;
	}

	public void addContato(ContatoRepresentada contato) {
		if (this.listaContato == null) {
			this.listaContato = new ArrayList<ContatoRepresentada>();
		}
		this.listaContato.add(contato);
		contato.setRepresentada(this);
	}

	public void addContato(List<ContatoRepresentada> listaContato) {
		for (ContatoRepresentada contato : listaContato) {
			this.addContato(contato);
		}
	}

	public void addMaterial(Material material) {
		if (listaMaterial == null) {
			setListaMaterial(new ArrayList<Material>());
		}
		listaMaterial.add(material);
		material.setRepresentada(this);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Representada && this.id != null && this.id.equals(((Representada) o).id);
	}

	public Double getAliquotaICMS() {
		return aliquotaICMS;
	}

	public String getCnpj() {
		return cnpj;
	}

	public double getComissao() {
		return comissao;
	}

	public double getComissaoPercentual() {
		return this.comissao * 100;
	}

	public String getEmail() {
		return email;
	}

	public Integer getId() {
		return id;
	}

	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public List<ContatoRepresentada> getListaContato() {
		return listaContato;
	}

	public List<Material> getListaMaterial() {
		return listaMaterial;
	}

	public LogradouroRepresentada getLogradouro() {
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

	public String getTelefone() {
		return telefone;
	}

	public TipoApresentacaoIPI getTipoApresentacaoIPI() {
		return tipoApresentacaoIPI;
	}

	public TipoRelacionamento getTipoRelacionamento() {
		return tipoRelacionamento;
	}

	@Override
	public int hashCode() {
		return this.id == null ? -1 : this.id;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public boolean isFornecedor() {
		return TipoRelacionamento.FORNECIMENTO.equals(tipoRelacionamento)
				|| TipoRelacionamento.REPRESENTACAO_FORNECIMENTO.equals(tipoRelacionamento);
	}

	public boolean isIPIHabilitado() {
		return !TipoApresentacaoIPI.NUNCA.equals(this.tipoApresentacaoIPI);
	}

	public boolean isPessoaJuridica() {
		return cnpj != null && !cnpj.isEmpty();
	}

	public boolean isRepresentda() {
		return TipoRelacionamento.REPRESENTACAO.equals(tipoRelacionamento);
	}

	public boolean isRevendedor() {
		return TipoRelacionamento.REVENDA.equals(tipoRelacionamento);
	}

	public void setAliquotaICMS(Double aliquotaICMS) {
		this.aliquotaICMS = aliquotaICMS;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setComissao(double comissao) {
		this.comissao = comissao;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	public void setListaContato(List<ContatoRepresentada> listaContato) {
		this.listaContato = listaContato;
	}

	void setListaMaterial(List<Material> listaMaterial) {
		this.listaMaterial = listaMaterial;
	}

	public void setLogradouro(LogradouroRepresentada logradouro) {
		this.logradouro = logradouro;
	}

	public void setMaterial(Material material) {
		if (listaMaterial == null) {
			setListaMaterial(new ArrayList<Material>());
		}
		listaMaterial.add(material);
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

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public void setTipoApresentacaoIPI(TipoApresentacaoIPI tipoApresentacaoIPI) {
		this.tipoApresentacaoIPI = tipoApresentacaoIPI;
	}

	public void setTipoRelacionamento(TipoRelacionamento tipoRelacionamento) {
		this.tipoRelacionamento = tipoRelacionamento;
	}
}
