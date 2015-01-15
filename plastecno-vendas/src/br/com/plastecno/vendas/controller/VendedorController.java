package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.controller.exception.ControllerException;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

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

    @Get("vendedor/listagem/nome")
    public void pesquisarVendedorByNome(String nome) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Usuario> listaVendedor = usuarioService.pesquisarVendedorByNome(nome);
        for (Usuario vendedor : listaVendedor) {
            lista.add(new Autocomplete(vendedor.getId(), vendedor.getNome()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    @Get("vendedor/edicao")
    public void pesquisarVendedor(Integer idVendedor) {
        Usuario vendedor = this.usuarioService.pesquisarById(idVendedor);
        if (vendedor == null) {
            this.gerarListaMensagemErro("Vendedor n�o existe no sistema");
        } else {
            vendedor.setCpf(formatarCPF(vendedor.getCpf()));
            vendedor.addRemuneracao(this.usuarioService.pesquisarRemuneracaoById(idVendedor));
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
