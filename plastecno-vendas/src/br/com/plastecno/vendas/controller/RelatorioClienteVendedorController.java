package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioClienteVendedorController extends AbstractController {

    public RelatorioClienteVendedorController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Servico
    private UsuarioService usuarioService;

    @Servico
    private ClienteService clienteService;

    @Get("relatorio/cliente/vendedor")
    public void relatorioClienteVendedorHome() {
        boolean acessoClienteVendedorPermitido = isAcessoPermitido(TipoAcesso.ADMINISTRACAO);
        if (!acessoClienteVendedorPermitido) {
            addAtributo("vendedor", this.usuarioService.pesquisarUsuarioResumidoById(getCodigoUsuario()));
        }
        addAtributo("acessoClienteVendedorPermitido", acessoClienteVendedorPermitido);
        addAtributo("relatorioGerado", contemAtributo("listaCliente"));
    }

    @Get("relatorio/cliente/vendedor/listagem")
    public void gerarRelatorioClienteVendedor(Integer idVendedor, boolean pesquisaClienteInativo) {
        List<Cliente> listaCliente = null;

        try {
            listaCliente = this.clienteService.pesquisarByIdVendedor(idVendedor, pesquisaClienteInativo);
            for (Cliente cliente : listaCliente) {
                cliente.setDataUltimoContatoFormatada(this.formatarData(cliente.getDataUltimoContato()));
            }
            addAtributo("listaCliente", listaCliente);
            addAtributo("tituloRelatorio", "Relatório de Clientes " + (pesquisaClienteInativo ? "Inativos" : "Ativos"));
            irRodapePagina();
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }

        addAtributo("pesquisaClienteInativo", pesquisaClienteInativo);
        addAtributo("vendedor", this.usuarioService.pesquisarUsuarioResumidoById(idVendedor));
    }

    @Get("relatorio/cliente/vendedor/nome")
    public void pesquisarClienteByNome(String nome) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Usuario> listaUsuario = this.usuarioService.pesquisarByNome(nome);
        for (Usuario usuario : listaUsuario) {
            lista.add(new Autocomplete(usuario.getId(), usuario.getNomeCompleto()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }
}
