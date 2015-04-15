package br.com.plastecno.vendas.controller;

import java.util.Date;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.vendas.controller.anotacao.Servico;

@Resource
public class RelatorioPedidoPeriodoController extends AbstractController {

    @Servico
    private RelatorioService relatorioService;

    public RelatorioPedidoPeriodoController(Result result) {
        super(result);
    }

    @Get("relatorio/pedido/listagem")
    public void pesquisarPedidoByPeriodo(Date dataInicial, Date dataFinal, boolean isCompra, boolean isEntrega) {
        try {
            List<Pedido> listaPedido = null;
            if (isEntrega) {
                listaPedido = relatorioService.gerarRelatorioEntrega(new Periodo(dataInicial, dataFinal));
                addAtributo(
                        "tituloRelatorio",
                        "Relátorio de Acompanhamento de Entregas de " + this.formatarData(dataInicial) + " à "
                                + this.formatarData(dataFinal));
            } else if (isCompra) {
                listaPedido = relatorioService.gerarRelatorioCompra(new Periodo(dataInicial, dataFinal));
                addAtributo("tituloRelatorio", "Relátorio de Pedido de Compras de " + this.formatarData(dataInicial)
                        + " à " + this.formatarData(dataFinal));

            } else {
                listaPedido = relatorioService.gerarRelatorioVenda(new Periodo(dataInicial, dataFinal));
                addAtributo("tituloRelatorio", "Relátorio de Pedido de Vendas de " + this.formatarData(dataInicial)
                        + " à " + this.formatarData(dataFinal));

            }

            for (Pedido pedido : listaPedido) {
                formatarPedido(pedido);
            }
            addAtributo("listaPedido", listaPedido);
            addAtributo("relatorioGerado", true);
        } catch (InformacaoInvalidaException e) {
            this.gerarListaMensagemErro(e);
        }

        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        redirecTo(this.getClass()).relatorioPedidoPeriodoHome(dataInicial, dataFinal, isCompra, isEntrega);
    }

    @Get("relatorio/pedido")
    public void relatorioPedidoPeriodoHome(Date dataInicial, Date dataFinal, boolean isCompra, boolean isEntrega) {
        if (isEntrega) {
            addAtributo("titulo", "Relatório de Entregas por Período");
            addAtributo(
                    "tituloRelatorio",
                    "Relátorio de Acompanhamento de Entregas de " + this.formatarData(dataInicial) + " à "
                            + this.formatarData(dataFinal));
        } else if (isCompra) {
            addAtributo("titulo", "Relatório de Compras por Período");
            addAtributo("tituloRelatorio", "Relátorio de Pedido de Compras de " + this.formatarData(dataInicial)
                    + " à " + this.formatarData(dataFinal));

        } else {
            addAtributo("titulo", "Relatório de Vendas por Período");
            addAtributo("tituloRelatorio", "Relátorio de Pedido de Vendas de " + this.formatarData(dataInicial) + " à "
                    + this.formatarData(dataFinal));

        }
        addAtributo("isCompra", isCompra);
        addAtributo("isEntrega", isEntrega);

        configurarFiltroPediodoMensal();
    }

    @Get("relatorio/pedido/pdf")
    public Download downloadPedidoPDF(Integer idPedido, TipoPedido tipoPedido) {
        return redirecTo(PedidoController.class).downloadPedidoPDF(idPedido, tipoPedido);
    }

}
