package br.com.plastecno.vendas.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.PagamentoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoPagamento;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoModalidadeFrete;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class PagamentoController extends AbstractController {

    @Servico
    private PagamentoService pagamentoService;
    @Servico
    private RelatorioService relatorioService;

    @Servico
    private RepresentadaService representadaService;

    public PagamentoController(Result result, UsuarioInfo usuarioInfo, HttpServletRequest request) {
        super(result, usuarioInfo, request);
    }

    private void addPagamento(Pagamento p) {
        formatarPagamento(p);
        p.setNomeFornecedor(representadaService.pesquisarNomeFantasiaById(p.getIdFornecedor()));
        addAtributo("pagamento", p);
    }

    @Get("pagamento/compraefetivada/listagem/{idFornecedor}")
    public void gerarRelatorioItemPedidoCompraEfetivada(Integer idFornecedor, Date dataInicial, Date dataFinal) {
        try {
            String nomeForn = representadaService.pesquisarNomeFantasiaById(idFornecedor);
            Pagamento p = new Pagamento();
            p.setIdFornecedor(idFornecedor);
            p.setNomeFornecedor(nomeForn);

            addPagamento(p);
            addAtributo("isPesquisaPedidoCompra", true);
            addAtributo("relatorio", relatorioService.gerarRelatorioItemPedidoCompraEfetivada(idFornecedor,
                    new Periodo(dataInicial, dataFinal)));
            irRodapePagina();
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
        addPeriodo(dataInicial, dataFinal);
    }

    private void gerarRelatorioPagamento(Date dataInicial, Date dataFinal) throws InformacaoInvalidaException {
        boolean exibirApenasInsumos = !isAcessoPermitido(TipoAcesso.OPERACAO_CONTABIL, TipoAcesso.ADMINISTRACAO);
        gerarRelatorioPagamento(
                pagamentoService.pesquisarPagamentoByPeriodo(new Periodo(dataInicial, dataFinal), exibirApenasInsumos),
                dataInicial, dataFinal);
    }

    private void gerarRelatorioPagamento(List<Pagamento> lista, Date dataInicial, Date dataFinal)
            throws InformacaoInvalidaException {

        RelatorioWrapper<String, Pagamento> relatorio = pagamentoService.gerarRelatorioPagamento(lista, new Periodo(
                dataInicial, dataFinal));

        formatarPagamento(relatorio.getListaElemento());
        addAtributo("relatorio", relatorio);
        addPeriodo(dataInicial, dataFinal);
    }

    @Get("pagamento/periodo/listagem")
    public void gerarRelatorioPagamentoByPeriodo(Date dataInicial, Date dataFinal) {
        try {

            // Estamos inicializando as datas pois esse metodo eh acessado a
            // partir do menu inicial
            if (dataInicial == null) {
                dataInicial = new Date();
            }
            if (dataFinal == null) {
                dataFinal = new Date();
            }
            gerarRelatorioPagamento(dataInicial, dataFinal);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemAlerta(e);
            irTopoPagina();
        }
    }

    @Post("pagamento/inclusao")
    public void inserirPagamento(Pagamento pagamento, Date dataInicial, Date dataFinal,
            List<Integer> listaIdItemSelecionado) {

        if (listaIdItemSelecionado == null || listaIdItemSelecionado.isEmpty()) {
            try {
                pagamentoService.inserir(pagamento);
                gerarRelatorioPagamentoByPeriodo(dataInicial, dataFinal);
                gerarMensagemSucesso("Pagamento inserido com sucesso.");
            } catch (BusinessException e) {
                addPagamento(pagamento);
                try {
                    gerarRelatorioPagamento(dataInicial, dataFinal);
                } catch (InformacaoInvalidaException e1) {
                    gerarListaMensagemErro(e);
                }
                gerarListaMensagemErro(e);
                irTopoPagina();
            }
        } else {
            try {
                pagamentoService.inserirPagamentoParceladoItemPedido(pagamento.getNumeroNF(), pagamento.getValorNF(),
                        pagamento.getDataVencimento(), pagamento.getDataEmissao(), pagamento.getModalidadeFrete(),
                        listaIdItemSelecionado);
                redirecTo(PagamentoController.class).pesquisarPagamentoByNF(pagamento.getNumeroNF(), dataInicial,
                        dataFinal);
                gerarMensagemSucesso("Pagamento inserido com sucesso.");
            } catch (BusinessException e) {
                addPagamento(pagamento);
                adicionarIdItemSelecionado(listaIdItemSelecionado);
                gerarRelatorioItemPedidoCompraEfetivada(pagamento.getIdFornecedor(), dataInicial, dataFinal);
                gerarListaMensagemErro(e);
                irTopoPagina();
            }
        }

        addPeriodo(dataInicial, dataFinal);
    }

    @Post("pagamento/liquidacao/{idPagamento}")
    public void liquidarPagamento(Integer idPagamento, Date dataInicial, Date dataFinal) {
        pagamentoService.liquidarPagamento(idPagamento);
        gerarMensagemSucesso("Pagamento liquidado com sucesso.");
        gerarRelatorioPagamentoByPeriodo(dataInicial, dataFinal);
    }

    @Post("pagamento/liquidacao/nfparcelada")
    public void liquidarPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela,
            String nomeFornecedor, Date dataInicial, Date dataFinal) {
        pagamentoService.liquidarPagamentoNFParcelada(numeroNF, idFornecedor, parcela);
        gerarMensagemSucesso("A parcela No. " + parcela + "da NF " + numeroNF + " do fornecedor " + nomeFornecedor
                + " liquidada com sucesso.");
        gerarRelatorioPagamentoByPeriodo(dataInicial, dataFinal);
    }

    @Get("pagamento")
    public void pagamentoHome() {
        addAtributo("listaModalidadeFrete", TipoModalidadeFrete.values());
        addAtributo("listaTipoPagamento", TipoPagamento.values());
        addAtributo("listaFornecedor", representadaService.pesquisarRepresentadaAtivoByTipoPedido(TipoPedido.COMPRA));
        Date dtAtual = new Date();
        addPeriodo(dtAtual, dtAtual);
    }

    @Get("pagamento/fornecedor/listagem")
    public void pesquisarFornecedorByNomeFantasia(String nomeFantasia) {
        forwardTo(RepresentadaController.class).pesquisarFornecedorByNomeFantasia(nomeFantasia);
    }

    @Get("pagamento/{idPagamento}")
    public void pesquisarPagamentoById(Integer idPagamento, Date dataInicial, Date dataFinal) {
        addPagamento(pagamentoService.pesquisarById(idPagamento));
        try {
            gerarRelatorioPagamento(dataInicial, dataFinal);
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemAlerta(e);
        }
        pagamentoHome();
        irTopoPagina();
    }

    @Get("pagamento/fornecedor/{idFornecedor}")
    public void pesquisarPagamentoByIdFornecedor(Integer idFornecedor, Date dataInicial, Date dataFinal) {
        try {
            gerarRelatorioPagamento(pagamentoService.pesquisarPagamentoByIdFornecedor(idFornecedor, new Periodo(
                    dataInicial, dataFinal)), dataInicial, dataFinal);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            addPeriodo(dataInicial, dataFinal);
            irTopoPagina();
        }
    }

    @Get("pagamento/pedido/{idPedido}")
    public void pesquisarPagamentoByIdPedido(Integer idPedido, Date dataInicial, Date dataFinal) {
        try {
            gerarRelatorioPagamento(pagamentoService.pesquisarPagamentoByIdPedido(idPedido), dataInicial, dataFinal);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            addPeriodo(dataInicial, dataFinal);
            irTopoPagina();
        }
    }

    @Get("pagamento/nf/{numeroNF}")
    public void pesquisarPagamentoByNF(Integer numeroNF, Date dataInicial, Date dataFinal) {
        try {
            gerarRelatorioPagamento(pagamentoService.pesquisarPagamentoByNF(numeroNF), dataInicial, dataFinal);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemErro(e);
            addPeriodo(dataInicial, dataFinal);
            irTopoPagina();
        }
    }

    @Post("pagamento/remocao/{idPagamento}")
    public void removerPagamento(Integer idPagamento, Date dataInicial, Date dataFinal) {
        try {
            pagamentoService.remover(idPagamento);
            gerarMensagemSucesso("Pagamento removido com sucesso.");
        } catch (BusinessException e) {
            addPagamento(pagamentoService.pesquisarById(idPagamento));
            gerarListaMensagemErro(e);
        }
        gerarRelatorioPagamentoByPeriodo(dataInicial, dataFinal);
    }

    @Post("pagamento/remocao/nfparcelada/{idItemPedido}")
    public void removerPagamentoParceladoItemPedido(Integer idItemPedido, Date dataInicial, Date dataFinal) {
        try {
            pagamentoService.removerPagamentoPaceladoItemPedido(idItemPedido);
            gerarMensagemSucesso("Pagamento do item parcelado foi removido com sucesso.");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        gerarRelatorioPagamentoByPeriodo(dataInicial, dataFinal);
    }

    @Post("pagamento/retonoliquidacao/{idPagamento}")
    public void retornarLiquidacaoPagamento(Integer idPagamento, Date dataInicial, Date dataFinal) {
        try {
            pagamentoService.retornarLiquidacaoPagamento(idPagamento);
            gerarMensagemSucesso("O retorno da liquidação do pagamento foi realizado com sucesso.");
        } catch (BusinessException e) {
            pesquisarPagamentoById(idPagamento, dataInicial, dataFinal);
            gerarListaMensagemErro(e);
        }
        try {
            gerarRelatorioPagamento(dataInicial, dataFinal);
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
    }

    @Post("pagamento/retonoliquidacao/nfparcelada")
    public void retornarLiquidacaoPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela,
            String nomeFornecedor, Date dataInicial, Date dataFinal) {
        try {
            pagamentoService.retornarLiquidacaoPagamentoNFParcelada(numeroNF, idFornecedor, parcela);
            gerarRelatorioPagamentoByPeriodo(dataInicial, dataFinal);
        } catch (BusinessException e) {
            try {
                gerarRelatorioPagamento(dataInicial, dataFinal);
            } catch (InformacaoInvalidaException e1) {
                e.addMensagem(e1.getListaMensagem());
            }
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
    }
}
