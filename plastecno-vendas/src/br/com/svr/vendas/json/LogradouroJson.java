package br.com.svr.vendas.json;

import br.com.svr.service.entity.Logradouro;

public class LogradouroJson {
    private final String bairro;
    private final String cep;
    private final String cidade;
    private final String complemento;
    private final String endereco;
    private final String numero;
    private final String pais;
    private final String uf;

    public LogradouroJson(Logradouro logradouro) {
        if (logradouro == null) {
            cep = "";
            endereco = "";
            numero = "";
            complemento = "";
            bairro = "";
            cidade = "";
            uf = "";
            pais = "";
        } else {
            cep = logradouro.getCep();
            endereco = logradouro.getEndereco();
            numero = logradouro.getNumero();
            complemento = logradouro.getComplemento();
            bairro = logradouro.getBairro();
            cidade = logradouro.getCidade();
            uf = logradouro.getUf();
            pais = logradouro.getPais();
        }
    }

    public String getBairro() {
        return bairro;
    }

    public String getCep() {
        return cep;
    }

    public String getCidade() {
        return cidade;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getNumero() {
        return numero;
    }

    public String getPais() {
        return pais;
    }

    public String getUf() {
        return uf;
    }

}
