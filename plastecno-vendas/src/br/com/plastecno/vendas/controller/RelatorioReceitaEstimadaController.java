package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

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
