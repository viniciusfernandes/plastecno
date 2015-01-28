package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioCompraPendenteController extends AbstractController {

    @Servico
    private RepresentadaService representadaService;
    @Servico
    private RelatorioService relatorioService;

    public RelatorioCompraPendenteController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("pedido/compra/recepcao")
    public void relatorioCompraPendenteHome(Date dataInicial, Date dataFinal, Integer idRepresentada) {

        addAtributo("relatorio", relatorioService.gerarRelatorioCompraPendente(dataInicial, dataFinal, idRepresentada));
        addAtributo("listaRepresentada", representadaService.pesquisar());
    }
}
