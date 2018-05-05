package br.com.svr.vendas.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.EstoqueService;
import br.com.svr.service.PagamentoService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.constante.FormaMaterial;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.constante.TipoPagamento;
import br.com.svr.service.constante.TipoPedido;
import br.com.svr.service.entity.Item;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.Pagamento;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.message.AlteracaoEstoquePublisher;
import br.com.svr.service.nfe.constante.TipoModalidadeFrete;
import br.com.svr.service.relatorio.RelatorioService;
import br.com.svr.service.validacao.InformacaoInvalidaException;
import br.com.svr.service.wrapper.GrupoWrapper;
import br.com.svr.service.wrapper.Periodo;
import br.com.svr.service.wrapper.RelatorioWrapper;
import br.com.svr.util.StringUtils;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class RecepcaoCompraController extends AbstractController {

    @Servico
    private AlteracaoEstoquePublisher alteracaoEstoquePublisher;

    @Servico
    private EstoqueService estoqueService;

    @Servico
    private PagamentoService pagamentoService;

    @Servico
    private PedidoService pedidoService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private RepresentadaService representadaService;

    public RecepcaoCompraController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        verificarPermissaoAcesso("acessoGeracaoPagamentoPermitida", TipoAcesso.CADASTRO_PEDIDO_COMPRA,
                TipoAcesso.ADMINISTRACAO, TipoAcesso.RECEPCAO_COMPRA, TipoAcesso.OPERACAO_CONTABIL);
    }

    @Post("compra/item/pagamento/{idItem}")
    public void gerarPagamentoItemPedido(Integer idItem, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        Pagamento p = pagamentoService.gerarPagamentoItemCompra(idItem);
        formatarPagamento(p);
        addAtributo("pagamento", p);
        addAtributo("listaModalidadeFrete", TipoModalidadeFrete.values());
        addAtributo("listaTipoPagamento", TipoPagamento.values());
        addAtributo("listaFornecedor", representadaService.pesquisarRepresentadaAtivoByTipoPedido(TipoPedido.COMPRA));
        try {
            gerarRelatorio(dataInicial, dataFinal, idRepresentada);
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemAlerta(e);

        }
        irTopoPagina();
    }

    private RelatorioWrapper<Integer, ItemPedido> gerarRelatorio(Date dataInicial, Date dataFinal,
            Integer idRepresentada) throws InformacaoInvalidaException {
        return gerarRelatorio(dataInicial, dataFinal, idRepresentada, null);
    }

    private RelatorioWrapper<Integer, ItemPedido> gerarRelatorio(Date dataInicial, Date dataFinal,
            Integer idRepresentada, List<Integer> listaNumeroPedido) throws InformacaoInvalidaException {
        Periodo periodo = Periodo.gerarPeriodo(dataInicial, dataFinal);
        RelatorioWrapper<Integer, ItemPedido> relatorio = relatorioService.gerarRelatorioCompraAguardandoRecepcao(
                idRepresentada, listaNumeroPedido, periodo);
        addAtributo("relatorio", relatorio);
        return relatorio;
    }

    @Get("compra/recepcao/inclusaodadosnf")
    public void inserirDadosNotaFiscal(Pedido pedido, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        pedidoService.inserirDadosNotaFiscal(pedido);
        pesquisarCompraAguardandoRecepcao(dataInicial, dataFinal, idRepresentada);
    }

    /*
     * AQUI ACHO QUE PODEMOS REALIZAR UMA CHAMADA DO
     * AlteracaoEstoqueListener.publicar PARA ENVIAR OS PEDIDOS PARA O
     * EMPACOTAMENTO AUTOMATICAMENTE.
     */
    @Post("compra/item/pagamento/inclusao")
    public void inserirPagamentoItemPedido(Pagamento pagamento, Date dataInicial, Date dataFinal,
            Integer idRepresentada, List<Integer> listaIdItemSelecionado) {
        try {
            pagamentoService.inserirPagamentoParceladoItemCompra(pagamento.getNumeroNF(), pagamento.getValorNF(),
                    pagamento.getDataVencimento(), pagamento.getDataEmissao(), pagamento.getModalidadeFrete(),
                    listaIdItemSelecionado);

            redirecTo(PagamentoController.class).gerarRelatorioPagamentoByNF(pagamento.getNumeroNF(), new Date(),
                    new Date());
            alteracaoEstoquePublisher.publicar();
        } catch (BusinessException e) {
            addAtributo("dataInicial", dataInicial);
            addAtributo("dataFinal", dataFinal);
            addAtributo("pagamento", pagamento);
            pesquisarCompraAguardandoRecepcao(dataInicial, dataFinal, idRepresentada);
            adicionarIdItemSelecionado(listaIdItemSelecionado);
            gerarListaMensagemErro(e);
        }
    }

    @Get("compra/recepcao/listagem")
    public void pesquisarCompraAguardandoRecepcao(Date dataInicial, Date dataFinal, Integer idRepresentada) {

        try {
            gerarRelatorio(dataInicial, dataFinal, idRepresentada);
            if (contemAtributo("permanecerTopo")) {
                irTopoPagina();
            } else {
                irRodapePagina();
            }
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
        addPeriodo(dataInicial, dataFinal);
        addAtributo("idRepresentadaSelecionada", idRepresentada);
    }

    @Post("compra/item/edicao")
    public void pesquisarItemCompraById(Integer idItemPedido, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        ItemPedido itemPedido = pedidoService.pesquisarItemPedidoById(idItemPedido);
        Pedido pedido = pedidoService.pesquisarDadosNotaFiscalByIdItemPedido(idItemPedido);

        if (itemPedido == null) {
            gerarListaMensagemErro("Item de compra não existe no sistema");
        } else {
            formatarAliquotaItemPedido(itemPedido);
            formatarPedido(pedido);
            addAtributo("pedido", pedido);
            addAtributo("itemPedido", itemPedido);
        }
        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("idRepresentadaSelecionada", idRepresentada);
        pesquisarCompraAguardandoRecepcao(dataInicial, dataFinal, idRepresentada);
        irTopoPagina();
    }

    @Get("compra/recepcao/listagem/pedido")
    public void pesquisarListaPedido(List<Integer> listaNumeroPedido, Date dataInicial, Date dataFinal,
            Integer idRepresentada) {
        try {
            RelatorioWrapper<Integer, ItemPedido> r = gerarRelatorio(dataInicial, dataFinal, idRepresentada,
                    listaNumeroPedido);

            // Adicionando IDs dos itens dos pedidos na lista de itens
            // selecionados para que eles sejam checkados na tela para facilitar
            // a escolha pelo usuario.
            List<Integer> listaIdItemSelecionado = new ArrayList<>();
            for (GrupoWrapper<Integer, ItemPedido> g : r.getListaGrupo()) {
                for (Item i : g.getListaElemento()) {
                    listaIdItemSelecionado.add(i.getId());
                }
            }
            adicionarIdItemSelecionado(listaIdItemSelecionado);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            irTopoPagina();
        }
        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("idRepresentadaSelecionada", idRepresentada);

        StringBuilder lNum = new StringBuilder();
        if (listaNumeroPedido != null && !listaNumeroPedido.isEmpty()) {
            for (Integer num : listaNumeroPedido) {
                lNum.append(num).append(" ");
            }
            addAtributo("listaNumeroPedido", lNum);
        }
    }

    @Get("compra/recepcao")
    public void recepcaoCompraHome() {
        addAtributo("alteracaoPedidoCompraHabilitada", true);
        addAtributo("listaRepresentada", representadaService.pesquisarFornecedorAtivo());
        addAtributo("listaFormaMaterial", FormaMaterial.values());
        addAtributo("listaModalidadeFrete", TipoModalidadeFrete.values());
        addAtributo("dataEmissaoFormatada", StringUtils.formatarData(new Date()));
        addAtributo("dataRecebimentoFormatada", StringUtils.formatarData(new Date()));
    }

    @Post("compra/item/recepcaoparcial")
    public void recepcaoParcialItemPedido(Integer idItemPedido, Integer quantidadeRecepcionada, Date dataInicial,
            Date dataFinal, Integer idRepresentada, String ncm) {
        String mensagem = null;
        try {
            estoqueService.adicionarQuantidadeRecepcionadaItemCompra(idItemPedido, quantidadeRecepcionada, ncm);
            boolean contemItem = pedidoService.contemQuantidadeNaoRecepcionadaItemPedido(idItemPedido);
            Integer idPedido = pedidoService.pesquisarIdPedidoByIdItemPedido(idItemPedido);

            if (contemItem) {
                mensagem = "O pedido No. "
                        + idPedido
                        + " teve item de compra foi recepcionado parcialmente e essas alterações já foram incluidas no estoque.";
            } else {
                mensagem = "O pedido No. \""
                        + idPedido
                        + "\" não contém outros itens para serem recepcionados e essas alterações já foram incluidas no estoque.";
            }
            gerarMensagemSucesso(mensagem);
        } catch (BusinessException e) {
            addAtributo("itemPedido", pedidoService.pesquisarItemPedidoById(idItemPedido));
            gerarListaMensagemErro(e);
        }

        alteracaoEstoquePublisher.publicar();

        addAtributo("permanecerTopo", true);
        pesquisarCompraAguardandoRecepcao(dataInicial, dataFinal, idRepresentada);
    }

    @Post("compra/item/recepcao")
    public void recepcionarItemCompra(Date dataInicial, Date dataFinal, Integer idRepresentada, Integer idItemPedido,
            String ncm) {
        Integer quantidadeNaoRecepcionada = pedidoService.pesquisarQuantidadeNaoRecepcionadaItemPedido(idItemPedido);
        recepcaoParcialItemPedido(idItemPedido, quantidadeNaoRecepcionada, dataInicial, dataFinal, idRepresentada, ncm);
    }

    @Post("compra/item/remocao")
    public void removerItemCompra(Date dataInicial, Date dataFinal, Integer idRepresentada, Integer idItemPedido) {
        try {
            pedidoService.removerItemPedido(idItemPedido);
        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
        }
        redirecTo(this.getClass()).pesquisarCompraAguardandoRecepcao(dataInicial, dataFinal, idRepresentada);
    }
}
