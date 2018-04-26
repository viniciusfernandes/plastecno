package br.com.svr.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.vendas.controller.anotacao.Login;

@Resource
public class ErroController extends AbstractController {

    public ErroController(Result result) {
        super(result);
    }

    @Login
    @Get("erro")
    public void erroHome() {
    }
}
