package br.com.plastecno.vendas.controller;

public class PicklistElement {
    private final Object valor;
    private final Object label;

    public PicklistElement(Object valor, Object label) {
        this.valor = valor;
        this.label = label;
    }

    public Object getLabel() {
        return label;
    }

    public Object getValor() {
        return valor;
    }
}