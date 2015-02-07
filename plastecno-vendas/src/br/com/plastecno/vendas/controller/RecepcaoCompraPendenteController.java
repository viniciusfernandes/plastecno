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
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RecepcaoCompraPendenteController extends AbstractController {

    @Servico
    private RepresentadaService representadaService;
    @Servico
    private RelatorioService relatorioService;
    @Servico
    private PedidoService pedidoService;
    @Servico
    private EstoqueService estoqueService;

    public RecepcaoCompraPendenteController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("compra/pdf")
    public Download downloadPedidoPDF(Integer idPedido) {
        return redirecTo(PedidoController.class).downloadPedidoPDF(idPedido, TipoPedido.COMPRA);
    }

    @Get("compra/recepcao/listagem")
    public void pesquisarCompraPendente(Date dataInicial, Date dataFinal, Integer idRepresentada) {

        try {
            Periodo periodo = new Periodo(dataInicial, dataFinal);
            RelatorioWrapper<Integer, ItemPedido> relatorio = relatorioService.gerarRelatorioCompraPendente(idRepresentada, periodo);

            addAtributo("relatorio", relatorio);
            irRodapePagina();
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }

        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("idRepresentadaSelecionada", idRepresentada);
        addAtributo("listaRepresentada", representadaService.pesquisar());
    }

    @Get("compra/edicao")
    public void pesquisarPedidoById(Integer idPedido) {
        redirecTo(PedidoController.class).pesquisarPedidoById(idPedido, TipoPedido.COMPRA);
    }

    @Get("compra/recepcao")
    public void recepcaoCompraPendenteHome() {
        addAtributo("listaRepresentada", representadaService.pesquisar());
    }

    @Post("compra/item/recepcao")
    public void recepcionarItemCompra(Date dataInicial, Date dataFinal, Integer idRepresentada, Integer idItemCompra) {
        estoqueService.inserirItemEstoque(idItemCompra);
        redirecTo(this.getClass()).pesquisarCompraPendente(dataInicial, dataFinal, idRepresentada);
    }

    @Post("compra/item/remocao")
    public void removerItemCompra(Date dataInicial, Date dataFinal, Integer idRepresentada, Integer idItemCompra) {
        try {
            pedidoService.removerItemPedido(idItemCompra);
        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
        }
        redirecTo(this.getClass()).pesquisarCompraPendente(dataInicial, dataFinal, idRepresentada);
    }
}
