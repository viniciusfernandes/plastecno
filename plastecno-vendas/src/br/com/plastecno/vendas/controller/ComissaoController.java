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
    private UsuarioService usuarioService;

    @Servico
    private ClienteService clienteService;

    @Servico
    private ComissaoService comissaoService;

    public ComissaoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("comissao")
    public void comissaoHome() {
        addAtributo("listaFormaMaterial", FormaMaterial.values());
    }

    private void formatarComissao(List<Comissao> listaComissao) {
        for (Comissao comissao : listaComissao) {
            comissao.setDataInicioFormatado(StringUtils.formatarDataHora(comissao.getDataInicio()));
            comissao.setDataFimFormatado(StringUtils.formatarDataHora(comissao.getDataFim()));
            comissao.setValorFormatado(NumeroUtils.gerarPercentual(comissao.getValor()).toString());
        }
    }

    @Post("comissao/produto/inclusao")
    public void inserirComissaoProduto(FormaMaterial formaMaterial, Material material, Double valorComissaoProduto) {
        try {
            comissaoService.inserirComissaoProduto(formaMaterial, material.getId(),
                    NumeroUtils.gerarAliquota(valorComissaoProduto));
            gerarMensagemSucesso("Comiss�o do produto \"" + formaMaterial.getDescricao() + " - "
                    + material.getDescricao() + "\" foi inclu�da com sucesso");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("material", material);
            addAtributo("formaSelecionada", formaMaterial);
        }
        irTopoPagina();
    }

    @Post("comissao/vendedor/inclusao")
    public void inserirComissaoVendedor(Usuario vendedor, Double valorComissaoVendedor) {
        try {
            comissaoService.inserirComissaoVendedor(vendedor.getId(), NumeroUtils.gerarAliquota(valorComissaoVendedor));
            gerarMensagemSucesso("Comiss�o do vendedor \"" + vendedor.getNomeCompleto() + "\" foi inclu�da com sucesso");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("vendedor", vendedor);
            addAtributo("valorComissaoVendedor", valorComissaoVendedor);
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

    @Get("comissao/produto/listagem")
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
        addAtributo("vendedor", vendedor);
        addAtributo("valorComissaoVendedor",
                NumeroUtils.gerarPercentual(comissaoService.pesquisarValorComissaoVigenteVendedor(idVendedor)));
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
