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

    private void formatarPagamento(List<Pagamento> lista) {
        for (Pagamento p : lista) {
            p.setDataVencimentoFormatada(StringUtils.formatarData(p.getDataVencimento()));
            p.setValor(NumeroUtils.arredondarValorMonetario(p.getValor()));
            p.setValorCreditoICMS(NumeroUtils.arredondarValorMonetario(p.getValorCreditoICMS()));
            p.setValorNF(NumeroUtils.arredondarValorMonetario(p.getValorNF()));
        }
    }

    @Post("pagamento/inclusao")
    public void inserirPagamento(Pagamento pagamento) {
        try {
            pagamentoService.inserir(pagamento);
            gerarMensagemSucesso("Pagamento inserido com sucesso.");
        } catch (BusinessException e) {
            addAtributo("pagamento", pagamento);
            gerarListaMensagemErro(e);
        }
        irTopoPagina();
    }

    @Get("pagamento")
    public void pagamentoHome() {
        addAtributo("listaModalidadeFrete", TipoModalidadeFrete.values());
        addAtributo("listaTipoPagamento", TipoPagamento.values());
        addAtributo("listaFornecedor", representadaService.pesquisarRepresentadaAtivoByTipoPedido(TipoPedido.COMPRA));
        addAtributo("dataInicial", formatarData(gerarDataInicioMes()));
        addAtributo("dataFinal", formatarData(new Date()));
    }

    @Get("pagamento/fornecedor/listagem")
    public void pesquisarFornecedorByNomeFantasia(String nomeFantasia) {
        forwardTo(RepresentadaController.class).pesquisarFornecedorByNomeFantasia(nomeFantasia);
    }

    @Get("pagamento/pedido/listagem")
    public void pesquisarPagamentoByIdPedido(Integer idPedido) {
    }

    @Get("pagamento/{idPagamento}")
    public void pesquisarPagamentoById(Integer idPagamento) {
        addAtributo("pagamento", pagamentoService.pesquisarById(idPagamento));
        irTopoPagina();
    }

    @Post("pagamento/liquidacao/{idPagamento}")
    public void liquidarPagamento(Integer idPagamento) {
        pagamentoService.liquidarPagamento(idPagamento);
        gerarMensagemSucesso("Pagamento liquidado com sucesso.");
        irTopoPagina();
    }

    @Get("pagamento/periodo/listagem")
    public void pesquisarPagamentoByPeriodo(Date dataInicial, Date dataFinal) {
        try {
            List<Pagamento> lista = pagamentoService.pesquisarPagamentoByPeriodo(new Periodo(dataInicial, dataFinal));
            formatarPagamento(lista);
            addAtributo(
                    "titulo",
                    "Pagamentos de " + StringUtils.formatarData(dataInicial) + " a "
                            + StringUtils.formatarData(dataFinal));
            addAtributo("listaPagamento", lista);
            irRodapePagina();
        } catch (InformacaoInvalidaException e) {
            gerarListaMensagemAlerta(e);
            irTopoPagina();
        }
    }
}
