package br.com.plastecno.service.impl;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.impl.calculo.AlgoritmoRecorte;
import br.com.plastecno.service.impl.calculo.AlgoritmoRecorteChapa;
import br.com.plastecno.service.impl.calculo.AlgoritmoRecorteTransversal;
import br.com.plastecno.service.impl.calculo.exception.RecorteInvalidoException;
import br.com.plastecno.service.wrapper.RecorteItemEstoqueWrapper;

public class RecortadorItemEstoque {

	private static AlgoritmoRecorte gerarAlgoritoRecorte(FormaMaterial formaMaterial) throws RecorteInvalidoException {
		switch (formaMaterial) {
		case TB:
			return new AlgoritmoRecorteTransversal();
		case BR:
			return new AlgoritmoRecorteTransversal();
		case BQ:
			return new AlgoritmoRecorteTransversal();
		case BS:
			return new AlgoritmoRecorteTransversal();
		case CH:
			return new AlgoritmoRecorteChapa();
		default:
			throw new RecorteInvalidoException("Não exite algoritmo de recorte para a forma de material \"" + formaMaterial
					+ "\"");
		}
	}

	public static RecorteItemEstoqueWrapper recortar(ItemEstoque itemEstoque, ItemEstoque itemRecortado)
			throws RecorteInvalidoException {
		validarMedidasRecorte(itemEstoque, itemRecortado);
		AlgoritmoRecorte algoritmoRecorte = gerarAlgoritoRecorte(itemEstoque.getFormaMaterial());
		return algoritmoRecorte.recortar(null, itemRecortado);
	}

	private static void validarMedidasRecorte(ItemEstoque itemEstoque, ItemEstoque itemRecortado)
			throws RecorteInvalidoException {
		if (itemRecortado.getMedidaExterna() > itemEstoque.getMedidaExterna()) {
			throw new RecorteInvalidoException(
					"Não é possível que a medida externa recortada seja maior do que a medida no estoque");
		}

		if (itemRecortado.contemLargura() && itemRecortado.getMedidaInterna() > itemEstoque.getMedidaInterna()) {
			throw new RecorteInvalidoException(
					"Não é possível que a medida interna recortada seja maior do que a medida no estoque");
		}

		if (itemRecortado.getComprimento() > itemEstoque.getComprimento()) {
			throw new RecorteInvalidoException(
					"Não é possível que o comprimento recortado seja maior do que o comprimento no estoque");
		}
	}

	private RecortadorItemEstoque() {
	}
}
