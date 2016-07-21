package br.com.plastecno.vendas.json;

import java.util.ArrayList;
import java.util.List;

import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Transportadora;

/*
 * Essa classe foi criada para enviarmos a lista de transporda e redespacho
 * para a tela de pedido, ja que os conteudos dessas listas sao diferentes
 */
public class ClienteJson {
    private final String cnpj;
    private final String cpf;
    private final String email;
    private final Integer id;
    private String inscricaoEstadual;
    private final List<TransportadoraJson> listaRedespacho;
    private final List<TransportadoraJson> listaTransportadora;
    private final String nomeCompleto;
    private final String nomeFantasia;
    private final String razaoSocial;
    private final String site;

    private final String telefone;

    private final VendedorJson vendedor;

    public ClienteJson(Cliente cliente) {
        this(cliente, null);
    }
    public ClienteJson(Cliente cliente, List<Transportadora> listaTransportadora) {
        id = cliente.getId();
        nomeFantasia = cliente.getNomeFantasia();
        razaoSocial = cliente.getRazaoSocial();
        site = cliente.getSite();
        email = cliente.getEmail();
        cnpj = cliente.getCnpj();
        cpf = cliente.getCpf();
        inscricaoEstadual = cliente.getInscricaoEstadual();
        nomeCompleto = cliente.getNomeCompleto();
        // telefone = cliente.getContatoPrincipal() != null ?
        // cliente.getContatoPrincipal().getTelefoneFormatado() : "";
        telefone = "9999-9999";
        // vendedor = new VendedorJson(cliente.getVendedor());
        vendedor = null;
        this.listaTransportadora = new ArrayList<TransportadoraJson>();
        this.listaRedespacho = new ArrayList<TransportadoraJson>();

        if (cliente.getListaRedespacho() != null) {
            for (Transportadora redespacho : cliente.getListaRedespacho()) {
                this.listaRedespacho.add(new TransportadoraJson(redespacho));
            }
        }

        if (listaTransportadora != null) {
            for (Transportadora transportadora : listaTransportadora) {
                this.listaTransportadora.add(new TransportadoraJson(transportadora));
            }
        }
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public List<TransportadoraJson> getListaRedespacho() {
        return listaRedespacho;
    }

    public List<TransportadoraJson> getListaTransportadora() {
        return listaTransportadora;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getSite() {
        return site;
    }

    public String getTelefone() {
        return telefone;
    }

    public VendedorJson getVendedor() {
        return vendedor;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }
}
