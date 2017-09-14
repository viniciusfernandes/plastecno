package br.com.plastecno.vendas.controller;

import java.util.Date;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.FaturamentoService;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Fluxo;
import br.com.plastecno.service.wrapper.FluxoCaixa;
import br.com.plastecno.service.wrapper.Periodo;
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

    @Get("fluxocaixa/grafico/bar")
    public void gerarGraficoBar(Date dataInicial, Date dataFinal) {
        try {
            FluxoCaixa fluxoCaixa = faturamentoService.gerarFluxoFaixaByPeriodo(new Periodo(dataInicial, dataFinal));
            List<Fluxo> lFluxo = fluxoCaixa.gerarFluxoByMes();
            GraficoBar2D grf = new GraficoBar2D();
            for (Fluxo f : lFluxo) {
                System.out.println(f);
                grf.adicionar(f.getMes() + "/" + f.getAno(), f.getValFluxo());
            }
            serializarJson(new SerializacaoJson("grafico", grf).incluirAtributo("listaLabel", "listaDado"));

        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("inclusao/alteracao do pedido", e);
        }

    }
}
