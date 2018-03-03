package br.com.svr.vendas.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.svr.service.PedidoService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.relatorio.RelatorioService;
import br.com.svr.service.validacao.InformacaoInvalidaException;
import br.com.svr.service.wrapper.Periodo;
import br.com.svr.util.NumeroUtils;
import br.com.svr.util.StringUtils;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.login.UsuarioInfo;
import br.com.svr.vendas.relatorio.conversor.GeradorRelatorioPDF;

@Resource
public class RelatorioVendaRepresentadaController extends AbstractController {

    @Servico
    private PedidoService pedidoService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private RepresentadaService representadaService;

    public RelatorioVendaRepresentadaController(Result result, UsuarioInfo usuarioInfo, GeradorRelatorioPDF gerador,
            HttpServletRequest request) {
        super(result, usuarioInfo, gerador, request);
    }

    @Get("relatorio/venda/representada/pdf")
    public Download gerarRelatorioVendaRepresentada(Date dataInicial, Date dataFinal, Integer idRepresentada) {

        final Representada representada = this.representadaService.pesquisarById(idRepresentada);
        final String dataInicialFormatada = StringUtils.formatarData(dataInicial);
        final String dataFinalFormatada = StringUtils.formatarData(dataFinal);

        addAtributo("dataInicial", dataInicialFormatada);
        addAtributo("dataFinal", dataInicialFormatada);
        addAtributo("representadaSelecionada", representada);

        List<Pedido> listaPedido;
        try {
            listaPedido = this.pedidoService.pesquisarEnviadosByPeriodoERepresentada(
                    new Periodo(dataInicial, dataFinal), idRepresentada);
        } catch (InformacaoInvalidaException e1) {
            gerarListaMensagemErro(e1);
            return null;
        }

        double totalVendido = 0d;
        for (Pedido pedido : listaPedido) {
            totalVendido += pedido.getValorPedido();
            formatarPedido(pedido);
        }

        try {

            addAtributoPDF("representada", representada);
            addAtributoPDF("listaPedido", listaPedido);
            addAtributoPDF("totalVendido", NumeroUtils.formatarValorMonetario(totalVendido));
            addAtributoPDF("valorComissao",
                    NumeroUtils.formatarValorMonetario(totalVendido * representada.getComissao()));
            addAtributoPDF("dataInicial", dataInicialFormatada);
            addAtributoPDF("dataFinal", dataFinalFormatada);
            processarPDF("relatorioPedido.html");
            return this.gerarDownloadPDF(gerarPDF(), "Vendas " + representada.getNomeFantasia() + " "
                    + dataInicialFormatada + " a " + dataFinalFormatada + ".pdf");

        } catch (Exception e) {
            gerarLogErro("geracao do relatorio de pedidos por representada", e);
            return null;
        }
    }

    @Get("relatorio/venda/representada")
    public void relatorioVendaRepresentadaHome() {
        addAtributo("listaRepresentada", this.representadaService.pesquisarRepresentadaEFornecedor());
        configurarFiltroPediodoMensal();
    }
}
