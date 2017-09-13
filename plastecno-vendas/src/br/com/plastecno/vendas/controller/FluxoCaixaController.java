package br.com.plastecno.vendas.controller;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.FaturamentoService;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Fluxo;
import br.com.plastecno.service.wrapper.FluxoCaixa;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.Ponto2D;
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

    }

    @Get("fluxocaixa/grafico")
    public void gerarGrafico() {
        Calendar inicio = Calendar.getInstance();
        inicio.setTime(new Date());
        inicio.set(Calendar.YEAR, 2016);

        Calendar fim = Calendar.getInstance();
        fim.setTime(new Date());
        fim.set(Calendar.YEAR, 2019);

        try {
            FluxoCaixa fluxoCaixa = faturamentoService.gerarFluxoFaixaByPeriodo(new Periodo(inicio.getTime(), fim
                    .getTime()));
            List<Fluxo> lFluxo = fluxoCaixa.gerarFluxoByMes();
            List<Ponto2D> dados = new ArrayList<>(100);

            for (Fluxo f : lFluxo) {
                System.out.println(f);
                dados.add(new Ponto2D(null, f.getValFluxo()));
            }
            serializarJson(new SerializacaoJson("dados", dados));

        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        } catch (Exception e) {
            gerarLogErro("Falha na geracao do fluxo de caixa", e);
        }
    }
}
