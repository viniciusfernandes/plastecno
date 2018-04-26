package br.com.svr.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.ClienteService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.controller.exception.ControllerException;
import br.com.svr.vendas.json.SerializacaoJson;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class VendedorController extends AbstractController {
    @Servico
    private UsuarioService usuarioService;
    @Servico
    private ClienteService clienteService;

    public VendedorController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Vendedor");
        this.inicializarPicklist("Clientes", "Clientes Cadastrados", "Clientes Associados", "id", "nomeFantasia");
    }

    @Post("vendedor/associacaocliente")
    public void associarCliente(Usuario vendedor, List<Integer> listaIdClienteAssociado) {
        try {

            usuarioService.associarCliente(vendedor.getId(), listaIdClienteAssociado);

            gerarMensagemSucesso("Cliente(s) associado(s) com sucesso");
            pesquisarVendedor(vendedor.getId());
        } catch (BusinessException e) {
            vendedor.setCpf(formatarCPF(vendedor.getCpf()));
            addAtributo("vendedor", vendedor);
            try {
                popularPicklist(this.clienteService.pesquisarClientesAssociados(vendedor.getId()),
                        this.clienteService.pesquisarClientesDesassociados());
                gerarListaMensagemErro(e);
            } catch (ControllerException e1) {
                gerarLogErroNavegacao("Cliente", e1);
            }
            irTopoPagina();
        } catch (Exception e) {
            gerarLogErroInclusao("Vendedor", e);
            irTopoPagina();
        }
    }

    @Post("vendedor/desassociacaocliente")
    public void desassociarCliente(Usuario vendedor, List<Integer> listaIdClienteDesassociado) {
        try {

            usuarioService.desassociarCliente(vendedor.getId(), listaIdClienteDesassociado);

            gerarMensagemSucesso("Cliente(s) desassociado(s) com sucesso");
            pesquisarVendedor(vendedor.getId());
        } catch (BusinessException e) {
            vendedor.setCpf(formatarCPF(vendedor.getCpf()));
            addAtributo("vendedor", vendedor);
            try {
                popularPicklist(this.clienteService.pesquisarClientesAssociados(vendedor.getId()),
                        this.clienteService.pesquisarClientesDesassociados());
                gerarListaMensagemErro(e);
            } catch (ControllerException e1) {
                gerarLogErroNavegacao("Cliente", e1);
            }
            irTopoPagina();
        } catch (Exception e) {
            gerarLogErroInclusao("Vendedor", e);
            irTopoPagina();
        }
    }

    @Get("vendedor/listagem")
    public void pesquisar(Usuario filtro, Integer paginaSelecionada) {
        final PaginacaoWrapper<Usuario> paginacao = this.usuarioService.paginarVendedor(filtro, null,
                this.calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());

        this.inicializarPaginacao(paginaSelecionada, paginacao, "listaVendedor");
        addAtributo("vendedor", filtro);
    }

    @Get("vendedor/edicao")
    public void pesquisarVendedor(Integer idVendedor) {
        Usuario vendedor = this.usuarioService.pesquisarById(idVendedor);
        if (vendedor == null) {
            this.gerarListaMensagemErro("Vendedor não existe no sistema");
        } else {
            vendedor.setCpf(formatarCPF(vendedor.getCpf()));
            addAtributo("vendedor", vendedor);

            try {
                popularPicklist(clienteService.pesquisarClientesDesassociados(),
                        clienteService.pesquisarClientesAssociados(idVendedor));
            } catch (ControllerException e) {
                gerarLogErroNavegacao("Vendedor", e);
            }
        }
        irTopoPagina();
    }

    @Get("vendedor/listagem/nome")
    public void pesquisarVendedorByNome(String nome) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Usuario> listaVendedor = usuarioService.pesquisarVendedorByNome(nome);
        for (Usuario vendedor : listaVendedor) {
            lista.add(new Autocomplete(vendedor.getId(), vendedor.getNome()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    @Get("vendedor")
    public void vendedorHome() {
        if (!isElementosNaoAssociadosPreenchidosPicklist()) {
            try {
                popularPicklist(null, null);
            } catch (ControllerException e) {
                gerarLogErroNavegacao("Material", e);
            }
        }
    }
}
