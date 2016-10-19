package br.com.plastecno.vendas.json;

import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Transportadora;

public class TransportadoraJson {
    private final String cidade;
    private final String cnpj;
    private final String endereco;

    private final Integer id;
    private final String inscricaoEstadual;
    private final String nomeFantasia;
    private final String razaoSocial;
    private final String uf;

    public TransportadoraJson(Transportadora transp) {
      this(transp, null);
    }
    
    public TransportadoraJson(Transportadora transp, Logradouro logr) {
        id = transp == null ? 0 : transp.getId();
        nomeFantasia = transp == null ? "" : transp.getNomeFantasia();
        razaoSocial = transp == null ? "" : transp.getRazaoSocial();
        cnpj = transp == null ? "" : transp.getCnpj();
        inscricaoEstadual = transp == null ? "" : transp.getInscricaoEstadual();

        if (logr != null ) {
            endereco = logr.getEndereco();
            cidade = logr.getCidade();
            uf = logr.getUf();
        } else {
            endereco = "";
            cidade = "";
            uf = "";
        }
    }

    public String getCidade() {
        return cidade;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public Integer getId() {
        return id;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getUf() {
        return uf;
    }
}
