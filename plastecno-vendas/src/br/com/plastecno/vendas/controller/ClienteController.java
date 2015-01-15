package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ContatoService;
import br.com.plastecno.service.RamoAtividadeService;
import br.com.plastecno.service.TransportadoraService;
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
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public final class ClienteController extends AbstractController {

    @Servico
    private TransportadoraService transportadoraService;
    @Servico
    private RamoAtividadeService ramoAtividadeService;
    @Servico
    private ClienteService clienteService;
    @Servico
    private ContatoService contatoService;

    public ClienteController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);

        this.setNomeTela("Cliente");
        this.inicializarPicklist("Redespacho", "Cadastradas", "Redespacho", "id", "nomeFantasia", false,
                "Transportadora");
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

        this.liberarAcesso("acessoInclusaoPermitido", isInclusaoCliente || isVendedorIgual);
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
        List<ComentarioCliente> listaComentario = this.clienteService
                .pesquisarComentarioByIdCliente(idCliente);
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

    @Post("cliente/inclusao")
    public void inserir(Cliente cliente, List<LogradouroCliente> listaLogradouro, List<ContatoCliente> listaContato,
            List<Integer> listaIdTransportadoraAssociada) {
        try {
            if (temElementos(listaLogradouro)) {
                cliente.addLogradouro(listaLogradouro);
            }

            if (temElementos(listaContato)) {
                cliente.addContato(listaContato);
            }

            if (temElementos(listaIdTransportadoraAssociada)) {
                cliente.addRedespacho(this.transportadoraService.pesquisarById(listaIdTransportadoraAssociada));
            }

            if (cliente.getId() == null) {
                cliente.setVendedor(new Usuario(getCodigoUsuario()));
            }
            this.clienteService.inserir(cliente);

            this.gerarMensagemCadastroSucesso(cliente, "nomeFantasia");
        } catch (BusinessException e) {
            this.formatarDocumento(cliente);
            this.carregarVendedor(cliente);
            addAtributo("cliente", cliente);
            addAtributo("listaLogradouro", listaLogradouro);
            addAtributo("listaContato", listaContato);
            addAtributo("ramoAtividadeSelecionado", cliente.getRamoAtividade() != null ? cliente.getRamoAtividade()
                    .getId() : null);

            try {
                // Temos que manter as transportadoras escolhidas na tela em
                // caso excecao de negocios
                popularPicklist(null, cliente.getListaRedespacho());
                gerarListaMensagemErro(e);
            } catch (ControllerException e1) {
                gerarLogErroNavegacao("Cliente", e);
            }

        } catch (Exception e) {
            gerarLogErro("inclusao/alteracao de cliente", e);
        }

        irTopoPagina();
    }

    @Post("cliente/inclusao/comentario")
    public void inserirComentario(Integer idCliente, String comentario) {

        if (idCliente == null) {
            gerarListaMensagemErro("Para inserir um comentário é necessário escolher um cliente.");
            irTopoPagina();
        } else {
            try {
                clienteService.inserirComentario(idCliente, comentario);
                this.gerarMensagemSucesso("Comentário sonre o cliente No. " + idCliente + " inserido com sucesso.");
            } catch (BusinessException e) {
                gerarListaMensagemErro(e);
                addAtributo("comentario", comentario);
            }
            pesquisarClienteById(idCliente);
        }
    }

    @Get("cliente/listagem")
    public void pesquisar(Cliente filtro, Integer paginaSelecionada) {
        final PaginacaoWrapper<Cliente> paginacao = this.clienteService.paginarCliente(filtro, true,
                this.calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());

        for (Cliente cliente : paginacao.getLista()) {
            cliente.setCnpj(formatarCNPJ(cliente.getCnpj()));
            cliente.setCpf(formatarCPF(cliente.getCpf()));
        }

        this.inicializarPaginacao(paginaSelecionada, paginacao, "listaCliente");
        addAtributo("cliente", filtro);
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

    @Get("cliente/{idCliente}")
    public void pesquisarClienteById(Integer idCliente) {
        Cliente cliente = this.clienteService.pesquisarById(idCliente);
        this.carregarVendedor(cliente);

        try {
            popularPicklist(null, this.clienteService.pesquisarTransportadorasRedespacho(idCliente));
        } catch (ControllerException e) {
            gerarLogErroNavegacao("Cliente", e);
        }

        addAtributo("clienteAtivo", this.clienteService.isClienteAtivo(idCliente));

        if (cliente.getDataUltimoContato() != null) {
            addAtributo("ultimoContato", this.formatarData(cliente.getDataUltimoContato()));
        }

        this.formatarDocumento(cliente);
        addAtributo("cliente", cliente);
        addAtributo("ramoAtividadeSelecionado", cliente.getRamoAtividade() != null ? cliente.getRamoAtividade().getId()
                : "");
        addAtributo("listaLogradouro", this.clienteService.pesquisarLogradouro(idCliente));
        addAtributo("listaContato", this.clienteService.pesquisarContato(idCliente));
        addAtributo("comentarios", formatarComentarios(idCliente));
        irTopoPagina();
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

    @Post("cliente/contato/remocao/{idContato}")
    public void removerContato(Integer idContato) {
        this.contatoService.remover(idContato, ContatoCliente.class);
        irTopoPagina();
    }

    @Post("cliente/logradouro/remocao/{idLogradouro}")
    public void removerLogradouro(Integer idLogradouro) {
        this.clienteService.removerLogradouro(idLogradouro);
        irTopoPagina();
    }
}
