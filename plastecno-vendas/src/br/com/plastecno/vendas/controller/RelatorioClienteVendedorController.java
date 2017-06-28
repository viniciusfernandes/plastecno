package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioClienteVendedorController extends AbstractController {

    @Servico
    private ClienteService clienteService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private UsuarioService usuarioService;

    public RelatorioClienteVendedorController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/cliente/vendedor/pedido/pdf")
    public Download downloadPedidoPDF(Integer idPedido) {
        return redirecTo(PedidoController.class).downloadPedidoPDF(idPedido, TipoPedido.REVENDA);
    }

    @Get("relatorio/cliente/vendedor/listagem")
    public void gerarRelatorioClienteVendedor(Integer idVendedor, boolean inativo) {
        Usuario vend = usuarioService.pesquisarUsuarioResumidoById(idVendedor);
        try {
            String titulo = vend != null ? "Clientes do Vendedor " + vend.getNome() : "Clientes";
            if (inativo) {
                titulo += ". Inativos desde " + StringUtils.formatarData(clienteService.gerarDataInatividadeCliente());
            }
            addAtributo("titulo", titulo);
            addAtributo("listaCliente",
                    relatorioService.gerarRelatorioClienteVendedor(idVendedor, inativo));

            irRodapePagina();
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }

        addAtributo("inativo", inativo);
        addAtributo("vendedor", vend);
    }

    @Get("relatorio/cliente/vendedor/nome")
    public void pesquisarClienteByNome(String nome) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Usuario> listaUsuario = usuarioService.pesquisarByNome(nome);
        for (Usuario usuario : listaUsuario) {
            lista.add(new Autocomplete(usuario.getId(), usuario.getNomeCompleto()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    @Get("relatorio/cliente/vendedor")
    public void relatorioClienteVendedorHome() {
        boolean acessoClienteVendedorPermitido = isAcessoPermitido(TipoAcesso.ADMINISTRACAO);
        if (!acessoClienteVendedorPermitido) {
            addAtributo("vendedor", usuarioService.pesquisarUsuarioResumidoById(getCodigoUsuario()));
        }
        addAtributo("acessoClienteVendedorPermitido", acessoClienteVendedorPermitido);
        addAtributo("relatorioGerado", contemAtributo("listaCliente"));
    }
}
