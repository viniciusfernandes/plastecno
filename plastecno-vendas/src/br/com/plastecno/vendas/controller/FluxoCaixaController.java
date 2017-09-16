package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
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
import br.com.plastecno.vendas.json.GraficoBar2D;
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
            GraficoBar2D gValFluxo = new GraficoBar2D("Fluxo");
            GraficoBar2D gValPag = new GraficoBar2D("Saída");
            GraficoBar2D gValDup = new GraficoBar2D("Entrada");
            GraficoBar2D gValTipoPag = new GraficoBar2D("Tipo Pagamento");
            GraficoBar2D gValFatAnual = new GraficoBar2D("Faturamento Anual");

            String label = null;
            for (Fluxo f : lFluxo) {
                label = mapMes.get(f.getMes()) + "/" + f.getAno();
                gValFluxo.adicionar(label, NumeroUtils.arredondarValorMonetario(f.getValFluxo()));
                gValDup.adicionar(label, NumeroUtils.arredondarValorMonetario(f.getValDuplicata()));
                gValPag.adicionar(label, NumeroUtils.arredondarValorMonetario(-f.getValPagamento()));
            }

            List<Fluxo> lFluxoPag = fluxoCaixa.gerarFluxoByTipoPagamento();
            for (Fluxo f : lFluxoPag) {
                gValTipoPag.adicionar(f.getTipoPagamento().toString(),
                        NumeroUtils.arredondarValorMonetario(f.getValPagamento()));
            }

            List<Fluxo> lFluxoAnual = fluxoCaixa.gerarFluxoByAno();
            for (Fluxo f : lFluxoAnual) {
                gValFatAnual.adicionar(String.valueOf(f.getAno()),
                        NumeroUtils.arredondarValorMonetario(f.getValFluxo()));
            }

            List<GraficoBar2D> lGrafico = new ArrayList<>();
            lGrafico.add(gValDup);
            lGrafico.add(gValPag);
            lGrafico.add(gValFluxo);
            lGrafico.add(gValTipoPag);
            lGrafico.add(gValFatAnual);
            serializarJson(new SerializacaoJson("listaGrafico", lGrafico).incluirAtributo("listaLabel", "listaDado"));

        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("inclusao/alteracao do pedido", e);
        }

    }
}
