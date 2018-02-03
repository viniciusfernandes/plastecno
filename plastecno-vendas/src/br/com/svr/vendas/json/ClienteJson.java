package br.com.svr.vendas.json;

import java.util.ArrayList;
import java.util.List;

import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.Contato;
import br.com.svr.service.entity.Logradouro;
import br.com.svr.service.entity.LogradouroCliente;
import br.com.svr.service.entity.Transportadora;

/*
 * Essa classe foi criada para enviarmos a lista de transporda e redespacho
 * para a tela de pedido, ja que os conteudos dessas listas sao diferentes
 */
public class ClienteJson {
    private final String cnpj;
    private final String cpf;
    private String ddd;
    private final String email;
    private final String emailContato;
    private final Integer id;
    private String inscricaoEstadual;
    private final List<TransportadoraJson> listaRedespacho;
    private final List<TransportadoraJson> listaTransportadora;
    private final LogradouroJson logradouroFaturamento;
    private final String logradouroFormatado;
    private final String nomeCompleto;
    private final String nomeContato;
    private final String nomeFantasia;
    private final String razaoSocial;
    private final String site;
    private final String suframa;
    private String telefone;
    private final VendedorJson vendedor;

    public ClienteJson(Cliente cliente) {
        this(cliente, (List<Transportadora>) null, null, null);
    }

    public ClienteJson(Cliente cliente, boolean telefoneFormatado) {
        this(cliente, (List<Transportadora>) null, null, null);
    }

    public ClienteJson(Cliente cliente, List<Transportadora> listaTransportadora) {
        this(cliente, listaTransportadora, null, null);
    }

    public ClienteJson(Cliente cliente, List<Transportadora> listaTransportadora, List<Transportadora> listaRedespacho,
            Logradouro logradouro) {
        this.listaTransportadora = new ArrayList<TransportadoraJson>();
        this.listaRedespacho = new ArrayList<TransportadoraJson>();

        if (cliente != null) {
            id = cliente.getId();
            nomeFantasia = cliente.getNomeFantasia();
            razaoSocial = cliente.getRazaoSocial();
            site = cliente.getSite();
            email = cliente.getEmail();
            cnpj = cliente.getCnpj();
            cpf = cliente.getCpf();
            inscricaoEstadual = cliente.getInscricaoEstadual();
            nomeCompleto = cliente.getNomeCompleto();

            Contato c = cliente.getContatoPrincipal();
            telefone = c != null ? c.getTelefoneFormatado() : "";
            nomeContato = c != null ? c.getNome() : "";
            emailContato = c != null ? c.getEmail() : "";

            vendedor = cliente.getVendedor() == null ? null : new VendedorJson(cliente.getVendedor());
            suframa = cliente.getInscricaoSUFRAMA();
            logradouroFaturamento = new LogradouroJson(logradouro);
            if (logradouro != null) {
                logradouroFormatado = logradouro.getCepEnderecoNumeroBairro();
            } else {
                logradouroFormatado = "";
            }

            if (listaRedespacho != null) {
                for (Transportadora redespacho : listaRedespacho) {
                    this.listaRedespacho.add(new TransportadoraJson(redespacho));
                }
            }

            if (listaTransportadora != null) {
                for (Transportadora transportadora : listaTransportadora) {
                    this.listaTransportadora.add(new TransportadoraJson(transportadora));
                }
            }
        } else {
            id = null;
            nomeFantasia = "";
            razaoSocial = "";
            site = "";
            email = "";
            cnpj = "";
            cpf = "";
            inscricaoEstadual = "";
            nomeCompleto = "";
            telefone = "";
            vendedor = null;
            suframa = "";
            logradouroFaturamento = new LogradouroJson(null);
            logradouroFormatado = "";
            nomeContato = "";
            emailContato = "";
        }
    }

    public ClienteJson(Cliente cliente, List<Transportadora> listaTransportadora, Logradouro logradouro) {
        this(cliente, listaTransportadora, null, logradouro);
    }

    public ClienteJson(Cliente cliente, LogradouroCliente logradouro) {
        this(cliente, null, logradouro);
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getCpf() {
        return cpf;
    }

    public String getDdd() {
        return ddd;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailContato() {
        return emailContato;
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

    public LogradouroJson getLogradouroFaturamento() {
        return logradouroFaturamento;
    }

    public String getLogradouroFormatado() {
        return logradouroFormatado;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getNomeContato() {
        return nomeContato;
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

    public String getSuframa() {
        return suframa;
    }

    public String getTelefone() {
        return telefone;
    }

    public VendedorJson getVendedor() {
        return vendedor;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public void setDDDTelefone(String ddd, String telefone) {
        this.ddd = ddd;
        this.telefone = telefone;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
