package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.util.NumeroUtils;
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
        verificarPermissaoAcesso("acessoManutencaoEstoquePermitido", TipoAcesso.ADMINISTRACAO,
                TipoAcesso.MANUTENCAO_ESTOQUE);
        verificarPermissaoAcesso("acessoValorEstoquePermitido", TipoAcesso.ADMINISTRACAO);
    }

    @Post("estoque/valor")
    public void pesquisarValorEstoque(Material material, FormaMaterial formaMaterial) {
        Double valorEstoque = estoqueService.pesquisarValorEstoque(material.getId(), formaMaterial);

        addAtributo("valorEstoque", NumeroUtils.formatarValorMonetario(valorEstoque));
        addAtributo("formaSelecionada", formaMaterial);
        addAtributo("material", material);
        irTopoPagina();
    }

    @Post("estoque/item/edicao")
    public void redefinirItemEstoque(Integer idItem, Integer quantidade, Double preco, Double aliquotaIPI,
            Double aliquotaICMS, Material material, FormaMaterial formaMaterial) {
        try {
            ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItem);
            if (itemEstoque == null) {
                gerarListaMensagemErro("O item de estoque não foi encontrado.");
            } else {
                itemEstoque.setAliquotaIPI(NumeroUtils.gerarAliquota(aliquotaIPI));
                itemEstoque.setAliquotaICMS(NumeroUtils.gerarAliquota(aliquotaICMS));
                itemEstoque.setQuantidade(quantidade);
                itemEstoque.setPrecoMedio(preco);
                estoqueService.redefinirItemEstoque(itemEstoque);
                gerarMensagemSucesso("Item de estoque inserido/alterado com sucesso.");
            }
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);

        }
        addAtributo("permanecerTopo", true);
        redirecTo(this.getClass()).pesquisarItemEstoque(material, formaMaterial);
    }

    @Post("estoque/item/inclusao")
    public void inserirItemEstoque(ItemEstoque itemPedido, Material material, FormaMaterial formaMaterial) {
        try {
            itemPedido.setAliquotaIPI(NumeroUtils.gerarAliquota(itemPedido.getAliquotaIPI()));
            itemPedido.setAliquotaICMS(NumeroUtils.gerarAliquota(itemPedido.getAliquotaICMS()));
            estoqueService.inserirItemEstoque(itemPedido);
            gerarMensagemSucesso("Item de estoque inserido/alterado com sucesso.");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("itemPedido", itemPedido);
        }
        addAtributo("permanecerTopo", true);
        redirecTo(this.getClass()).pesquisarItemEstoque(material, formaMaterial);
    }

    @Get("estoque/item/listagem")
    public void pesquisarItemEstoque(Material material, FormaMaterial formaMaterial) {
        if ((material == null || material.getId() == null) && formaMaterial == null) {
            gerarListaMensagemErro("Escolha o material e/ou forma de material. Não é possível pesquisar o estoque inteiro.");
            addAtributo("permanecerTopo", true);
        } else {
            final Integer idMaterial = material != null ? material.getId() : null;
            List<ItemEstoque> lista = estoqueService.pesquisarItemEstoque(idMaterial, formaMaterial);
            formatarItemEstoque(lista);

            addAtributo("formaSelecionada", formaMaterial);
            addAtributo("listaItemEstoque", lista);
            addAtributo("material", material);
        }

        if (contemAtributo("permanecerTopo")) {
            irTopoPagina();
        } else {
            irRodapePagina();
        }
        estoqueHome();
    }

    @Get("estoque/item/{idItemEstoque}")
    public void pesquisarItemEstoqueById(Integer idItemEstoque, Material material, FormaMaterial formaMaterial) {
        ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
        if (itemEstoque == null) {
            gerarListaMensagemErro("Item de estoque não existe no sistema");
        } else {
            formatarAliquotaItemEstoque(itemEstoque);
            addAtributo("itemPedido", itemEstoque);
        }
        addAtributo("permanecerTopo", true);
        material = materialService.pesquisarById(material.getId());
        redirecTo(this.getClass()).pesquisarItemEstoque(material, formaMaterial);
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
