package br.com.svr.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class NegociacaoController extends AbstractController {

    public NegociacaoController(Result result) {
        super(result);
    }

    @Get("negociacao")
    public void negociacaoHome() {
    }
}
