package br.com.plastecno.vendas.json;

public class PicklistJson {
    private final Integer id;
    private final String descricao;

    public PicklistJson(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}
