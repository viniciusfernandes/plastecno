package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_observacao_cliente", schema="vendas")
public class ObservacaoCliente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 422899612343389268L;

	@Id
	@SequenceGenerator(name = "clienteObservacaoSequence", sequenceName = "vendas.seq_cliente_observacao_id", allocationSize=1, initialValue=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clienteObservacaoSequence")
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	@InformacaoValidavel(obrigatorio=true, nomeExibicao="Data de inclusão das observações sobre o cliente")
	@Column(name="data_inclusao")
	private Date dataInclusao;
	
	@InformacaoValidavel(obrigatorio=true, intervalo={1,800}, nomeExibicao="Descrição das observações sobre o cliente")
	private String descricao;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_vendedor")
	@InformacaoValidavel(obrigatorio=true, nomeExibicao="Vendedor do cliente")
	private Usuario vendedor;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getDataInclusao() {
		return dataInclusao;
	}
	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Usuario getVendedor() {
		return vendedor;
	}
	public void setVendedor(Usuario vendedor) {
		this.vendedor = vendedor;
	}
}
