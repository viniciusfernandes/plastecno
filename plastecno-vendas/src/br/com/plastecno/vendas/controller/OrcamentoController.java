package br.com.plastecno.vendas.controller;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoFinalidadePedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.ClienteJson;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;
import br.com.plastecno.vendas.relatorio.conversor.GeradorRelatorioPDF;

@Resource
public class OrcamentoController extends AbstractPedidoController {

    @Servico
    private ClienteService clienteService;

    @Servico
    private MaterialService materialService;

    @Servico
    private PedidoService pedidoService;

    @Servico
    private RepresentadaService representadaService;

    @Servico
    private UsuarioService usuarioService;

    public OrcamentoController(Result result, UsuarioInfo usuarioInfo, GeradorRelatorioPDF geradorRelatorioPDF,
            HttpServletRequest request) {
        super(result, usuarioInfo, geradorRelatorioPDF, request);
        verificarPermissaoAcesso("acessoCadastroPedidoPermitido", TipoAcesso.CADASTRO_PEDIDO_VENDAS);

    }

    @Post("orcamento/item/inclusao")
    public void inserirItemPedido(Integer numeroPedido, ItemPedido itemPedido, Double aliquotaIPI) {
        forwardTo(PedidoController.class).inserirItemPedido(numeroPedido, itemPedido, aliquotaIPI);
    }

    @Post("orcamento/inclusao")
    public void inserirOrcamento(Pedido pedido, Contato contato, Cliente cliente) {
        if (cliente != null && contato != null) {
            cliente.addContato(new ContatoCliente(contato));
        }
        pedido.setFinalidadePedido(TipoFinalidadePedido.OUTRA_ENTRADA);
        pedido.setCliente(cliente);
        forwardTo(PedidoController.class).inserirPedido(pedido, contato, true);
    }

    @Get("orcamento")
    public void orcamentoHome() {
        addAtributo("orcamento", true);
        addAtributoCondicional("pedidoDesabilitado", false);
        addAtributo("listaRepresentada", representadaService.pesquisarRepresentadaAtiva());
        addAtributo("vendedorNome", getUsuario().getNome());
        addAtributo("vendedorEmail", getUsuario().getEmail());
        addAtributoCondicional("idRepresentadaSelecionada", representadaService.pesquisarIdRevendedor());
        addAtributo("listaFormaMaterial", FormaMaterial.values());
    }

    /*
     * Metodo disparado quando o usuario selecionar um cliente do autocomplete
     */
    @Get("orcamento/cliente/{id}")
    public void pesquisarClienteById(Integer id) {
        Cliente cliente = clienteService.pesquisarClienteEContatoById(id);
        carregarVendedor(cliente);
        formatarDocumento(cliente);
        ClienteJson json = new ClienteJson(cliente);
        if (cliente != null && cliente.contemContato()) {
            json.setDDDTelefone(cliente.getContatoPrincipal().getDdd(), cliente.getContatoPrincipal().getTelefone());
        }
        SerializacaoJson serializacaoJson = new SerializacaoJson("cliente", json).incluirAtributo("vendedor");
        serializarJson(serializacaoJson);
    }

    @Get("orcamento/cliente")
    public void pesquisarClienteByNomeFantasia(String nomeFantasia) {
        forwardTo(PedidoController.class).pesquisarClienteByNomeFantasia(nomeFantasia);
    }

    @Get("orcamento/material")
    public void pesquisarMaterial(String sigla, Integer idRepresentada) {
        forwardTo(PedidoController.class).pesquisarMaterial(sigla, idRepresentada);
    }

    @Get("orcamento/{idPedido}")
    public void pesquisarOrcamentoById(Integer idPedido) {
        Pedido orcamento = pedidoService.pesquisarPedidoById(idPedido);
        addAtributo("pedidoDesabilitado", isPedidoDesabilitado(orcamento));
        addAtributo("pedido", orcamento);
        addAtributo("contato", orcamento.getContato());
        addAtributo("cliente", orcamento.getCliente());
        addAtributoCondicional("idRepresentadaSelecionada", pedidoService.pesquisarIdRepresentadaByIdPedido(idPedido));
        addAtributo("listaItemPedido", pedidoService.pesquisarItemPedidoByIdPedido(idPedido));
        irTopoPagina();
    }
}
