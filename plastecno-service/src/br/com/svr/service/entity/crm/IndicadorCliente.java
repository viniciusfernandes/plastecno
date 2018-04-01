package br.com.svr.service.entity.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tb_indicador_cliente", schema = "crm")
public class IndicadorCliente {

	@Id
	@SequenceGenerator(name = "indicadorClienteSequence", sequenceName = "crm.seq_indicador_cliente_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicadorClienteSequence")
	private Integer id;

	@Column(name = "id_cliente")
	private Integer idCliente;

	@Column(name = "indice_conversao_quantidade")
	private double indiceConversaoQuantidade = 0d;

	@Column(name = "indice_conversao_valor")
	private double indiceConversaoValor = 0d;

	@Column(name = "quantidade_orcamentos")
	private int quantidadeOrcamentos = 0;

	@Column(name = "quantidade_vendas")
	private long quantidadeVendas = 0;

	@Column(name = "valor_medio")
	private double valorMedio = 0d;

	@Column(name = "valor_orcamentos")
	private double valorOrcamentos = 0;

	@Column(name = "valor_vendas")
	private double valorVendas = 0;

	public IndicadorCliente() {
	}

	public void calcularIndicadores() {
		if (valorVendas == 0 || valorOrcamentos == 0) {
			indiceConversaoValor = 0d;
		} else {
			indiceConversaoValor = valorVendas / valorOrcamentos;
		}
		if (quantidadeVendas == 0 || quantidadeOrcamentos == 0) {
			indiceConversaoQuantidade = 0d;
		} else {
			indiceConversaoQuantidade = quantidadeVendas / quantidadeOrcamentos;
		}
		if (quantidadeVendas == 0 || valorVendas == 0) {
			valorMedio = 0;
		} else {
			valorMedio = valorVendas / quantidadeVendas;
		}
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public double getIndiceConversaoQuantidade() {
		return indiceConversaoQuantidade;
	}

	public double getIndiceConversaoValor() {
		return indiceConversaoValor;
	}

	public int getQuantidadeOrcamentos() {
		return quantidadeOrcamentos;
	}

	public long getQuantidadeVendas() {
		return quantidadeVendas;
	}

	public double getValorMedio() {
		return valorMedio;
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

	public void setIndiceConversaoQuantidade(double indiceConversaoQuantidade) {
		this.indiceConversaoQuantidade = indiceConversaoQuantidade;
	}

	public void setIndiceConversaoValor(double indiceConversaoValor) {
		this.indiceConversaoValor = indiceConversaoValor;
	}

	public void setQuantidadeOrcamentos(int quantidadeOrcamentos) {
		this.quantidadeOrcamentos = quantidadeOrcamentos;
	}

	public void setQuantidadeVendas(long quantidadeVendas) {
		this.quantidadeVendas = quantidadeVendas;
	}

	public void setValorMedio(double valorMedio) {
		this.valorMedio = valorMedio;
	}

	public void setValorOrcamentos(double valorOrcamentos) {
		this.valorOrcamentos = valorOrcamentos;
	}

	public void setValorVendas(double valorVendas) {
		this.valorVendas = valorVendas;
	}
}
