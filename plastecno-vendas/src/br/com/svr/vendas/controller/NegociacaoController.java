package br.com.svr.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.NegociacaoService;
import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.GrupoWrapper;
import br.com.svr.service.wrapper.RelatorioWrapper;
import br.com.svr.util.NumeroUtils;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.json.SerializacaoJson;
import br.com.svr.vendas.json.ValorNegociacaoJson;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class NegociacaoController extends AbstractController {
    @Servico
    private NegociacaoService negociacaoService;

    public NegociacaoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Post("negociacao/alteracaocategoria/{idNegociacao}")
    public void alterarCategoriaNegociacao(Integer idNegociacao, CategoriaNegociacao categoriaInicial,
            CategoriaNegociacao categoriaFinal) {
        try {
            negociacaoService.alterarCategoria(idNegociacao, categoriaFinal);
            Integer idVendedor = getCodigoUsuario();

            ValorNegociacaoJson v = new ValorNegociacaoJson();
            v.setValorCategoriaInicial(NumeroUtils.formatarValorMonetario(negociacaoService
                    .calcularValorCategoriaNegociacao(idVendedor, categoriaInicial)));
            v.setValorCategoriaFinal(NumeroUtils.formatarValorMonetario(negociacaoService
                    .calcularValorCategoriaNegociacao(idVendedor, categoriaFinal)));

            serializarJson(new SerializacaoJson("valores", v));
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        irTopoPagina();
    }

    @Get("negociacao/gerarnegociacao")
    public void gerarNegociacao() {
        try {
            negociacaoService.gerarNegociacaoInicial();
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        irTopoPagina();
    }

    @Get("negociacao")
    public void negociacaoHome() {
        RelatorioWrapper<CategoriaNegociacao, Negociacao> rel = negociacaoService
                .gerarRelatorioNegociacao(getCodigoUsuario());

        for (GrupoWrapper<CategoriaNegociacao, Negociacao> g : rel.getListaGrupo()) {
            g.setPropriedade("valorTotal", NumeroUtils.formatarValorMonetario((Double) g.getPropriedade("valorTotal")));
        }
        
        addAtributo("relatorio", rel);
    }
}
