package br.com.svr.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.NegociacaoService;
import br.com.svr.service.constante.TipoPedido;
import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
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

    @Post("negociacao/aceite/{idNegociacao}")
    public void aceitarNegocicacao(Integer idNegociacao) {
        Integer idOrcamento;
        try {
            idOrcamento = negociacaoService.aceitarNegocicacao(idNegociacao);
            redirecTo(PedidoController.class).pesquisarPedidoById(idOrcamento, TipoPedido.REVENDA, true);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }

    }

    @Post("negociacao/alteracaocategoria/{idNegociacao}")
    public void alterarCategoriaNegociacao(Integer idNegociacao, CategoriaNegociacao categoriaInicial,
            CategoriaNegociacao categoriaFinal) {
        try {
            negociacaoService.alterarCategoria(idNegociacao, categoriaFinal);
            Integer idVendedor = getCodigoUsuario();

            ValorNegociacaoJson v = new ValorNegociacaoJson();
            v.setValorCategoriaInicial(NumeroUtils.formatarValorMonetario(negociacaoService
                    .calcularValorCategoriaNegociacaoAberta(idVendedor, categoriaInicial)));
            v.setValorCategoriaFinal(NumeroUtils.formatarValorMonetario(negociacaoService
                    .calcularValorCategoriaNegociacaoAberta(idVendedor, categoriaFinal)));

            serializarJson(new SerializacaoJson("valores", v));
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        irTopoPagina();
    }

    @Post("negociacao/cancelamento/{idNegociacao}")
    public void cancelarNegocicacao(Integer idNegociacao, TipoNaoFechamento motivo) {
        try {
            negociacaoService.cancelarNegocicacao(idNegociacao, motivo);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        irTopoPagina();
    }

    @Get("negociacao/geracaoindiceconversaocliente")
    public void gerarIndiceConversaoCliente() {
        try {
            System.out.println("INICIO de geracao de indice de conversao...");

            negociacaoService.gerarIndiceConversaoCliente();

            System.out.println("FIM de geracao de indice de conversao...");
        } catch (BusinessException e) {
            gerarLogErro("Geracao indice de conversao", e);
        }
    }

    @Get("negociacao/geracaonegociacao")
    public void gerarNegociacao() {
        try {
            System.out.println("INICIO de geracao de negociacao...");

            negociacaoService.gerarNegociacaoInicial();

            System.out.println("FIM de geracao de negociacao...");
        } catch (BusinessException e) {
            gerarLogErro("Geracao de negociacao", e);
        }
        // irTopoPagina();
    }

    @Get("negociacao")
    public void negociacaoHome() {
        RelatorioWrapper<CategoriaNegociacao, Negociacao> rel = negociacaoService
                .gerarRelatorioNegociacao(getCodigoUsuario());

        for (GrupoWrapper<CategoriaNegociacao, Negociacao> g : rel.getListaGrupo()) {
            g.setPropriedade("valorTotal", NumeroUtils.formatarValorMonetario((Double) g.getPropriedade("valorTotal")));
        }

        addAtributo("relatorio", rel);
        addAtributo("motivoPagamento", TipoNaoFechamento.FORMA_PAGAMENTO);
        addAtributo("motivoFrete", TipoNaoFechamento.FRETE);
        addAtributo("motivoOutros", TipoNaoFechamento.OUTROS);
        addAtributo("motivoEntrega", TipoNaoFechamento.PRAZO_ENTREGA);
        addAtributo("motivoPreco", TipoNaoFechamento.PRECO);
    }
}
