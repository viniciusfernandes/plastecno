package br.com.plastecno.service.impl.calculo;

import java.util.HashMap;
import java.util.Map;

import br.com.plastecno.service.calculo.exception.AlgoritmoCalculoException;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.impl.calculo.exception.VolumeInvalidoException;

public class CalculadoraVolume {
	
	private static Map<FormaMaterial, AlgoritmoCalculo> mapaAlgoritmo;
	
	static {
		mapaAlgoritmo = new HashMap<FormaMaterial, AlgoritmoCalculo>();
		mapaAlgoritmo.put(FormaMaterial.CH, new AlgoritmoCalculoVolumeCH());
		mapaAlgoritmo.put(FormaMaterial.BQ, new AlgoritmoCalculoVolumeBQ());
		mapaAlgoritmo.put(FormaMaterial.BR, new AlgoritmoCalculoVolumeBR());
		mapaAlgoritmo.put(FormaMaterial.TB, new AlgoritmoCalculoVolumeTB());
		//Estamos usando a aproximacao de uma barra sextavada por um tubo. Recomendacao do cliente.
		mapaAlgoritmo.put(FormaMaterial.BS, new AlgoritmoCalculoVolumeBR());
	}
	
	private CalculadoraVolume(){};
	
	public static double calcular(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		
		CalculadoraVolume.validarVolume(itemPedido);
		
		AlgoritmoCalculo algoritmoCalculo = mapaAlgoritmo.get(itemPedido.getFormaMaterial());
		if (algoritmoCalculo == null) {
			throw new AlgoritmoCalculoException("Não  existe algoritmo para o calculo de volume da forma de material "+itemPedido.getFormaMaterial());
		}
		return algoritmoCalculo.calcular(itemPedido);
	}
	
	private static void validarVolume(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		// A sequencia de execucao dos metodos eh importante
		if (itemPedido.getFormaMaterial() == null) {
			throw new AlgoritmoCalculoException("A forma do material do item do pedido deve ser preenchida");
		}
		validarPeca(itemPedido);
		validarIntegridadeMedidas(itemPedido);
		validarFormaMaterial(itemPedido);
	}
	
	private static void validarPeca(ItemPedido itemPedido) throws VolumeInvalidoException {
		final Double medidaExterna = itemPedido.getMedidaExterna();
		final Double medidaInterna = itemPedido.getMedidaInterna();
		final Double comprimento = itemPedido.getComprimento();
		final boolean isAlgumaMedidaPreenchida = medidaExterna != null || medidaInterna != null || comprimento != null; 
		
		if (itemPedido.isPeca() && isAlgumaMedidaPreenchida) {
			throw new VolumeInvalidoException ("Peças não podem ter medida");
		} 
	}
	
	private static void validarIntegridadeMedidas(ItemPedido itemPedido) throws VolumeInvalidoException {
		final Double medidaExterna = itemPedido.getMedidaExterna();
		final Double medidaInterna = itemPedido.getMedidaInterna();
		final Double comprimento = itemPedido.getComprimento();
		final boolean isMedidasEmBranco = medidaExterna == null && medidaInterna == null && comprimento == null;
		if (!itemPedido.isPeca() && isMedidasEmBranco) {
			throw new VolumeInvalidoException ("As medidas da forma do material estao em branco");
		}
		
		if ((medidaExterna != null && medidaExterna <= 0) 
				|| (medidaInterna != null && medidaInterna <= 0) 
				|| (comprimento != null && comprimento <= 0)) {
			throw new VolumeInvalidoException ("Os valores das medidas devem ser positivos");
		}
	}
	
	private static void validarFormaMaterial(ItemPedido itemPedido) throws VolumeInvalidoException {
		
		final Double medidaExterna = itemPedido.getMedidaExterna();
		final Double medidaInterna = itemPedido.getMedidaInterna();
		final Double comprimento = itemPedido.getComprimento();

		// Entendemos por solido todas as formas que nao tem medida interna
		boolean contemLargura = itemPedido.contemLargura();
		boolean formaVazada = itemPedido.isFormaMaterialVazada();
		
		// formas que nao possuem medida interna pois sao solidas
		if (medidaInterna != null && !contemLargura) {
			throw new VolumeInvalidoException(
					"A forma de material escolhida não tem medida interna");
		}

		if (medidaInterna == null && contemLargura) {
			throw new VolumeInvalidoException(
					"A forma de material escolhida deve ter medida interna");
		}

		if (formaVazada && medidaInterna.compareTo(medidaExterna) >= 0) {
			throw new VolumeInvalidoException(
					"A medida interna deve ser inferior a media externa");
		}

		if (!itemPedido.isPeca()
				&& (medidaExterna == null || comprimento == null)) {
			throw new VolumeInvalidoException(
					"A forma de material escolhida deve ter medida externa e comprimento");
		}
	}
}
