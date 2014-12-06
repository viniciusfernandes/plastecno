package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioVendaPeriodoController extends AbstractController {

    @Servico
    private RelatorioService relatorioService;

    public RelatorioVendaPeriodoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/venda/periodo/listagem")
    public void gerarRelatorioVendaPeriodo(Date dataInicial, Date dataFinal) {

        try {
            addAtributo("relatorio",
                    this.relatorioService.gerarRelatorioVendaPeriodo(new Periodo(dataInicial, dataFinal)));
        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
        }

        addAtributo("dataInicial", this.formatarData(dataInicial));
        addAtributo("dataFinal", this.formatarData(dataFinal));
        this.irPaginaHome();
    }

    @Get("relatorio/venda/periodo")
    public void relatorioVendaPeriodoHome() {
        addAtributo("relatorioGerado", contemAtributo("relatorio"));
    }
}
