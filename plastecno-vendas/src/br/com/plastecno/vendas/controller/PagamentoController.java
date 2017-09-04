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
import br.com.plastecno.service.constante.TipoPagamento;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.constante.TipoModalidadeFrete;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;

@Resource
public class PagamentoController extends AbstractController {

    @Servico
    private PagamentoService pagamentoService;
    @Servico
    private RepresentadaService representadaService;

    public PagamentoController(Result result, HttpServletRequest request) {
        super(result, request);
    }

    private void addListaPagamento(Date dataInicial, Date dataFinal) throws InformacaoInvalidaException {
        addListaPagamento(pagamentoService.pesquisarPagamentoByPeriodo(new Periodo(dataInicial, dataFinal)),
                dataInicial, dataFinal);
    }

    private void addListaPagamento(List<Pagamento> lista, Date dataInicial, Date dataFinal)
            throws InformacaoInvalidaException {
        formatarPagamento(lista);
        addAtributo("titulo",
                "Pagamentos de " + StringUtils.formatarData(dataInicial) + " a " + StringUtils.formatarData(dataFinal));
        addAtributo("listaPagamento", lista);
        addPeriodo(dataInicial, dataFinal);
    }

    private void addPagamento(Pagamento p) {
        formatarPagamento(p);
        p.setNomeFornecedor(representadaService.pesquisarNomeFantasiaById(p.getIdFornecedor()));
        addAtributo("pagamento", p);
    }

    private void formatarPagamento(List<Pagamento> lista) {
        for (Pagamento p : lista) {
            formatarPagamento(p);
        }
    }

    private void formatarPagamento(Pagamento p) {
        p.setDataVencimentoFormatada(StringUtils.formatarData(p.getDataVencimento()));
        p.setDataEmissaoFormatada(StringUtils.formatarData(p.getDataEmissao()));
        p.setDataRecebimentoFormatada(StringUtils.formatarData(p.getDataRecebimento()));

        p.setValor(NumeroUtils.arredondarValorMonetario(p.getValor()));
        p.setValorCreditoICMS(NumeroUtils.arredondarValorMonetario(p.getValorCreditoICMS()));
        p.setValorNF(NumeroUtils.arredondarValorMonetario(p.getValorNF()));
    }

    @Post("pagamento/inclusao")
    public void inserirPagamento(Pagamento pagamento, Date dataInicial, Date dataFinal) {
        try {
            pagamentoService.inserir(pagamento);
            pesquisarPagamentoByPeriodo(dataInicial, dataFinal);

            gerarMensagemSucesso("Pagamento inserido com sucesso.");
        } catch (BusinessException e) {
            addPagamento(pagamento);
            try {
                addListaPagamento(dataInicial, dataFinal);
            } catch (InformacaoInvalidaException e1) {
                gerarListaMensagemErro(e);
            }
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
    }

    @Post("pagamento/liquidacao/{idPagamento}")
    public void liquidarPagamento(Integer idPagamento, Date dataInicial, Date dataFinal) {
        pagamentoService.liquidarPagamento(idPagamento);
        gerarMensagemSucesso("Pagamento liquidado com sucesso.");
        pesquisarPagamentoByPeriodo(dataInicial, dataFinal);
    }

    @Get("pagamento")
    public void pagamentoHome() {
        addAtributo("listaModalidadeFrete", TipoModalidadeFrete.values());
        addAtributo("listaTipoPagamento", TipoPagamento.values());
        addAtributo("listaFornecedor", representadaService.pesquisarRepresentadaAtivoByTipoPedido(TipoPedido.COMPRA));
        addPeriodo(gerarDataInicioMes(), new Date());
    }

    @Get("pagamento/fornecedor/listagem")
    public void pesquisarFornecedorByNomeFantasia(String nomeFantasia) {
        forwardTo(RepresentadaController.class).pesquisarFornecedorByNomeFantasia(nomeFantasia);
    }

    @Get("pagamento/{idPagamento}")
    public void pesquisarPagamentoById(Integer idPagamento, Date dataInicial, Date dataFinal) {
        addPagamento(pagamentoService.pesquisarById(idPagamento));
        try {
            addListaPagamento(dataInicial, dataFinal);
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemAlerta(e);
        }
        pagamentoHome();
        irTopoPagina();
    }

    @Get("pagamento/fornecedor/{idFornecedor}")
    public void pesquisarPagamentoByIdFornecedor(Integer idFornecedor, Date dataInicial, Date dataFinal) {
        List<Pagamento> lista;
        try {
            lista = pagamentoService
                    .pesquisarPagamentoByIdFornecedor(idFornecedor, new Periodo(dataInicial, dataFinal));
            addListaPagamento(lista, dataInicial, dataFinal);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            addPeriodo(dataInicial, dataFinal);
            irTopoPagina();
        }
    }

    @Get("pagamento/pedido/{idPedido}")
    public void pesquisarPagamentoByIdPedido(Integer idPedido, Date dataInicial, Date dataFinal) {
        List<Pagamento> lista;
        try {
            lista = pagamentoService.pesquisarPagamentoByIdPedido(idPedido);
            addListaPagamento(lista, dataInicial, dataFinal);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            addPeriodo(dataInicial, dataFinal);
            irTopoPagina();
        }
    }

    @Get("pagamento/nf/{numeroNF}")
    public void pesquisarPagamentoByNF(Integer numeroNF, Date dataInicial, Date dataFinal) {
        List<Pagamento> lista;
        try {
            lista = pagamentoService.pesquisarPagamentoByNF(numeroNF);
            addListaPagamento(lista, dataInicial, dataFinal);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            addPeriodo(dataInicial, dataFinal);
            irTopoPagina();
        }
    }

    @Get("pagamento/periodo/listagem")
    public void pesquisarPagamentoByPeriodo(Date dataInicial, Date dataFinal) {
        try {
            addListaPagamento(dataInicial, dataFinal);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemAlerta(e);
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
        pesquisarPagamentoByPeriodo(dataInicial, dataFinal);
    }

    @Post("pagamento/retonoliquidacao/{idPagamento}")
    public void retornarLiquidacaoPagamento(Integer idPagamento, Date dataInicial, Date dataFinal) {
        pagamentoService.retornarLiquidacaoPagamento(idPagamento);
        pesquisarPagamentoById(idPagamento, dataInicial, dataFinal);
    }
}
