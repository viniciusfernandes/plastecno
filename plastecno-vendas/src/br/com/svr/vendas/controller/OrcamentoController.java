package br.com.svr.vendas.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.svr.service.ClienteService;
import br.com.svr.service.MaterialService;
import br.com.svr.service.NegociacaoService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.constante.FormaMaterial;
import br.com.svr.service.constante.TipoCST;
import br.com.svr.service.constante.TipoEntrega;
import br.com.svr.service.constante.TipoFinalidadePedido;
import br.com.svr.service.constante.TipoPedido;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.Contato;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.exception.NotificacaoException;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.relatorio.RelatorioService;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.json.ClienteJson;
import br.com.svr.vendas.json.SerializacaoJson;
import br.com.svr.vendas.json.VendedorJson;
import br.com.svr.vendas.login.UsuarioInfo;
import br.com.svr.vendas.relatorio.conversor.GeradorRelatorioPDF;

@Resource
public class OrcamentoController extends AbstractPedidoController {

    @Servico
    private ClienteService clienteService;

    private final String ID_ORCAMENTO = "idOrcamento";

    @Servico
    private MaterialService materialService;

    @Servico
    private NegociacaoService negociacaoService;

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

        setClienteService(clienteService);
        setPedidoService(pedidoService);
        setRelatorioService(relatorioService);
    }

    @Post("orcamento/aceite/listaitem")
    public void aceitarListaItemOrcamento(Integer idCliente, Integer idRepresentada, Integer idVendedor,
            TipoPedido tipoPedido, Integer[] listaIdItemSelecionado) {
        try {
            Integer idPed = pedidoService.aceitarListaItemOrcamento(idCliente, idRepresentada, idVendedor, tipoPedido,
                    listaIdItemSelecionado);
            TipoPedido tPed = representadaService.isRevendedor(idRepresentada) ? TipoPedido.REVENDA
                    : TipoPedido.REPRESENTACAO;

            // Devemos configurar o parametro orcamento = false para direcionar
            // usuario para a tela de vendas apos o aceite.
            redirecTo(PedidoController.class).pesquisarPedidoById(idPed, tPed, true);
        } catch (BusinessException e) {
            pesquisarOrcamentoByIdCliente(idCliente, idVendedor, idRepresentada, tipoPedido, 1, null,
                    listaIdItemSelecionado);
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
    }

    @Post("orcamento/aceite/{idOrcamento}")
    public void aceitarOrcamento(Integer idOrcamento) {
        try {
            Integer idPedido = pedidoService.aceitarOrcamentoENegociacaoByIdOrcamento(idOrcamento);
            // Devemos configurar o parametro orcamento = false para direcionar
            // usuario para a tela de vendas apos o aceite.
            redirecTo(PedidoController.class).pesquisarPedidoById(idPedido, TipoPedido.REVENDA, true);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            pesquisarOrcamentoById(idOrcamento);
        }
    }

    @Post("orcamento/temporario/id")
    public void adicionarIdOrcamentoTemporario(Integer idOrcamento) {
        addSessao(ID_ORCAMENTO, idOrcamento);
        serializarJson(new SerializacaoJson("ok", true));
    }

    @Post("orcamento/cancelamento/{idOrcamento}")
    public void cancelarOrcamento(Integer idOrcamento) {
        try {
            pedidoService.cancelarOrcamentoRemoverNegociacao(idOrcamento);
            gerarMensagemSucesso("Orçamento No. " + idOrcamento + " cancelado com sucesso");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e.getListaMensagem());
            redirecTo(this.getClass()).pesquisarOrcamentoById(idOrcamento);
        }
        irTopoPagina();
    }

    @Post("orcamento/copiaitem")
    public void copiarItemSelecionado(Integer idCliente, Integer idRepresentada, Integer idVendedor,
            TipoPedido tipoPedido, Integer[] listaIdItemSelecionado) {
        try {
            Pedido orc = pedidoService.gerarPedidoItemSelecionado(idVendedor == null ? getCodigoUsuario() : idVendedor,
                    false, true, listaIdItemSelecionado == null ? null : Arrays.asList(listaIdItemSelecionado));
            pesquisarOrcamentoById(orc.getId());
        } catch (BusinessException e) {
            pesquisarOrcamentoByIdCliente(idCliente, idVendedor, idRepresentada, tipoPedido, 1, null,
                    listaIdItemSelecionado);
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
    }

    @Post("orcamento/copia/{idOrcamento}")
    public void copiarOrcamento(Integer idOrcamento) {
        try {
            Integer idPedidoClone = pedidoService.copiarPedido(idOrcamento, true);
            pesquisarOrcamentoById(idPedidoClone);
            gerarMensagemSucesso("Orçamento No. " + idPedidoClone + " inserido e copiado a partir do orçamento No. "
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

    private void enviarOrcamento(Integer idOrcamento, List<UploadedFile> anexo) {
        try {
            final PedidoPDFWrapper wrapper = gerarPDF(idOrcamento, TipoPedido.REVENDA);
            final Pedido pedido = wrapper.getPedido();

            AnexoEmail pdfPedido = new AnexoEmail(wrapper.getArquivoPDF());
            AnexoEmail[] anexoEmail = null;
            if (anexo != null) {
                anexoEmail = new AnexoEmail[anexo.size()];
                for (int i = 0; i < anexoEmail.length; i++) {
                    anexoEmail[i] = new AnexoEmail(toByteArray(anexo.get(i).getFile()), anexo.get(i).getContentType(),
                            anexo.get(i).getFileName(), null);
                }

            }

            pedidoService.enviarPedido(idOrcamento, pdfPedido, anexoEmail);

            serializarJson(new SerializacaoJson("sucesso", new String[] {"Orçamento No. " + idOrcamento
                    + " foi enviado com sucesso para o cliente " + pedido.getCliente().getNomeFantasia()}));
        } catch (NotificacaoException e) {
            gerarLogErro("envio de email do orcamento No. " + idOrcamento, e);
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErro("envio de email do orcamento No. " + idOrcamento, e);
            serializarJson(new SerializacaoJson("erros", new String[] {"Falha no envio do orçamento No. " + idOrcamento
                    + ". Verifique o log do servidor para mais detalhes."}));

        }
    }

    @Post("orcamento/envio/anexo")
    public void enviarOrcamentoArquivoAnexo(List<UploadedFile> anexo) {
        Integer idOrcamento = (Integer) getSessao(ID_ORCAMENTO);
        removerSessao(ID_ORCAMENTO);
        enviarOrcamento(idOrcamento, anexo);
    }

    @Post("orcamento/item/inclusao")
    public void inserirItemOrcamento(Integer numeroPedido, ItemPedido itemPedido, Double aliquotaIPI) {
        forwardTo(PedidoController.class).inserirItemPedido(numeroPedido, itemPedido, aliquotaIPI);
    }

    // Aqui o nome do parametro eh "pedido" pois parte do javascript foi
    // reaproveitado da tela de pedidos e os parametros sao enviados com esse
    // nome.
    @Post("orcamento/inclusao")
    public void inserirOrcamento(Pedido pedido, Contato contato, Cliente cliente) {
        if (cliente != null) {
            removerMascaraDocumento(cliente);
        }

        // Configurando o tipo de venda que sera efetuado. Aqui ja estamos
        // supondo que a lista de representadas inicializada na tela nao contem
        // fornecedores.
        if (pedido.getTipoPedido() == null && pedido.getRepresentada() != null) {
            pedido.setTipoPedido(representadaService.isRevendedor(pedido.getRepresentada().getId()) ? TipoPedido.REVENDA
                    : TipoPedido.REPRESENTACAO);
        }

        pedido.setFinalidadePedido(TipoFinalidadePedido.INDUSTRIALIZACAO);
        pedido.setCliente(cliente);
        forwardTo(PedidoController.class).inserirPedido(pedido, contato, true);
    }

    @Get("orcamento")
    public void orcamentoHome() {
        addAtributo("orcamento", true);
        addAtributoCondicional("pedidoDesabilitado", false);
        addAtributo("listaRepresentada", representadaService.pesquisarRepresentadaAtiva());
        addAtributoCondicional("idRepresentadaSelecionada", representadaService.pesquisarIdRevendedor());
        addAtributo("listaFormaMaterial", FormaMaterial.values());
        addAtributo("listaCST", TipoCST.values());
        addAtributo("listaTipoEntrega", TipoEntrega.values());
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

    @Get("orcamento/contatocliente/{idContato}")
    public void pesquisarContatoByIdContato(Integer idContato) {
        super.pesquisarContatoByIdContato(idContato);
    }

    @Get("orcamento/contatocliente")
    public void pesquisarContatoClienteByIdCliente(Integer idCliente) {
        super.pesquisarContatoClienteByIdCliente(idCliente);
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
        if (pedido == null) {
            gerarListaMensagemAlerta("O orçamento No. " + idPedido + " não existe no sistema");
        } else if (pedido.isOrcamento()) {
            pedido.setRepresentada(pedidoService.pesquisarRepresentadaResumidaByIdPedido(idPedido));
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

        if (pedido != null && !pedido.isOrcamento()) {
            redirecTo(PedidoController.class).pesquisarPedidoById(idPedido, TipoPedido.REVENDA, false);
        } else {
            irTopoPagina();
        }
    }

    @Get("orcamento/listagem")
    public void pesquisarOrcamentoByIdCliente(Integer idCliente, Integer idVendedor, Integer idRepresentada,
            TipoPedido tipoPedido, Integer paginaSelecionada, ItemPedido itemVendido, Integer[] listaIdItemSelecionado) {

        super.pesquisarPedidoByIdCliente(idCliente, idVendedor, idRepresentada, tipoPedido, true, paginaSelecionada,
                itemVendido, listaIdItemSelecionado);

        irRodapePagina();
    }

    @Get("orcamento/transportadora/listagem")
    public void pesquisarTransportadoraByNomeFantasia(String nomeFantasia) {
        forwardTo(TransportadoraController.class).pesquisarTransportadoraByNomeFantasia(nomeFantasia);
    }

    @Get("orcamento/vendedor")
    public void pesquisarVendedor() {
        serializarJson(new SerializacaoJson("vendedor", new VendedorJson(getCodigoUsuario(), getNomeUsuario(),
                getEmailUsuario())));
    }

    @Post("orcamento/itempedido/remocao/{id}")
    public void removerItemOrcamento(Integer id) {
        super.removerItemPedido(id);
    }
}
