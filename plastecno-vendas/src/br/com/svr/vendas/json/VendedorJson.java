package br.com.svr.vendas.json;

import br.com.svr.service.entity.Usuario;

public class VendedorJson {
    private final Integer id;
    private final String nome;
    private final String email;

    public VendedorJson(Usuario vendedor) {
        id = vendedor.getId();
        nome = vendedor.getNome();
        email = vendedor.getEmail();
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
