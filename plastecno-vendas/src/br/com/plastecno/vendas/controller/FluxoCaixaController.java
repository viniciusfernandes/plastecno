package br.com.plastecno.vendas.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.FaturamentoService;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Fluxo;
import br.com.plastecno.service.wrapper.FluxoCaixa;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.FluxoCaixaPainelGrafico2D;
import br.com.plastecno.vendas.json.Grafico2D;
import br.com.plastecno.vendas.json.SerializacaoJson;

@Resource
public class FluxoCaixaController extends AbstractController {

    private static final Map<Integer, String> mapMes;

    static {
        mapMes = new HashMap<>();
        mapMes.put(0, "Jan");
        mapMes.put(1, "Fev");
        mapMes.put(2, "Mar");
        mapMes.put(3, "Abr");
        mapMes.put(4, "Mai");
        mapMes.put(5, "Jun");
        mapMes.put(6, "Jul");
        mapMes.put(7, "Ago");
        mapMes.put(8, "Set");
        mapMes.put(9, "Out");
        mapMes.put(10, "Nov");
        mapMes.put(11, "Dez");
    }

    @Servico
    private FaturamentoService faturamentoService;

    public FluxoCaixaController(Result result) {
        super(result);
    }

    @Get("fluxocaixa")
    public void fluxoCaixaHome() {
        addPeriodo(gerarDataInicioAno(), gerarDataFimAno());
    }

    @Get("fluxocaixa/graficos")
    public void gerarGraficos(Date dataInicial, Date dataFinal) {
        try {
            FluxoCaixa fluxoCaixa = faturamentoService.gerarFluxoFaixaByPeriodo(new Periodo(dataInicial, dataFinal));
            List<Fluxo> lFluxo = fluxoCaixa.gerarFluxoByMes();
            Grafico2D grfFluxoMensal = new Grafico2D("Fluxo Mensal");
            Grafico2D grfPagMensal = new Grafico2D("Pagamento Mesal");
            Grafico2D grfFatMensal = new Grafico2D("Faturamento Mensal");
            Grafico2D grfTipoPag = new Grafico2D("Tipo Pagamento");
            Grafico2D gValFatAnual = new Grafico2D("Faturamento Anual");

            // gerando os graficos mensais
            String label = null;
            for (Fluxo f : lFluxo) {
                label = mapMes.get(f.getMes()) + "/" + f.getAno();
                grfFluxoMensal.adicionar(label, NumeroUtils.arredondarValorMonetario(f.getValFluxo()));
                grfFatMensal.adicionar(label, NumeroUtils.arredondarValorMonetario(f.getValDuplicata()));
                grfPagMensal.adicionar(label, NumeroUtils.arredondarValorMonetario(-f.getValPagamento()));
            }

            // gerando o grafico de pagamento por tipo de pagamento
            List<Fluxo> lFluxoPag = fluxoCaixa.gerarFluxoByTipoPagamento();
            for (Fluxo f : lFluxoPag) {
                grfTipoPag.adicionar(f.getTipoPagamento().toString(),
                        NumeroUtils.arredondarValorMonetario(f.getValPagamento()));
            }

            // gerando o grafico de fluxo de caixa anual
            List<Fluxo> lFluxoAnual = fluxoCaixa.gerarFluxoByAno();
            for (Fluxo f : lFluxoAnual) {
                gValFatAnual.adicionar(String.valueOf(f.getAno()),
                        NumeroUtils.arredondarValorMonetario(f.getValFluxo()));
            }

            FluxoCaixaPainelGrafico2D grfPainel = new FluxoCaixaPainelGrafico2D();
            grfPainel.setGraficoFaturamentoAnual(gValFatAnual);
            grfPainel.setGraficoFaturamentoMensal(grfFatMensal);
            grfPainel.setGraficoFluxoMensal(grfFluxoMensal);
            grfPainel.setGraficoPagamentoMensal(grfPagMensal);
            grfPainel.setGraficoTipoPagamento(grfTipoPag);

            serializarJson(new SerializacaoJson("painelGrafico", grfPainel, true));

        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("inclusao/alteracao do pedido", e);
        }

    }
}
