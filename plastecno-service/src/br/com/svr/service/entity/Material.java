package br.com.svr.service.entity;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_material", schema = "vendas")
@InformacaoValidavel
public class Material implements Serializable {
	private static final long serialVersionUID = 9196522865218486979L;

	private boolean ativo = true;
	@InformacaoValidavel(trim = true, intervaloComprimento = { 0, 50 }, nomeExibicao = "Descrição do Material")
	private String descricao;

	@Id
	@SequenceGenerator(name = "materialSequence", sequenceName = "vendas.seq_material_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "materialSequence")
	private Integer id;

	private boolean importado;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "tb_material_tb_representada", schema = "vendas", joinColumns = { @JoinColumn(name = "id_material", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "id_representada", referencedColumnName = "id") })
	private List<Representada> listaRepresentada;

	@Column(name = "peso_especifico")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Peso especifico do Material")
	private Double pesoEspecifico;

	@InformacaoValidavel(trim = true, obrigatorio = true, intervaloComprimento = { 1, 10 }, nomeExibicao = "Sigla do Material")
	private String sigla;

	public Material() {

	}

	public Material(Double pesoEspecifico) {
		this.pesoEspecifico = pesoEspecifico;
	}

	public Material(Integer id) {
		this.id = id;
	}

	public Material(Integer id, String sigla, String descricao) {
		this.id = id;
		this.sigla = sigla;
		this.descricao = descricao;
	}

	public void addRepresentada(final List<Representada> listaRepresentada) {
		if (this.listaRepresentada == null) {
			setListaRepresentada(new ArrayList<Representada>());
		}
		this.listaRepresentada.addAll(listaRepresentada);
	}

	public void addRepresentada(final Representada representada) {
		if (this.listaRepresentada == null) {
			setListaRepresentada(new ArrayList<Representada>());
		}
		this.listaRepresentada.add(representada);
		representada.setMaterial(this);
	}

	public void clearListaRepresentada() {
		if (this.listaRepresentada != null) {
			this.listaRepresentada.clear();
		}
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Material && id != null && id.equals(((Material) o).id);
	}

	public String getDescricao() {
		return descricao;
	}

	public String getDescricaoFormatada() {
		return descricao == null ? sigla : sigla + " - " + descricao;
	}

	public Integer getId() {
		return id;
	}

	public List<Representada> getListaRepresentada() {
		return listaRepresentada;
	}

	public Double getPesoEspecifico() {
		return pesoEspecifico;
	}

	public String getSigla() {
		return sigla;
	}

	@Override
	public int hashCode() {
		return id != null ? id : -1;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public boolean isImportado() {
		return importado;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setImportado(boolean importado) {
		this.importado = importado;
	}

	void setListaRepresentada(List<Representada> listaRepresentada) {
		this.listaRepresentada = listaRepresentada;
	}

	public void setPesoEspecifico(Double pesoEspecifico) {
		this.pesoEspecifico = pesoEspecifico;
	}

	public void setRepresentada(Representada representada) {
		if (listaRepresentada == null) {
			setListaRepresentada(new ArrayList<Representada>());
		}
		listaRepresentada.add(representada);
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
}
