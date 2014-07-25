package br.com.plastecno.service.constante;

public enum FinalidadePedido {
    INDUSTRIALIZACAO("INDUSTRIALIZAÇÃO"), CONSUMO("CONSUMO"), REVENDA("REVENDA");

    private String descricao;

    private FinalidadePedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }
}
