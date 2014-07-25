package br.com.plastecno.vendas.json;

public class RepresentadaJson {
    private Integer id;
    private Boolean ipiHabilitado;

    public RepresentadaJson(Integer id, Boolean ipiHabilitado) {
        this.id = id;
        this.ipiHabilitado = ipiHabilitado;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIpiHabilitado() {
        return ipiHabilitado;
    }

    public void setIpiHabilitado(Boolean ipiHabilitado) {
        this.ipiHabilitado = ipiHabilitado;
    }

}
