package br.com.plastecno.vendas.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoCST;
import br.com.plastecno.service.constante.TipoFinalidadePedido;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.relatorio.RelatorioService;
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
    private RelatorioService relatorioService;

    @Servico
    private RepresentadaService representadaService;

    @Servico
    private UsuarioService usuarioService;

    public OrcamentoController(Result result, UsuarioInfo usuarioInfo, GeradorRelatorioPDF geradorRelatorioPDF,
            HttpServletRequest request) {
        super(result, usuarioInfo, geradorRelatorioPDF, request);
        verificarPermissaoAcesso("acessoCadastroPedidoPermitido", TipoAcesso.CADASTRO_PEDIDO_VENDAS);

        super.setClienteService(clienteService);
        super.setPedidoService(pedidoService);
        super.setRelatorioService(relatorioService);
    }

    @Post("orcamento/aceite/{id}")
    public void aceitarOrcamento(Integer id) {
        try {
            Integer idPedido = pedidoService.aceitarOrcamento(id);
            // Devemos configurar o parametro orcamento = false para direcionar
            // o
            // usuario para a tela de vendas apos o aceite.
            redirecTo(PedidoController.class).pesquisarPedidoById(idPedido, TipoPedido.REVENDA, true);
        } catch (BusinessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Post("orcamento/cancelamento/{idOrcamento}")
    public void cancelarOrcamento(Integer idOrcamento) {
        try {
            pedidoService.cancelarOrcamento(idOrcamento);
            gerarMensagemSucesso("Or�amento No. " + idOrcamento + " cancelado com sucesso");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e.getListaMensagem());
            redirecTo(this.getClass()).pesquisarOrcamentoById(idOrcamento);
        }
        irTopoPagina();
    }

    @Post("orcamento/copia/{idOrcamento}")
    public void copiarOrcamento(Integer idOrcamento) {
        try {
            Integer idPedidoClone = pedidoService.copiarPedido(idOrcamento, true);
            pesquisarOrcamentoById(idPedidoClone);
            gerarMensagemSucesso("Or�amento No. " + idPedidoClone + " inserido e copiado a partir do or�amento No. "
                    + idOrcamento);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            pesquisarOrcamentoById(idOrcamento);
        } catch (Exception e) {
            gerarLogErro("copia do pedido de No. " + idOrcamento, e);
            pesquisarOrcamentoById(idOrcamento);
        }
    }

    @Get("orcamento/pdf")
    public Download downloadPDFOrcamento(Integer idPedido) {
        return super.downloadPDFPedido(idPedido, TipoPedido.REVENDA);
    }

    @Post("orcamento/envio/{id}")
    public void enviarOrcamento(Integer id) {
        try {
            final PedidoPDFWrapper wrapper = gerarPDF(id, TipoPedido.REVENDA);
            final Pedido pedido = wrapper.getPedido();

            pedidoService.enviarPedido(id, wrapper.getArquivoPDF());

            final String mensagem = "Or�amento No. " + id + " foi enviado com sucesso para o cliente "
                    + pedido.getCliente().getNomeFantasia();

            gerarMensagemSucesso(mensagem);
        } catch (NotificacaoException e) {
            gerarLogErro("envio de email do orcamento No. " + id, e);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            // populando a tela de pedidos
            redirecTo(this.getClass()).pesquisarOrcamentoById(id);
        } catch (Exception e) {
            gerarLogErro("envio de email do orcamento No. " + id, e);
        }
        irTopoPagina();
    }

    @Post("orcamento/item/inclusao")
    public void inserirItemPedido(Integer numeroPedido, ItemPedido itemPedido, Double aliquotaIPI) {
        forwardTo(PedidoController.class).inserirItemPedido(numeroPedido, itemPedido, aliquotaIPI);
    }

    @Post("orcamento/inclusao")
    public void inserirOrcamento(Pedido pedido, Contato contato, Cliente cliente) {
        if (cliente != null) {
            cliente.addContato(contato != null ? new ContatoCliente(contato) : null);
            cliente.setRazaoSocial(cliente.getNomeFantasia());
            removerMascaraDocumento(cliente);
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
        addAtributo("listaCST", TipoCST.values());
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

    @Get("orcamento/item/{id}")
    public void pesquisarItemOrcamentoById(Integer id) {
        forwardTo(PedidoController.class).pesquisarItemPedidoById(id);
    }

    @Get("orcamento/material")
    public void pesquisarMaterial(String sigla, Integer idRepresentada) {
        forwardTo(PedidoController.class).pesquisarMaterial(sigla, idRepresentada);
    }

    @Get("orcamento/{idPedido}")
    public void pesquisarOrcamentoById(Integer idPedido) {
        Pedido pedido = pedidoService.pesquisarPedidoById(idPedido);
        if (pedido == null || !pedido.isOrcamento()) {
            gerarListaMensagemAlerta("O or�amento No. " + idPedido + " n�o existe no sistema");
        } else {
            pedido.setRepresentada(pedidoService.pesquisarRepresentadaByIdPedido(idPedido));
            pedido.setTransportadora(pedidoService.pesquisarTransportadoraResumidaByIdPedido(idPedido));

            List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
            formatarItemPedido(listaItem);
            formatarPedido(pedido);

            addAtributo("pedidoDesabilitado", isPedidoDesabilitado(pedido));
            addAtributo("pedido", pedido);
            addAtributo("contato", pedido.getContato());
            addAtributo("cliente", pedido.getCliente());
            addAtributoCondicional("idRepresentadaSelecionada",
                    pedidoService.pesquisarIdRepresentadaByIdPedido(idPedido));
            addAtributo("listaItemPedido", listaItem);
        }
        irTopoPagina();
    }

    @Get("orcamento/listagem")
    public void pesquisarOrcamentoByIdCliente(Integer idCliente, Integer idVendedor, Integer idRepresentada,
            TipoPedido tipoPedido, Integer paginaSelecionada, ItemPedido itemVendido) {

        super.pesquisarPedidoByIdCliente(idCliente, idVendedor, idRepresentada, tipoPedido, true, paginaSelecionada,
                itemVendido);

        irRodapePagina();
    }

    @Get("orcamento/transportadora/listagem")
    public void pesquisarTransportadoraByNomeFantasia(String nomeFantasia) {
        forwardTo(TransportadoraController.class).pesquisarTransportadoraByNomeFantasia(nomeFantasia);
    }
}
