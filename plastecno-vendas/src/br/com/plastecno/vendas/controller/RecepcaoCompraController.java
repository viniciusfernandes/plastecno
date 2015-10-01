package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.message.AlteracaoEstoquePublisher;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.exception.BusinessException;
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
    private PedidoService pedidoService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private RepresentadaService representadaService;

    public RecepcaoCompraController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("compra/pdf")
    public Download downloadPedidoPDF(Integer idPedido) {
        return redirecTo(PedidoController.class).downloadPedidoPDF(idPedido, TipoPedido.COMPRA);
    }

    @Get("compra/recepcao/listagem")
    public void pesquisarCompraAguardandoRecebimento(Date dataInicial, Date dataFinal, Integer idRepresentada) {

        try {
            Periodo periodo = Periodo.gerarPeriodo(dataInicial, dataFinal);
            RelatorioWrapper<Integer, ItemPedido> relatorio = relatorioService
                    .gerarRelatorioCompraAguardandoRecebimento(idRepresentada, periodo);

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

        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("idRepresentadaSelecionada", idRepresentada);
        addAtributo("listaRepresentada", representadaService.pesquisarRepresentadaEFornecedor());
    }

    @Post("compra/item/edicao")
    public void pesquisarItemCompraById(Integer idItemPedido, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        ItemPedido itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);
        if (itemPedido == null) {
            gerarListaMensagemErro("Item de compra não existe no sistema");
        } else {
            formatarAliquotaItemPedido(itemPedido);
            addAtributo("itemPedido", itemPedido);
        }
        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("idRepresentadaSelecionada", idRepresentada);
        redirecTo(this.getClass()).pesquisarCompraAguardandoRecebimento(dataInicial, dataFinal, idRepresentada);
    }

    @Get("compra/recepcao")
    public void recepcaoCompraHome() {
        addAtributo("listaRepresentada", representadaService.pesquisarFornecedorAtivo());
        addAtributo("listaFormaMaterial", FormaMaterial.values());
    }

    @Post("compra/item/recepcaoparcial")
    public void recepcaoParcialItemPedido(Integer idItemPedido, Integer quantidadeRecepcionada, Date dataInicial,
            Date dataFinal, Integer idRepresentada) {
        String mensagem = null;
        try {
            estoqueService.recepcionarParcialmenteItemCompra(idItemPedido, quantidadeRecepcionada);
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
            addAtributo("itemPedido", pedidoService.pesquisarItemPedido(idItemPedido));
            gerarListaMensagemErro(e);
        }

        alteracaoEstoquePublisher.publicar();

        addAtributo("permanecerTopo", true);
        redirecTo(this.getClass()).pesquisarCompraAguardandoRecebimento(dataInicial, dataFinal, idRepresentada);
    }

    @Post("compra/item/recepcao")
    public void recepcionarItemCompra(Date dataInicial, Date dataFinal, Integer idRepresentada, Integer idItemPedido) {
        Integer quantidadeNaoRecepcionada = pedidoService.pesquisarQuantidadeNaoRecepcionadaItemPedido(idItemPedido);
        recepcaoParcialItemPedido(idItemPedido, quantidadeNaoRecepcionada, dataInicial, dataFinal, idRepresentada);
    }

    @Post("compra/item/remocao")
    public void removerItemCompra(Date dataInicial, Date dataFinal, Integer idRepresentada, Integer idItemPedido) {
        try {
            pedidoService.removerItemPedido(idItemPedido);
        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
        }
        redirecTo(this.getClass()).pesquisarCompraAguardandoRecebimento(dataInicial, dataFinal, idRepresentada);
    }
}
