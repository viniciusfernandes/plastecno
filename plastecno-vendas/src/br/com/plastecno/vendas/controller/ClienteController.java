package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ContatoService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.RamoAtividadeService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.UsuarioService;
import static br.com.plastecno.service.constante.TipoAcesso.*;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ComentarioCliente;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.controller.exception.ControllerException;
import br.com.plastecno.vendas.json.ClienteJson;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class ClienteController extends AbstractController {
    @Servico
    private ClienteService clienteService;

    @Servico
    private ContatoService contatoService;

    @Servico
    private LogradouroService logradouroService;

    @Servico
    private RamoAtividadeService ramoAtividadeService;

    @Servico
    private TransportadoraService transportadoraService;

    @Servico
    private UsuarioService usuarioService;

    public ClienteController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);

        setNomeTela("Cliente");
        inicializarPicklist("Redespacho", "Cadastradas", "Redespacho", "id", "nomeFantasia", false, "Transportadora");
        verificarPermissaoAcesso("isAssociacaoVendedorPermitida", ADMINISTRACAO, GERENCIA_VENDAS);
    }

    @Post("cliente/associacaovendedor")
    public void associarVendedor(Integer idVendedor, Integer idCliente, boolean isRevendedor) {
        try {
            usuarioService.associarCliente(idVendedor, idCliente);
            gerarMensagemSucesso("O cliente foi associado com sucesso.");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        redirecTo(this.getClass()).pesquisarClienteById(idCliente, isRevendedor);
    }

    @Get("cliente")
    public void clienteHome() {
        habilitarMultiplosLogradouros();
        addAtributo("listaRamoAtividade", this.ramoAtividadeService.pesquisarAtivo());

        inicializarComboTipoLogradouro();
        // bloqueando alteracao dos dados do usuario por outro vendedor nao
        // associado
        final Cliente cliente = (Cliente) getAtributo("cliente");
        final boolean isInclusaoCliente = cliente == null || cliente.getId() == null;
        final boolean isVendedorIgual = cliente != null && cliente.getVendedor() != null
                && getCodigoUsuario().equals(cliente.getVendedor().getId());
        final boolean isAcessoEdicao = isAcessoPermitido(ADMINISTRACAO, GERENCIA_VENDAS, FATURAMENTO);
        boolean isRevendedor = cliente != null && cliente.isRevendedor();
        addAtributo("acessoInclusaoClientePermitido", isAcessoEdicao || isInclusaoCliente || isVendedorIgual
                || isRevendedor);
    }

    @Post("cliente/contactar")
    public void contactar(Integer idClienteContactado) {
        try {
            this.clienteService.contactarCliente(idClienteContactado);
            gerarMensagemSucesso("Cliente contactado com sucesso");
        } catch (Exception e) {
            gerarLogErroInclusao("Cliente", e);
        }
        irTopoPagina();
    }

    private String formatarComentarios(Integer idCliente) {
        List<ComentarioCliente> listaComentario = this.clienteService.pesquisarComentarioByIdCliente(idCliente);
        StringBuilder concat = new StringBuilder();
        for (ComentarioCliente comentarioCliente : listaComentario) {
            concat.append("\n");
            concat.append(StringUtils.formatarData(comentarioCliente.getDataInclusao()));
            concat.append(" - ");
            concat.append(comentarioCliente.getNomeVendedor());
            concat.append(" ");
            concat.append(comentarioCliente.getSobrenomeVendedor());
            concat.append(" - ");
            concat.append(comentarioCliente.getConteudo());
            concat.append("\n");
        }
        return concat.toString();
    }

    @Get("cliente/importarlogradouro")
    public void importar() {
        System.out.println("Inicio da importacao");
        logradouroService.importarCodigoMunicipio();
        System.out.println("Fim da importacao");
        redirecTo(MenuController.class).menuHome();
    }

    @Post("cliente/inclusao")
    public void inserirCliente(Cliente cliente, String comentario, List<LogradouroCliente> listaLogradouro,
            List<ContatoCliente> listaContato, List<Integer> listaIdTransportadoraAssociada, boolean isRevendedor) {
        try {
            if (temElementos(listaLogradouro)) {
                cliente.setListaLogradouro(listaLogradouro);
            }

            if (temElementos(listaContato)) {
                cliente.setListaContato(new HashSet<ContatoCliente>(listaContato));
            }

            if (temElementos(listaIdTransportadoraAssociada)) {
                cliente.setListaRedespacho(transportadoraService.pesquisarById(listaIdTransportadoraAssociada));
            }

            if (cliente.getId() == null) {
                cliente.setVendedor(new Usuario(getCodigoUsuario()));
            }

            StringBuilder mensagem = new StringBuilder();
            if (isRevendedor) {
                cliente = clienteService.alterarRevendedor(cliente);
                mensagem.append("O revendedor ").append(cliente.getNomeCompleto()).append(" foi incluído com sucesso");
            } else {
                cliente = clienteService.inserir(cliente);
                mensagem.append("O cliente ").append(cliente.getNomeCompleto()).append(" foi incluído com sucesso");
            }

            clienteService.inserirComentario(cliente.getId(), comentario, getCodigoUsuario());

            gerarMensagemSucesso(mensagem.toString());
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        } catch (Exception e) {
            gerarLogErro("inclusao/alteracao de cliente", e);
        }

        cliente.setDataNascimentoFormatada(StringUtils.formatarData(cliente.getDataNascimento()));

        if (isRevendedor) {
            redirecTo(this.getClass()).revendedorHome();
        } else {
            carregarVendedor(cliente);
            addAtributo("cliente", cliente);
            addAtributo("listaLogradouro", listaLogradouro);
            addAtributo("listaContato", listaContato);
            addAtributo("ramoAtividadeSelecionado", cliente.getRamoAtividade() != null ? cliente.getRamoAtividade()
                    .getId() : null);
            addAtributo("comentarios", formatarComentarios(cliente.getId()));
            try {
                // Temos que manter as transportadoras escolhidas na tela em
                // caso excecao de negocios
                popularPicklist(null, cliente.getListaRedespacho());
            } catch (ControllerException e) {
                gerarLogErroNavegacao("Cliente", e);
            }
            irTopoPagina();
        }
    }

    @Get("cliente/listagem")
    public void pesquisar(Cliente filtro, Integer paginaSelecionada) {
        final PaginacaoWrapper<Cliente> paginacao = this.clienteService.paginarCliente(filtro, true,
                this.calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());

        for (Cliente c : paginacao.getLista()) {
            formatarDocumento(c);
        }

        this.inicializarPaginacao(paginaSelecionada, paginacao, "listaCliente");
        addAtributo("cliente", filtro);
    }

    @Get("cliente/{idCliente}")
    public void pesquisarClienteById(Integer idCliente, boolean isRevendedor) {
        Cliente cliente = clienteService.pesquisarById(idCliente);
        if (cliente != null) {
            cliente.setDataNascimentoFormatada(StringUtils.formatarData(cliente.getDataNascimento()));
            carregarVendedor(cliente);

            try {
                popularPicklist(null, clienteService.pesquisarTransportadorasRedespacho(idCliente));
            } catch (ControllerException e) {
                gerarLogErroNavegacao("Cliente", e);
            }

            if (cliente.getDataUltimoContato() != null) {
                addAtributo("ultimoContato", formatarData(cliente.getDataUltimoContato()));
            }

            addAtributo("cliente", cliente);
            addAtributo("ramoAtividadeSelecionado", cliente.getRamoAtividade() != null ? cliente.getRamoAtividade()
                    .getId() : "");
            addAtributo("listaLogradouro", clienteService.pesquisarLogradouroCliente(idCliente));
            addAtributo("listaContato", clienteService.pesquisarContato(idCliente));
            addAtributo("comentarios", formatarComentarios(idCliente));
            addAtributo("tipoCliente", cliente.getTipoCliente());
        }

        if (isRevendedor) {
            redirecTo(this.getClass()).revendedorHome();
        } else {
            irTopoPagina();
        }
    }

    @Get("cliente/listagem/nome")
    public void pesquisarClienteByNomeFantasia(String nomeFantasia) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Cliente> listaCliente = this.clienteService.pesquisarByNomeFantasia(nomeFantasia);
        for (Cliente cliente : listaCliente) {
            lista.add(new Autocomplete(cliente.getId(), cliente.getNomeFantasia()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    @Get("cliente/serializacao/cnpj")
    public void pesquisarClienteSerializadoByCnpj(String cnpj) {
        Cliente c = clienteService.pesquisarClienteResumidoByCnpj(cnpj);
        LogradouroCliente l = c != null ? c.recuperarLogradouroFaturamento() : null;
        serializarJson(new SerializacaoJson("cliente", new ClienteJson(c, l), true));
    }

    @Get("cliente/serializacao/{idCliente}")
    public void pesquisarClienteSerializadoById(Integer idCliente) {
        Cliente c = clienteService.pesquisarClienteResumidoLogradouroById(idCliente);
        serializarJson(new SerializacaoJson("cliente", new ClienteJson(c, c.recuperarLogradouroFaturamento()), true));
    }

    @Get("cliente/transportadora")
    public void pesquisarTransportadoraByNomeFantasia(String nomeFantasia) {
        final List<Autocomplete> listaResultado = new ArrayList<Autocomplete>();
        final List<Transportadora> listaTransportadora = this.transportadoraService
                .pesquisarByNomeFantasia(nomeFantasia);
        for (Transportadora transportadora : listaTransportadora) {
            listaResultado.add(new Autocomplete(transportadora.getId(), transportadora.getNomeFantasia()));
        }
        serializarJson(new SerializacaoJson("listaResultado", listaResultado));
    }

    @Get("cliente/listagem/vendedor")
    public void pesquisarVendedorByNome(String nomeVendedor) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Usuario> listaUsuario = usuarioService.pesquisarVendedorByNome(nomeVendedor);
        for (Usuario usuario : listaUsuario) {
            lista.add(new Autocomplete(usuario.getId(), usuario.getNome()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    @Post("cliente/contato/remocao/{idContato}")
    public void removerContato(Integer idContato) {
        this.contatoService.remover(idContato, ContatoCliente.class);
        irTopoPagina();
    }

    @Post("cliente/logradouro/remocao/{idLogradouro}")
    public void removerLogradouro(Integer idLogradouro) {
        clienteService.removerLogradouro(idLogradouro);
        irTopoPagina();
    }

    @Get("revendedor")
    public void revendedorHome() {
        addAtributo("isRevendedor", true);
        irTopoPagina();
    }

}
