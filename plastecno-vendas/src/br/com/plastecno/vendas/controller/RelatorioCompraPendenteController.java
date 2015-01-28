package br.com.plastecno.vendas.controller;

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
    public void relatorioCompraPendenteHome() {

        addAtributo("relatorio", relatorioService.gerarRelatorioCompraPendente());
        addAtributo("listaRepresentada", representadaService.pesquisar());
    }
}
