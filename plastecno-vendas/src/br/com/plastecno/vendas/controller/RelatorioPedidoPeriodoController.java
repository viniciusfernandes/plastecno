package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioPedidoPeriodoController extends AbstractController {

    @Servico
    private RelatorioService relatorioService;

    public RelatorioPedidoPeriodoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/pedido/periodo/listagem")
    public void gerarRelatorioPedidoPeriodo(boolean isCompra, Date dataInicial, Date dataFinal) {

        try {
            if (isCompra) {
                addAtributo("relatorio",
                        relatorioService.gerarRelatorioCompraPeriodo(new Periodo(dataInicial, dataFinal)));
            } else {
                addAtributo("relatorio",
                        relatorioService.gerarRelatorioVendaPeriodo(new Periodo(dataInicial, dataFinal)));
            }
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }

        addAtributo("dataInicial", StringUtils.formatarData(dataInicial));
        addAtributo("dataFinal", StringUtils.formatarData(dataFinal));
        redirecTo(this.getClass()).relatorioPedidoPeriodoHome(isCompra);
    }

    @Get("relatorio/pedido/periodo")
    public void relatorioPedidoPeriodoHome(boolean isCompra) {
        addAtributo("isCompra", isCompra);
        configurarFiltroPediodoMensal();
    }
}
