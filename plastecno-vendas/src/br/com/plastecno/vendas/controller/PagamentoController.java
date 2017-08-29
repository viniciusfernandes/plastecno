package br.com.plastecno.vendas.controller;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class PagamentoController extends AbstractController {

    public PagamentoController(Result result, HttpServletRequest request) {
        super(result, request);
    }

    @Get("pagamento")
    public void pagamentoHome() {
    }
    

}
