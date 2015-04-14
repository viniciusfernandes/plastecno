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
public class RelatorioFaturamentoController extends AbstractController {
    @Servico
    private RelatorioService relatorioService;

    @Servico
    private UsuarioService usuarioService;

    public RelatorioFaturamentoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/faturamento/listagem")
    public void gerarRelatorioComissaoVendedor(Date dataInicial, Date dataFinal) {
        try {
            addAtributo("faturamento", relatorioService.gerarFaturamento(new Periodo(dataInicial, dataFinal)));
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemErro(e);
        }
        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        irPaginaHome();
    }

    @Get("relatorio/faturamento")
    public void relatorioFaturamentoHome() {
        configurarFiltroPediodoMensal();
    }
}
