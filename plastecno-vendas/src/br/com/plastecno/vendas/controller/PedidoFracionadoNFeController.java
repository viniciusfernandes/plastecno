package br.com.plastecno.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class PedidoFracionadoNFeController extends AbstractController {

    @Servico
    private RelatorioService relatorioService;

    public PedidoFracionadoNFeController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("pedidoFracionadoNFe")
    public void pedidoFracionadoNFeHome() {
        addAtributo("relatorio", relatorioService.gerarRelatorioPedidoFracionado());
    }
}
