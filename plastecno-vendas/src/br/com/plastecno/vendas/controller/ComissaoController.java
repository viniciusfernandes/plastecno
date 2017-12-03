package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class ComissaoController extends AbstractController {
    @Servico
    private ClienteService clienteService;

    @Servico
    private ComissaoService comissaoService;

    @Servico
    private UsuarioService usuarioService;

    public ComissaoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("comissao")
    public void comissaoHome() {
        addAtributo("listaFormaMaterial", FormaMaterial.values());
    }

    private void formatarComissao(Comissao comissao) {
        if (comissao == null) {
            return;
        }
        comissao.setDataInicioFormatado(StringUtils.formatarDataHora(comissao.getDataInicio()));
        comissao.setDataFimFormatado(StringUtils.formatarDataHora(comissao.getDataFim()));
        comissao.setAliquotaRevendaFormatada(NumeroUtils.gerarPercentualInteiro(comissao.getAliquotaRevenda())
                .toString());
        comissao.setAliquotaRepresentacaoFormatada(NumeroUtils.gerarPercentual(comissao.getAliquotaRepresentacao(), 2)
                .toString());
    }

    private void formatarComissao(List<Comissao> listaComissao) {
        for (Comissao comissao : listaComissao) {
            formatarComissao(comissao);
        }
    }

    @Post("comissao/produto/inclusao")
    public void inserirComissaoProduto(FormaMaterial formaMaterial, Material material, Double comissaoRevenda) {
        try {
            comissaoService.inserirComissaoProduto(formaMaterial, material.getId(),
                    NumeroUtils.gerarAliquota(comissaoRevenda));
            String descricao = "";
            if (formaMaterial != null) {
                descricao = formaMaterial.getDescricao();
            }
            if (material != null && material.getId() != null) {
                descricao += " - " + material.getDescricao();
            }
            gerarMensagemSucesso("Comissão do produto \"" + descricao + "\" foi incluída com sucesso");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("material", material);
            addAtributo("formaSelecionada", formaMaterial);
        }
        irTopoPagina();
    }

    @Post("comissao/vendedor/inclusao")
    public void inserirComissaoVendedor(Usuario vendedor, Double comissaoRevenda, Double comissaoRepresentacao) {
        try {
            usuarioService.alterarComissaoSimples(vendedor.getId(), vendedor.isComissionadoSimples());
            comissaoService.inserirComissaoVendedor(vendedor.getId(), NumeroUtils.gerarAliquota(comissaoRevenda),
                    NumeroUtils.gerarAliquota(comissaoRepresentacao, 4));
            gerarMensagemSucesso("Comissão do vendedor \"" + vendedor.getNome() + "\" foi incluída com sucesso");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("vendedor", vendedor);
            addAtributo("comissaoRevenda", comissaoRevenda);
        }
        irTopoPagina();
    }

    @Get("comissao/vendedor/listagem")
    public void pesquisarComissaoByIdVendedor(Integer idVendedor) {
        List<Comissao> listaComissao = comissaoService.pesquisarComissaoByIdVendedor(idVendedor);
        formatarComissao(listaComissao);
        addAtributo("isProduto", false);
        addAtributo("listaComissao", listaComissao);
        irRodapePagina();
    }

    @Post("comissao/produto/listagem")
    public void pesquisarComissaoByProduto(FormaMaterial formaMaterial, Material material) {
        List<Comissao> listaComissao = comissaoService.pesquisarComissaoByProduto(formaMaterial, material.getId());
        formatarComissao(listaComissao);
        addAtributo("isProduto", true);
        addAtributo("listaComissao", listaComissao);
        irRodapePagina();
    }

    @Get("comissao/vendedor")
    public void pesquisarVendedorById(Integer idVendedor) {
        Usuario vendedor = usuarioService.pesquisarVendedorById(idVendedor);
        Comissao comissao = comissaoService.pesquisarComissaoVigenteVendedor(idVendedor);
        formatarComissao(comissao);

        addAtributo("vendedor", vendedor);
        addAtributo("comissao", comissao);
        irTopoPagina();
    }

    @Get("comissao/vendedor/listagem/nome")
    public void pesquisarVendedorByNome(String nome) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Usuario> listaVendedor = usuarioService.pesquisarVendedorByNome(nome);
        for (Usuario vendedor : listaVendedor) {
            lista.add(new Autocomplete(vendedor.getId(), vendedor.getNome()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }
}
