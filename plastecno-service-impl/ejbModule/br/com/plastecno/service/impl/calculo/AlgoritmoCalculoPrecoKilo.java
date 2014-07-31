package br.com.plastecno.service.impl.calculo;

import br.com.plastecno.service.calculo.exception.AlgoritmoCalculoException;
import br.com.plastecno.service.entity.ItemPedido;

public class AlgoritmoCalculoPrecoKilo implements AlgoritmoCalculo {

	private final double RAZAO_MILIMETRO_CUBICO_PARA_CENTIMETRO_CUBICO = 0.001;
	private final double RAZAO_GRAMAS_PARA_KILOS = 0.001;
	
	@Override
	public double calcular(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		if (itemPedido.getMaterial() == null || itemPedido.getMaterial().getPesoEspecifico() == null) {
			throw new AlgoritmoCalculoException("Peso específico do material não pode ser nulo");
		}
		// multiplicando a quantidade de itens pelo valor da venda pelo peso de cada item
		return itemPedido.calcularPrecoTotal() * calcularPesoEmKilos(itemPedido);
	}
	
	private Double calcularPesoEmKilos(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		/* 
		 * Transformando o volume em milimentros cubicos para centimetros cubicos e 
		 * multiplicando pelo valor do peso especifico teremos o peso em "gramas".
		 * Posteriormente multiplicamos por 1000 para recuperarmos o peso em kilos.
		 */
		return CalculadoraVolume.calcular(itemPedido) * RAZAO_MILIMETRO_CUBICO_PARA_CENTIMETRO_CUBICO 
				* itemPedido.getMaterial().getPesoEspecifico() * RAZAO_GRAMAS_PARA_KILOS;
				
	}
}
