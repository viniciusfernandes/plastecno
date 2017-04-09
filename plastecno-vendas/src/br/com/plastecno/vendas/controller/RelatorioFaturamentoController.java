package br.com.plastecno.vendas.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.entity.NFePedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.relatorio.conversor.GeradorRelatorioPDF;

@Resource
public class RelatorioFaturamentoController extends AbstractController {

    @Servico
    private NFeService nFeService;

    public RelatorioFaturamentoController(Result result, GeradorRelatorioPDF geradorRelatorioPDF,
            HttpServletRequest request) {
        super(result, null, geradorRelatorioPDF, request);
    }

    @Get("relatorio/faturamento/pdf")
    public Download downloadRelatorioFaturamento(Date dataInicial, Date dataFinal) {
        try {
            List<NFePedido> lNfe = nFeService.pesquisarNFePedidoEntradaEmitidoByPeriodo(new Periodo(dataInicial,
                    dataFinal));
            double vlTotal = 0;
            double vlTotalICMS = 0;
            for (NFePedido n : lNfe) {
                n.setDataEmissaoFormatada(StringUtils.formatarData(n.getDataEmissao()));
                vlTotal += n.getValor() == null ? 0 : n.getValor();
                vlTotalICMS += n.getValorICMS() == null ? 0 : n.getValorICMS();
            }

            String dtInicial = StringUtils.formatarData(dataInicial);
            String dtFinal = StringUtils.formatarData(dataFinal);

            addAtributoPDF("dataInicial", dtInicial);
            addAtributoPDF("dataFinal", dtFinal);
            addAtributoPDF("vlTotal", NumeroUtils.arredondarValorMonetario(vlTotal));
            addAtributoPDF("vlTotalICMS", NumeroUtils.arredondarValorMonetario(vlTotalICMS));
            addAtributoPDF("listaNFe", lNfe);

            processarPDF("relatorioFaturamento.html");
            return gerarDownloadPDF(gerarPDF(), "Faturamento de " + dtInicial + " a " + dtFinal);
        } catch (BusinessException e) {
            gerarLogErro("geração do relatório de faturamento", e);
            return null;
        }
    }

    @Get("relatorio/faturamento")
    public void relatorioFaturamentoHome() {
        configurarFiltroPediodoMensal();
    }

}
