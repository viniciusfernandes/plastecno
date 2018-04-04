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
        Integer idPedido;
        try {
            idPedido = negociacaoService.aceitarNegocicacaoEOrcamentoByIdNegociacao(idNegociacao);
            redirecTo(PedidoController.class).pesquisarPedidoById(idPedido, TipoPedido.REVENDA, true);
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
            v.setValorCategoriaInicial(NumeroUtils.formatarValor2Decimais(negociacaoService
                    .calcularValorCategoriaNegociacaoAberta(idVendedor, categoriaInicial)));
            v.setValorCategoriaFinal(NumeroUtils.formatarValor2Decimais(negociacaoService
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

    @Get("negociacao/geracaoindicadorcliente")
    public void gerarIndicadorCliente() {
        try {
            System.out.println("INICIO de geracao de indicador do cliente...");

            negociacaoService.gerarIndicadorCliente();

            System.out.println("FIM de geracao de indicador do cliente...");
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

    @Post("negociacao/observacao/inclusao")
    public void inserirObservacao(Integer idNegociacao, String observacao) {
        try {
            negociacaoService.inserirObservacao(idNegociacao, observacao);
            serializarJson(new SerializacaoJson("sucesso", "A observação foi incluida com sucesso."));
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        }
    }

    @Get("negociacao")
    public void negociacaoHome() {
        RelatorioWrapper<CategoriaNegociacao, Negociacao> rel = negociacaoService
                .gerarRelatorioNegociacao(getCodigoUsuario());

        for (GrupoWrapper<CategoriaNegociacao, Negociacao> g : rel.getListaGrupo()) {
            g.setPropriedade("valorTotal", NumeroUtils.formatarValor2Decimais((Double) g.getPropriedade("valorTotal")));
            double ind = -1;
            for (Negociacao n : g.getListaElemento()) {
                if (n == null) {
                    continue;
                }
                // Apresentando o indice de valor limitado por 100 por eh o
                // suficiente para informar o usuario dos potenciais do
                // usuario. Alem disso estava desalinhando os campos da tela.
                ind = NumeroUtils.gerarPercentualInteiro(n.getIndiceConversaoValor());
                n.setIndiceConversaoValor(ind > 1000d ? 999d : ind);

                ind = NumeroUtils.gerarPercentualInteiro(n.getIndiceConversaoQuantidade());
                n.setIndiceConversaoQuantidade(ind > 1000d ? 999d : ind);
            }
        }

        addAtributo("relatorio", rel);
        addAtributo("motivoPagamento", TipoNaoFechamento.FORMA_PAGAMENTO);
        addAtributo("motivoFrete", TipoNaoFechamento.FRETE);
        addAtributo("motivoOutros", TipoNaoFechamento.OUTROS);
        addAtributo("motivoEntrega", TipoNaoFechamento.PRAZO_ENTREGA);
        addAtributo("motivoPreco", TipoNaoFechamento.PRECO);
    }

    @Get("negociacao/observacao/{idNegociacao}")
    public void pesquisarObservacao(Integer idNegociacao) {
        String obs = negociacaoService.pesquisarObservacao(idNegociacao);
        serializarJson(new SerializacaoJson("observacao", obs == null ? " " : obs));
    }
}
