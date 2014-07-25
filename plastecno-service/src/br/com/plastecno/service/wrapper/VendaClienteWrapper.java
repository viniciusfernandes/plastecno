package br.com.plastecno.service.wrapper;

import br.com.plastecno.service.entity.Pedido;

public class VendaClienteWrapper extends Grupo {

	private final String dataEnvio;
	private final String nomeCliente;
	private final int numeroPedido;
	
	public VendaClienteWrapper(Pedido pedido) {
		super(pedido.getRepresentada().getNomeFantasia(), pedido.getValorPedido());
		this.dataEnvio = pedido.getDataEnvioFormatada();
		this.nomeCliente = pedido.getCliente().getRazaoSocial();
		this.numeroPedido = pedido.getId();
	}

	public String getDataEnvio() {
		return dataEnvio;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public int getNumeroPedido() {
		return numeroPedido;
	}

	public String getValorVendaFormatado(){
		return this.getValorFormatado();
	}
}
