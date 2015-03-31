package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.SituacaoPedido;
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
    private EstoqueService estoqueService;

    @Servico
    private PedidoService pedidoService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private RepresentadaService representadaService;

    public RevendaEncomendadaController(Result result) {
        super(result);
    }

    @Get("revendaEncomendada/pdf")
    public Download downloadPedidoPDF(Integer idPedido) {
        return redirecTo(PedidoController.class).downloadPedidoPDF(idPedido, TipoPedido.REVENDA);
    }

    @Post("revendaEncomendada/empacotamento")
    public void enviarPedidoEmpacotamento(Integer idPedido, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        try {
            pedidoService.enviarRevendaEncomendadaEmpacotamento(idPedido);
            SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
            if (SituacaoPedido.REVENDA_ENCOMENDADA.equals(situacaoPedido)) {
                gerarMensagemSucesso("O pedido No. " + idPedido
                        + " ainda possui alguns itens que não estão no estoque. Verifique com o setor de compras.");
            } else if (SituacaoPedido.REVENDA_AGUARDANDO_EMPACOTAMENTO.equals(situacaoPedido)) {
                gerarMensagemSucesso("O pedido No. " + idPedido + " foi encaminhado para o empacotamento.");
            }
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        addAtributo("permanecerTopo", true);
        redirecTo(this.getClass()).pesquisarRevendaEncomendada(dataInicial, dataFinal, idRepresentada);
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
}
