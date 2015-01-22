package br.com.plastecno.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RecepcaoCompraController extends AbstractController {

    @Servico
    private RepresentadaService representadaService;

    public RecepcaoCompraController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("pedido/compra/recepcao")
    public void recepcaoCompraHome() {
        addAtributo("listaRepresentada", representadaService.pesquisar());
    }
}
