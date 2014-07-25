package br.com.plastecno.vendas.json;

import br.com.plastecno.service.entity.Transportadora;

public class TransportadoraJson {
    private final Integer id;
    private final String nomeFantasia;

    public TransportadoraJson(Transportadora transporadora) {
        id = transporadora.getId();
        nomeFantasia = transporadora.getNomeFantasia();
    }

    public Integer getId() {
        return id;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }
}
