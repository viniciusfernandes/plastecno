package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;


@Entity
@Table(name="tb_regiao", schema="vendas")
@InformacaoValidavel
public class Regiao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6789273528051821498L;
	@Id
	@SequenceGenerator(name = "regiaoSequence", sequenceName = "vendas.seq_regiao_id", allocationSize=1, initialValue=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "regiaoSequence")
	private Integer id;
	
	@InformacaoValidavel(obrigatorio=true, intervalo={1, 50}, nomeExibicao="Nome da regiao")
	private String nome;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="tb_regiao_tb_bairro", schema="vendas", 
			joinColumns={@JoinColumn(name = "id_regiao", referencedColumnName = "id")},
			inverseJoinColumns={@JoinColumn(name = "id_bairro", referencedColumnName = "id_bairro")})
	@InformacaoValidavel(obrigatorio=true, nomeExibicao="Bairros da regiao")
	private List<Bairro> listaBairro;
	
	public Regiao(){
	}
	
	public Regiao(String nome){
		this.nome = nome;
	}
	
	public void addBairro(Bairro bairro){
		if (this.listaBairro == null) {
			this.listaBairro = new ArrayList<Bairro>();
		}
		this.listaBairro.add(bairro);
	}

	public void addBairro(List<Bairro> listaBairro){
		if (this.listaBairro == null) {
			this.listaBairro = new ArrayList<Bairro>();
		}
		this.listaBairro.addAll(listaBairro);
	}

	public Integer getId() {
		return id;
	}

	public List<Bairro> getListaBairro() {
		return listaBairro;
	}

	public String getNome() {
		return nome;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setListaBairro(List<Bairro> listaBairro) {
		this.listaBairro = listaBairro;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
}
