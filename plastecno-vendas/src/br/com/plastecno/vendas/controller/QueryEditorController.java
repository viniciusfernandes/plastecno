package br.com.plastecno.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.QueryNativaService;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.vendas.controller.anotacao.Servico;

@Resource
public final class QueryEditorController extends AbstractController {

    @Servico
    private QueryNativaService queryNativaService;

    public QueryEditorController(Result result) {
        super(result);
    }

    @Post("administracao/sql/execucao")
    public void executarQuery(String query) {
        addAtributo("query", query);

        try {
            addAtributo("resultado", this.queryNativaService.executar(query));
        } catch (BusinessException e) {
            this.gerarListaMensagemAlerta(e);
        }
        this.irPaginaHome();
    }

    @Get("administracao/sql")
    public void queryEditorHome() {
    }

}
