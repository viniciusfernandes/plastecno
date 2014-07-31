package br.com.plastecno.service.impl.calculo;

import java.util.HashMap;
import java.util.Map;

import br.com.plastecno.service.calculo.exception.AlgoritmoCalculoException;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.util.NumeroUtils;

public class CalculadoraPreco {
    private static final Map<TipoVenda, AlgoritmoCalculo> mapaAlgoritmo;
    static {
        mapaAlgoritmo = new HashMap<TipoVenda, AlgoritmoCalculo>();
        mapaAlgoritmo.put(TipoVenda.KILO, new AlgoritmoCalculoPrecoKilo());
        mapaAlgoritmo.put(TipoVenda.PECA, new AlgoritmoCalculoPrecoPeca());
    }

    public static double calcular(ItemPedido itemPedido) throws AlgoritmoCalculoException {
        validarCalculo(itemPedido);

        AlgoritmoCalculo algoritmoCalculo = mapaAlgoritmo.get(itemPedido.getTipoVenda());
        if (algoritmoCalculo == null) {
            throw new AlgoritmoCalculoException("Não existe algoritmo para o cálculo do valor do tipo de venda "
                    + itemPedido.getTipoVenda());
        }
        return algoritmoCalculo.calcular(itemPedido);
    }

    public static double calcularPorUnidade(ItemPedido itemPedido) throws AlgoritmoCalculoException {
        return itemPedido.getQuantidade() != null ? calcular(itemPedido) / itemPedido.getQuantidade() : 0d;
    }

    public static double calcularPorUnidadeIPI(ItemPedido itemPedido) throws AlgoritmoCalculoException {
        return calcularPorUnidadeIPI(itemPedido, null);
    }

    public static double calcularPorUnidadeIPI(ItemPedido itemPedido, Double aliquotaIPI)
            throws AlgoritmoCalculoException {
        CalculadoraPreco.validarCalculoIPI(itemPedido, aliquotaIPI);

        if (aliquotaIPI == null) {
            aliquotaIPI = itemPedido.getFormaMaterial().getIpi();
        }
        return calcularPorUnidade(itemPedido) * (1 + aliquotaIPI);
    }

    private static void validarCalculoIPI(ItemPedido itemPedido, Double aliquotaIPI) throws AlgoritmoCalculoException {
        validarCalculo(itemPedido);

        if (aliquotaIPI == null && FormaMaterial.PC.equals(itemPedido.getFormaMaterial())) {
            throw new AlgoritmoCalculoException("Toda peca deve ter aliquota de IPI");
        }
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