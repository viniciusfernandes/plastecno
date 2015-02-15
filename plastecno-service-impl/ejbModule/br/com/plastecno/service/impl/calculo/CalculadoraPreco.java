package br.com.plastecno.service.impl.calculo;

import java.util.HashMap;
import java.util.Map;

import br.com.plastecno.service.calculo.exception.AlgoritmoCalculoException;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.entity.ItemPedido;

public class CalculadoraPreco {
	public static double calcular(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		validarCalculo(itemPedido);

		AlgoritmoCalculo algoritmoCalculoPreco = mapaAlgoritmoPreco.get(itemPedido.getTipoVenda());
		if (algoritmoCalculoPreco == null) {
			throw new AlgoritmoCalculoException("Não existe algoritmo para o cálculo do valor do tipo de venda "
					+ itemPedido.getTipoVenda());
		}
		return algoritmoCalculoPreco.calcular(itemPedido);
	}

	public static double calcularPorUnidade(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		return itemPedido.getQuantidade() != null ? calcular(itemPedido) / itemPedido.getQuantidade() : 0d;
	}

	public static double calcularPorUnidadeIPI(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		Double aliquotaIPI = itemPedido.getAliquotaIPI();
		CalculadoraPreco.validarCalculoIPI(itemPedido);

		if (aliquotaIPI == null) {
			aliquotaIPI = itemPedido.getFormaMaterial().getIpi();
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

	private static void validarCalculoIPI(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		validarCalculo(itemPedido);

		if (itemPedido.getAliquotaIPI() == null && FormaMaterial.PC.equals(itemPedido.getFormaMaterial())) {
			throw new AlgoritmoCalculoException("Toda peca deve ter aliquota de IPI");
		}
	}

	private static final Map<TipoVenda, AlgoritmoCalculo> mapaAlgoritmoPreco;

	static {
		mapaAlgoritmoPreco = new HashMap<TipoVenda, AlgoritmoCalculo>();
		mapaAlgoritmoPreco.put(TipoVenda.KILO, new AlgoritmoCalculoPrecoKilo());
		mapaAlgoritmoPreco.put(TipoVenda.PECA, new AlgoritmoCalculoPrecoPeca());
	}
}