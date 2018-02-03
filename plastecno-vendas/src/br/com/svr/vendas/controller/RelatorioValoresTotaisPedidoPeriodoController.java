package br.com.svr.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.relatorio.RelatorioService;
import br.com.svr.service.wrapper.Periodo;
import br.com.svr.util.StringUtils;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class RelatorioValoresTotaisPedidoPeriodoController extends AbstractController {

    @Servico
    private RelatorioService relatorioService;

    public RelatorioValoresTotaisPedidoPeriodoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/pedido/periodo/listagem")
    public void gerarRelatorioPedidoPeriodo(boolean isCompra, Date dataInicial, Date dataFinal) {

        try {
            if (isCompra) {
                addAtributo("relatorio",
                        relatorioService.gerarRelatorioValorTotalPedidoCompraPeriodo(new Periodo(dataInicial, dataFinal)));
            } else {
                addAtributo("relatorio",
                        relatorioService.gerarRelatorioValorTotalPedidoVendaPeriodo(new Periodo(dataInicial, dataFinal)));
            }
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }

        addAtributo("dataInicial", StringUtils.formatarData(dataInicial));
        addAtributo("dataFinal", StringUtils.formatarData(dataFinal));
        redirecTo(this.getClass()).relatorioValoresTotaisPedidoPeriodoHome(isCompra);
    }

    @Get("relatorio/pedido/periodo")
    public void relatorioValoresTotaisPedidoPeriodoHome(boolean isCompra) {
        addAtributo("isCompra", isCompra);
        configurarFiltroPediodoMensal();
    }
}
