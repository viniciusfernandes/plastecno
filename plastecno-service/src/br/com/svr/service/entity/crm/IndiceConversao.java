package br.com.svr.service.entity.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tb_indice_conversao", schema = "crm")
public class IndiceConversao {

	@Id
	@SequenceGenerator(name = "indiceConversaoSequence", sequenceName = "crm.seq_indice_conversao_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indiceConversaoSequence")
	private Integer id;

	@Column(name = "id_cliente")
	private Integer idCliente;

	@Column(name = "indice_conversao_quantidade")
	private double indiceQuandidade = 0d;

	@Column(name = "indice_conversao_valor")
	private double indiceValor = 0d;

	@Column(name = "quantidade_vendas")
	private int quantidadeVendas = 0;

	public IndiceConversao() {
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public double getIndiceQuandidade() {
		return indiceQuandidade;
	}

	public double getIndiceValor() {
		return indiceValor;
	}

	public int getQuantidadeVendas() {
		return quantidadeVendas;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public void setIndiceQuandidade(double indiceQuandidade) {
		this.indiceQuandidade = indiceQuandidade;
	}

	public void setIndiceValor(double indiceValor) {
		this.indiceValor = indiceValor;
	}

	public void setQuantidadeVendas(int quantidadeVendas) {
		this.quantidadeVendas = quantidadeVendas;
	}

}
