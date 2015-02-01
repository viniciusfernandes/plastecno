package br.com.plastecno.vendas.controller;

import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class EstoqueController extends AbstractController {
    @Servico
    private EstoqueService estoqueService;

    public EstoqueController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("estoque")
    public void estoqueHome() {
        addAtributo("listaFormaMaterial", FormaMaterial.values());
        List<ItemEstoque> lista = estoqueService.pesquisarItemEstoque(1, FormaMaterial.BR);
        formatarItemEstoque(lista);
        addAtributo("listaItemEstoque", lista);
    }

    @Get("estoque/item/listagem")
    public void pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
        addAtributo("listaItemEstoque", estoqueService.pesquisarItemEstoque(idMaterial, formaMaterial));
    }
}
