package br.com.svr.vendas.json;

public class RepresentadaJson {
    private Integer id;
    private Boolean ipiHabilitado;
    private final String nomeFantasia;

    public RepresentadaJson(Integer id, Boolean ipiHabilitado) {
        this.id = id;
        this.ipiHabilitado = ipiHabilitado;
        this.nomeFantasia = null;
    }

    public RepresentadaJson(Integer id, String nomeFantasia) {
        this.id = id;
        this.nomeFantasia = nomeFantasia;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getIpiHabilitado() {
        return ipiHabilitado;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIpiHabilitado(Boolean ipiHabilitado) {
        this.ipiHabilitado = ipiHabilitado;
    }

}
