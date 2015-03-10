package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.vendas.controller.anotacao.Servico;

@Resource
public class RevendaEncomendadaController extends AbstractController {
    @Servico
    private RepresentadaService representadaService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private EstoqueService estoqueService;

    public RevendaEncomendadaController(Result result) {
        super(result);
    }

    @Get("revendaEncomendada/pdf")
    public Download downloadPedidoPDF(Integer idPedido) {
        return redirecTo(PedidoController.class).downloadPedidoPDF(idPedido, TipoPedido.REVENDA);
    }

    @Get("revendaEncomendada/listagem")
    public void pesquisarRevendaEncomendada(Date dataInicial, Date dataFinal, Integer idRepresentada) {

        try {
            Periodo periodo = new Periodo(dataInicial, dataFinal);
            RelatorioWrapper<Integer, ItemPedido> relatorio = relatorioService.gerarRelatorioRevendaEncomendada(
                    idRepresentada, periodo);

            addAtributo("relatorio", relatorio);
            if (contemAtributo("permanecerTopo")) {
                irTopoPagina();
            } else {
                irRodapePagina();
            }
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }

        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("idRepresentadaSelecionada", idRepresentada);
        addAtributo("listaRepresentada", representadaService.pesquisarRepresentada());
    }

    @Post("revendaEncomendada/edicao")
    public void pesquisarRevendaEncomendadaById(Integer idPedido, Date dataInicial, Date dataFinal,
            Integer idRepresentada) {
        redirecTo(PedidoController.class).pesquisarPedidoById(idPedido, TipoPedido.REVENDA);
    }

    @Get("revendaEncomendada")
    public void revendaEncomendadaHome() {
        // Pode ser que essas datas ja tenham sido preenchidas em outra
        // navegacao pois esse metodo eh reaproveitado.
        configurarFiltroPediodoMensal();
        addAtributo("listaRepresentada", representadaService.pesquisarRepresentada());
    }

    @Post("revendaEncomendada/empacotamento")
    public void enviarPedidoEmpacotamento(Integer idPedido, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        try {
            estoqueService.enviarPedidoEmpacotamento(idPedido);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        addAtributo("permanecerTopo", true);
        redirecTo(this.getClass()).pesquisarRevendaEncomendada(dataInicial, dataFinal, idRepresentada);
    }
}
