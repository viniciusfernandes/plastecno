package br.com.svr.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.relatorio.RelatorioService;
import br.com.svr.service.validacao.exception.InformacaoInvalidaException;
import br.com.svr.service.wrapper.Periodo;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class RelatorioReceitaEstimadaController extends AbstractController {
    @Servico
    private RelatorioService relatorioService;

    @Servico
    private UsuarioService usuarioService;

    public RelatorioReceitaEstimadaController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/receita/listagem")
    public void gerarRelatorioComissaoVendedor(Date dataInicial, Date dataFinal) {
        try {
            addAtributo("receita", relatorioService.gerarReceitaEstimada(new Periodo(dataInicial, dataFinal)));
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemErro(e);
        }
        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        irPaginaHome();
    }

    @Get("relatorio/receita")
    public void relatorioReceitaEstimadaHome() {
        configurarFiltroPediodoMensal();
    }
}
