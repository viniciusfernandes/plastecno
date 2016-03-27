package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.message.AlteracaoEstoquePublisher;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class EstoqueController extends AbstractController {

    @Servico
    private AlteracaoEstoquePublisher alteracaoEstoquePublisher;

    @Servico
    private EstoqueService estoqueService;

    @Servico
    private MaterialService materialService;

    public EstoqueController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("estoque/item/precominimo")
    public void calcularPrecoMinimoItemEstoque(ItemEstoque itemEstoque) {
        try {
            Double precoMinimo = estoqueService.calcularPrecoMinimoItemEstoque(itemEstoque);
            String precoMinimoFormatado = precoMinimo == null ? "" : NumeroUtils.formatarValorMonetario(precoMinimo);
            serializarJson(new SerializacaoJson("precoMinimo", precoMinimoFormatado));
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("cálculo do preço minimo do item do pedido", e);
        }
    }

    @Post("estoque/valor")
    public void calcularValorEstoque(Material material, FormaMaterial formaMaterial) {
        Double valorEstoque = estoqueService.calcularValorEstoque(material.getId(), formaMaterial);

        addAtributo("valorEstoque", NumeroUtils.formatarValorMonetario(valorEstoque));
        addAtributo("formaSelecionada", formaMaterial);
        addAtributo("material", materialService.pesquisarById(material.getId()));
        irTopoPagina();
    }

    @Get("estoque")
    public void estoqueHome() {
        addAtributo("listaFormaMaterial", FormaMaterial.values());
        addAtributo("isEstoque", true);

        verificarPermissaoAcesso("acessoManutencaoEstoquePermitido", TipoAcesso.ADMINISTRACAO,
                TipoAcesso.MANUTENCAO_ESTOQUE, TipoAcesso.CADASTRO_PEDIDO_COMPRA);

        verificarPermissaoAcesso("acessoValorEstoquePermitido", TipoAcesso.ADMINISTRACAO, TipoAcesso.OPERACAO_CONTABIL);
    }

    private void gerarRelatorioItemEstoque(List<ItemEstoque> lista) {
        RelatorioWrapper<String, ItemEstoque> relatorio = new RelatorioWrapper<String, ItemEstoque>(
                "Relatório de itens do estoque");
        String id = null;
        for (ItemEstoque item : lista) {
            formatarItemEstoque(item);
            id = item.getFormaMaterial() + " - " + item.getSiglaMaterial();
            relatorio.addGrupo(id, item);
        }
        addAtributo("relatorio", relatorio);
    }

    @Post("estoque/item/inclusao/configuracaoestoque")
    public void inserirConfiguracaoEstoque(ItemEstoque itemPedido, Material material, FormaMaterial formaMaterial) {
        try {
            itemPedido.setMargemMinimaLucro(NumeroUtils.gerarAliquota(itemPedido.getMargemMinimaLucro()));

            estoqueService.inserirConfiguracaoEstoque(itemPedido);
            gerarMensagemSucesso("Item de estoque inserido/alterado com sucesso.");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("itemPedido", itemPedido);
        }

        addAtributo("permanecerTopo", true);
        if (material != null && formaMaterial != null) {
            pesquisarItemEstoque(material, formaMaterial);
        } else {
            irTopoPagina();
        }
    }

    @Post("estoque/inclusao/configuracaoncm")
    public void inserirConfiguracaoNcmEstoque(Material material, FormaMaterial formaMaterial, String ncm) {
        boolean configurado = estoqueService.inserirConfiguracaoNcmEstoque(material == null ? null : material.getId(),
                formaMaterial, ncm);
        if (configurado) {
            gerarMensagemSucesso("NCM alterado/incluído con sucesso.");
        } else {
            gerarMensagemAlerta("NCM não foi alterado/incluído no sistema.");
        }
        pesquisarItemEstoque(material, formaMaterial);
        irTopoPagina();
    }

    @Post("estoque/item/inclusao")
    public void inserirItemEstoque(ItemEstoque itemPedido, Material material, FormaMaterial formaMaterial) {
        try {
            itemPedido.setAliquotaIPI(NumeroUtils.gerarAliquota(itemPedido.getAliquotaIPI()));
            itemPedido.setAliquotaICMS(NumeroUtils.gerarAliquota(itemPedido.getAliquotaICMS()));
            itemPedido.setMargemMinimaLucro(NumeroUtils.gerarAliquota(itemPedido.getMargemMinimaLucro()));

            estoqueService.inserirItemEstoque(itemPedido);

            gerarMensagemSucesso("Item de estoque inserido/alterado com sucesso.");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("itemPedido", itemPedido);
        }

        alteracaoEstoquePublisher.publicar();

        addAtributo("permanecerTopo", true);
        if (material != null && formaMaterial != null) {
            pesquisarItemEstoque(material, formaMaterial);
        } else {
            irTopoPagina();
        }
    }

    @Post("estoque/escassez")
    public void pesquisarEscassezItemEstoque(Material material, FormaMaterial formaMaterial) {
        pesquisarItemEstoque(material, formaMaterial, true);
    }

    @Post("estoque/item/listagem")
    public void pesquisarItemEstoque(Material material, FormaMaterial formaMaterial) {
        pesquisarItemEstoque(material, formaMaterial, false);
    }

    private void pesquisarItemEstoque(Material material, FormaMaterial formaMaterial, boolean isListagemEscassez) {
        if (!isListagemEscassez && ((material == null || material.getId() == null) && formaMaterial == null)) {
            addAtributo("permanecerTopo", true);
        } else {
            material = materialService.pesquisarById(material == null ? null : material.getId());
            final Integer idMaterial = material != null ? material.getId() : null;
            List<ItemEstoque> lista = null;
            if (isListagemEscassez) {
                lista = estoqueService.pesquisarItemEstoqueEscasso();
            } else {
                lista = estoqueService.pesquisarItemEstoque(idMaterial, formaMaterial);
            }

            gerarRelatorioItemEstoque(lista);

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

    @Post("estoque/item/{idItemEstoque}")
    public void pesquisarItemEstoqueById(Integer idItemEstoque, Material material, FormaMaterial formaMaterial) {
        ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
        if (itemEstoque == null) {
            gerarListaMensagemErro("Item de estoque não existe no sistema");
        } else {
            formatarAliquotaItemEstoque(itemEstoque);
            addAtributo("itemPedido", itemEstoque);
        }
        addAtributo("permanecerTopo", true);

        material = materialService.pesquisarById(material == null ? null : material.getId());
        if (material != null || formaMaterial != null) {
            pesquisarItemEstoque(material, formaMaterial);
        } else {
            irTopoPagina();
        }
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

    @Get("estoque/item/ncm")
    public void pesquisarNcmItemEstoque(ItemEstoque itemEstoque) {
        try {
            String ncm = estoqueService.pesquisarNcmItemEstoque(itemEstoque);
            serializarJson(new SerializacaoJson("ncm", ncm == null ? "" : ncm));
        } catch (Exception e) {
            gerarLogErroRequestAjax("cálculo do preço minimo do item do pedido", e);
        }
    }

    @Get("estoque/descricaopeca")
    public void pesquisarPecaByDescricao(String descricao) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        if (descricao != null && !descricao.isEmpty()) {
            List<ItemEstoque> listaPeca = estoqueService.pesquisarPecaByDescricao(descricao);
            for (ItemEstoque peca : listaPeca) {
                lista.add(new Autocomplete(peca.getId(), peca.getDescricaoPeca()));
            }
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    @Post("estoque/item/reajustarpreco")
    public void reajustarPrecoItemEstoque(ItemEstoque itemPedido, Material material, FormaMaterial formaMaterial) {

        itemPedido.setAliquotaReajuste(NumeroUtils.gerarAliquota(itemPedido.getAliquotaReajuste()));
        try {
            estoqueService.reajustarPrecoItemEstoque(itemPedido);

            if (itemPedido.getId() != null) {
                gerarMensagemSucesso("Item de estoque No. " + itemPedido.getId()
                        + " teve seu preço reajustado com sucesso");
            } else {
                gerarMensagemSucesso("Itens de estoque da forma teve seu preço reajustado com sucesso");
            }

        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("permanecerTopo", true);
            addAtributo("itemPedido", itemPedido);
        }

        addAtributo("permanecerTopo", true);
        if (material != null && formaMaterial != null) {
            pesquisarItemEstoque(material, formaMaterial);
        } else {
            irTopoPagina();
        }
    }

    @Post("estoque/item/edicao")
    public void redefinirItemEstoque(ItemEstoque itemEstoque, Material material, FormaMaterial formaMaterial) {
        try {
            if (estoqueService.pesquisarItemEstoqueById(itemEstoque.getId()) == null) {
                gerarListaMensagemErro("O item de estoque não foi encontrado.");
            } else {
                itemEstoque.setAliquotaIPI(NumeroUtils.gerarAliquota(itemEstoque.getAliquotaIPI()));
                itemEstoque.setAliquotaICMS(NumeroUtils.gerarAliquota(itemEstoque.getAliquotaICMS()));
                itemEstoque.setMargemMinimaLucro(NumeroUtils.gerarAliquota(itemEstoque.getMargemMinimaLucro()));
                estoqueService.redefinirItemEstoque(itemEstoque);
                gerarMensagemSucesso("Item de estoque inserido/alterado com sucesso.");
            }
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("permanecerTopo", true);
        }

        alteracaoEstoquePublisher.publicar();

        pesquisarItemEstoque(material, formaMaterial);
    }
}
