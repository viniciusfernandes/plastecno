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
import br.com.plastecno.vendas.json.GraficoBar2D;
import br.com.plastecno.vendas.json.SerializacaoJson;

@Resource
public class FluxoCaixaController extends AbstractController {

    @Servico
    private FaturamentoService faturamentoService;

    public FluxoCaixaController(Result result) {
        super(result);
    }

    @Get("fluxocaixa")
    public void fluxoCaixaHome() {
        addPeriodo(gerarDataInicioAno(), gerarDataFimAno());
    }

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

    @Get("fluxocaixa/grafico/bar/mes")
    public void gerarGraficoBarMensal(Date dataInicial, Date dataFinal) {
        try {
            FluxoCaixa fluxoCaixa = faturamentoService.gerarFluxoFaixaByPeriodo(new Periodo(dataInicial, dataFinal));
            List<Fluxo> lFluxo = fluxoCaixa.gerarFluxoByMes();
            GraficoBar2D grf = new GraficoBar2D("Faturamento mensal");
            for (Fluxo f : lFluxo) {
                grf.adicionar(mapMes.get(f.getMes()) + "/" + f.getAno(),
                        NumeroUtils.arredondarValorMonetario(f.getValFluxo()));
            }
            serializarJson(new SerializacaoJson("grafico", grf).incluirAtributo("listaLabel", "listaDado"));

        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("inclusao/alteracao do pedido", e);
        }

    }
}
