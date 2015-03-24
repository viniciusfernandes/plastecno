package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
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

    @Post("compra/item/inclusao")
    public void inserirItemPedido(Integer idItemPedido, Integer quantidadeRecepcionada, Date dataInicial,
            Date dataFinal, Integer idRepresentada) {
        try {
            pedidoService.alterarQuantidadeRecepcionada(idItemPedido, quantidadeRecepcionada);
            gerarMensagemSucesso("O item de compra foi alterado com sucesso. Essas altera��es j� podem ser incluidas no estoque.");
        } catch (BusinessException e) {
            addAtributo("itemPedido", pedidoService.pesquisarItemPedido(idItemPedido));
            gerarListaMensagemErro(e);
        }
        addAtributo("permanecerTopo", true);
        redirecTo(this.getClass()).pesquisarCompraAguardandoRecebimento(dataInicial, dataFinal, idRepresentada);
    }

    @Get("compra/recepcao/listagem")
    public void pesquisarCompraAguardandoRecebimento(Date dataInicial, Date dataFinal, Integer idRepresentada) {

        try {
            Periodo periodo = new Periodo(dataInicial, dataFinal);
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
        addAtributo("listaRepresentada", representadaService.pesquisarRepresentada());
    }

    @Post("compra/item/edicao")
    public void pesquisarItemCompraById(Integer idItemPedido, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        ItemPedido itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);
        if (itemPedido == null) {
            gerarListaMensagemErro("Item de compra n�o existe no sistema");
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
        // Pode ser que essas datas ja tenham sido preenchidas em outra
        // navegacao pois esse metodo eh reaproveitado.
        configurarFiltroPediodoMensal();
        addAtributo("listaRepresentada", representadaService.pesquisarRepresentada());
        addAtributo("listaFormaMaterial", FormaMaterial.values());
    }

    @Post("compra/item/recepcao")
    public void recepcionarItemCompra(Date dataInicial, Date dataFinal, Integer idRepresentada, Integer idItemPedido) {
        try {
            estoqueService.inserirItemPedido(idItemPedido);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        redirecTo(this.getClass()).pesquisarCompraAguardandoRecebimento(dataInicial, dataFinal, idRepresentada);
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
