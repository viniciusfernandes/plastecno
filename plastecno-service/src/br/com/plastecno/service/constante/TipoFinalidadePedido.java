package br.com.plastecno.service.constante;

public enum TipoFinalidadePedido {
    INDUSTRIALIZACAO("INDUSTRIALIZAÇÃO"), 
    CONSUMO("CONSUMO"), 
    REVENDA("REVENDA"),
    NOTA_ENTRADA("NOTA DE ENTRADA"),
    RETORNO("RETORNO"),
    DEVOLUCAO("DEVOLUÇÃO"),
    AMOSTRA_GRATIS("AMOSTRA GRÁTIS"),
    REMESSA_INDUSTRIALIZACAO("REMESSA P/ INDUSTRIALIZAÇÃO"),
    REMESSA_CONSERTO("REMESSA P/ CONSERTO"),
    REMESSA_ANALISE("REMESSA P/ ANÁLISE"),
    SIMPLES_REMESSA("SIMPLES REMESSA"),
    OUTRA_ENTRADA("OUTRA ENTRADA"),
    PRIMEIRA_NOTA_TRIANGULAR("PRIMEIRA NOTA TRIANGULAR");

    
    private String descricao;

    private TipoFinalidadePedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }
}
