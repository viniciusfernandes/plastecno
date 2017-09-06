package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.message.AlteracaoEstoquePublisher;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PagamentoService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoPagamento;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoModalidadeFrete;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

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
    }

    @Post("compra/item/pagamento/{idItem}")
    public void gerarPagamentoItemPedido(Integer idItem, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        Pagamento p = pagamentoService.gerarPagamentoItemPedido(idItem);
        formatarPagamento(p);
        addAtributo("pagamento", p);
        addAtributo("listaModalidadeFrete", TipoModalidadeFrete.values());
        addAtributo("listaTipoPagamento", TipoPagamento.values());
        addAtributo("listaFornecedor", representadaService.pesquisarRepresentadaAtivoByTipoPedido(TipoPedido.COMPRA));
        pesquisarCompraAguardandoRecepcao(dataInicial, dataFinal, idRepresentada);
    }

    @Get("compra/recepcao/inclusaodadosnf")
    public void inserirDadosNotaFiscal(Pedido pedido, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        pedidoService.inserirDadosNotaFiscal(pedido);
        pesquisarCompraAguardandoRecepcao(dataInicial, dataFinal, idRepresentada);
    }

    @Post("compra/item/pagamento/inclusao")
    public void inserirPagamentoItemPedido(Pagamento pagamento, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        try {
            pagamentoService.inserirPagamentoItemPedido(pagamento);
            gerarMensagemSucesso("Pagamento incluído com sucesso. Pedido No. " + pagamento.getIdPedido() + " item "
                    + pagamento.getDescricao());
        } catch (BusinessException e) {
            addAtributo("pagamento", pagamento);
            gerarListaMensagemErro(e);
        }
        pesquisarCompraAguardandoRecepcao(dataInicial, dataFinal, idRepresentada);
    }

    @Get("compra/recepcao/listagem")
    public void pesquisarCompraAguardandoRecepcao(Date dataInicial, Date dataFinal, Integer idRepresentada) {

        try {
            Periodo periodo = Periodo.gerarPeriodo(dataInicial, dataFinal);
            RelatorioWrapper<Integer, ItemPedido> relatorio = relatorioService
                    .gerarRelatorioCompraAguardandoRecepcao(idRepresentada, periodo);

            addAtributo("relatorio", relatorio);
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

    @Get("compra/recepcao")
    public void recepcaoCompraHome() {
        addAtributo("listaRepresentada", representadaService.pesquisarRepresentadaEFornecedor());
        addAtributo("listaFormaMaterial", FormaMaterial.values());
    }

    @Post("compra/item/recepcaoparcial")
    public void recepcaoParcialItemPedido(Integer idItemPedido, Integer quantidadeRecepcionada, Date dataInicial,
            Date dataFinal, Integer idRepresentada, String ncm) {
        String mensagem = null;
        try {
            estoqueService.recepcionarItemCompra(idItemPedido, quantidadeRecepcionada, ncm);
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
