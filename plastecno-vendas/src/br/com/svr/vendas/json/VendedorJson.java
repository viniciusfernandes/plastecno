package br.com.svr.vendas.json;

import br.com.svr.service.entity.Usuario;

public class VendedorJson {
    private final String email;
    private final Integer id;
    private final String nome;

    public VendedorJson(Integer id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public VendedorJson(Usuario usuario) {
        id = usuario.getId();
        nome = usuario.getNome();
        email = usuario.getEmail();
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
