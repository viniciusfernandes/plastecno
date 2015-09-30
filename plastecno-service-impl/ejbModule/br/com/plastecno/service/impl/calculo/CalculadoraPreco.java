package br.com.plastecno.service.impl.calculo;

import java.util.HashMap;
import java.util.Map;

import br.com.plastecno.service.calculo.exception.AlgoritmoCalculoException;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.entity.ItemPedido;

public class CalculadoraPreco {
	private static final Map<TipoVenda, AlgoritmoCalculo> mapaAlgoritmoPreco;

	static {
		mapaAlgoritmoPreco = new HashMap<TipoVenda, AlgoritmoCalculo>();
		mapaAlgoritmoPreco.put(TipoVenda.KILO, new AlgoritmoCalculoPrecoKilo());
		mapaAlgoritmoPreco.put(TipoVenda.PECA, new AlgoritmoCalculoPrecoPeca());
	}

	public static double calcular(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		validarCalculo(itemPedido);

		AlgoritmoCalculo algoritmoCalculoPreco = mapaAlgoritmoPreco.get(itemPedido.getTipoVenda());
		if (algoritmoCalculoPreco == null) {
			String mensagem = itemPedido.getTipoVenda() == null ? "O tipo de venda esta em branco. Selecione um tipo de venda"
					: "Não existe algoritmo para o cálculo do valor do tipo de venda " + itemPedido.getTipoVenda();
			throw new AlgoritmoCalculoException(mensagem);
		}
		return algoritmoCalculoPreco.calcular(itemPedido);
	}

	public static double calcularPorUnidade(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		return itemPedido.getQuantidade() != null ? calcular(itemPedido) / itemPedido.getQuantidade() : 0d;
	}

	public static double calcularPorUnidadeIPI(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		Double aliquotaIPI = itemPedido.getAliquotaIPI();
		CalculadoraPreco.validarCalculo(itemPedido);

		if (aliquotaIPI == null) {
			aliquotaIPI = 0d;
		}
		return calcularPorUnidade(itemPedido) * (1 + aliquotaIPI);
	}

	private static void validarCalculo(ItemPedido itemPedido) throws AlgoritmoCalculoException {

		if (itemPedido == null) {
			throw new AlgoritmoCalculoException("Item do pedido é obrigatório para o cálculo do preço");
		}

		if (itemPedido != null && itemPedido.getFormaMaterial() == null) {
			throw new AlgoritmoCalculoException("Forma do material é obrigatório para o cálculo do preço");
		}
	}
}