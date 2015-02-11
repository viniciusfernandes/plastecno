package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class EstoqueController extends AbstractController {
    @Servico
    private EstoqueService estoqueService;
    @Servico
    private MaterialService materialService;

    public EstoqueController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("estoque")
    public void estoqueHome() {
        addAtributo("listaFormaMaterial", FormaMaterial.values());
        addAtributo("isEstoque", true);
    }

    @Get("estoque/item/listagem")
    public void pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
        List<ItemEstoque> lista = estoqueService.pesquisarItemEstoque(idMaterial, formaMaterial);
        formatarItemEstoque(lista);
        Material material = materialService.pesquisarById(idMaterial);

        addAtributo("formaSelecionada", formaMaterial);
        addAtributo("listaItemEstoque", lista);
        addAtributo("idMaterial", idMaterial);
        addAtributo("descricaoMaterial", material != null ? material.getDescricaoFormatada() : "");

        redirecTo(this.getClass()).estoqueHome();
    }

    @Get("estoque/item/{idItemEstoque}")
    public void pesquisarItemEstoqueById(Integer idItemEstoque, Integer idMaterial, FormaMaterial formaMaterial) {
        ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
        if (itemEstoque == null) {
            gerarListaMensagemErro("Item de estoque não existe no sistema");
        } else {
            addAtributo("itemPedido", itemEstoque);
        }
        redirecTo(this.getClass()).pesquisarItemEstoque(idMaterial, formaMaterial);
    }

    @Get("estoque/material/listagem")
    public void pesquisarMaterialEstoque(String sigla) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        if (sigla != null) {
            List<Material> listaMaterial = materialService.pesquisarBySigla(sigla);
            for (Material material : listaMaterial) {
                lista.add(new Autocomplete(material.getId(), material.getDescricaoFormatada()));
            }
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }
}
