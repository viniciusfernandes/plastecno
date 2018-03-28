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

	@Column(name = "indice_conversao_valor")
	private double indiceValor = 0d;

	@Column(name = "quantidade_orcamentos")
	private long quantidadeOrcamentos = 0;

	@Column(name = "quantidade_vendas")
	private long quantidadeVendas = 0;

	@Column(name = "valor_orcamentos")
	private double valorOrcamentos = 0;

	@Column(name = "valor_vendas")
	private double valorVendas = 0;

	public IndiceConversao() {
	}

	public double calcularIndice() {
		if (valorVendas == 0 || valorOrcamentos == 0) {
			return 0d;
		}
		return indiceValor = valorVendas / valorOrcamentos;
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public double getIndiceValor() {
		return indiceValor;
	}

	public long getQuantidadeOrcamentos() {
		return quantidadeOrcamentos;
	}

	public long getQuantidadeVendas() {
		return quantidadeVendas;
	}

	public double getValorOrcamentos() {
		return valorOrcamentos;
	}

	public double getValorVendas() {
		return valorVendas;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public void setIndiceValor(double indiceValor) {
		this.indiceValor = indiceValor;
	}

	public void setQuantidadeOrcamentos(long quantidadeOrcamentos) {
		this.quantidadeOrcamentos = quantidadeOrcamentos;
	}

	public void setQuantidadeVendas(long quantidadeVendas) {
		this.quantidadeVendas = quantidadeVendas;
	}

	public void setValorOrcamentos(double valorOrcamentos) {
		this.valorOrcamentos = valorOrcamentos;
	}

	public void setValorVendas(double valorVendas) {
		this.valorVendas = valorVendas;
	}
}
