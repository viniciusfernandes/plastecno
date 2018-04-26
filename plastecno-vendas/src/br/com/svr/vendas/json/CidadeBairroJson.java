package br.com.svr.vendas.json;

public final class CidadeBairroJson {

    private final Integer idBairro;
    private final String descricao;

    public CidadeBairroJson(Integer chave, String valor) {
        this.idBairro = chave;
        this.descricao = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getIdBairro() {
        return idBairro;
    }
}
