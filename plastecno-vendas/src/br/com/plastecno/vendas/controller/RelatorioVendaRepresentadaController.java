package br.com.plastecno.vendas.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;
import br.com.plastecno.vendas.relatorio.conversor.GeradorRelatorioPDF;

@Resource
public class RelatorioVendaRepresentadaController extends AbstractController {

    @Servico
    private PedidoService pedidoService;

    @Servico
    private RepresentadaService representadaService;

    @Servico
    private RelatorioService relatorioService;

    private GeradorRelatorioPDF geradorRelatorio;
    private String diretorioTemplateRelatorio;

    public RelatorioVendaRepresentadaController(Result result, UsuarioInfo usuarioInfo,
            GeradorRelatorioPDF gerador, HttpServletRequest request) {
        super(result, usuarioInfo);
        this.diretorioTemplateRelatorio = request.getServletContext().getRealPath("/templates");
        this.geradorRelatorio = gerador;
    }

    @Get("relatorio/venda/representada")
    public void relatorioVendaRepresentadaHome() {
        addAtributo("listaRepresentada", this.representadaService.pesquisar());
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
            listaPedido = this.pedidoService.pesquisarEnviadosByPeriodoERepresentada(new Periodo(
                    dataInicial, dataFinal), idRepresentada);
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

            geradorRelatorio.addAtributo("representada", representada);
            geradorRelatorio.addAtributo("listaPedido", listaPedido);
            geradorRelatorio.addAtributo("totalVendido", NumeroUtils.formatarValorMonetario(totalVendido));
            geradorRelatorio.addAtributo("valorComissao",
                    NumeroUtils.formatarValorMonetario(totalVendido * representada.getComissao()));
            geradorRelatorio.addAtributo("dataInicial", dataInicialFormatada);
            geradorRelatorio.addAtributo("dataFinal", dataFinalFormatada);
            geradorRelatorio.processar(new File(diretorioTemplateRelatorio + "/relatorioPedido.html"));

            return this.gerarDownload(geradorRelatorio.gerarPDF(), "Vendas " + representada.getNomeFantasia() + " "
                    + dataInicialFormatada + " a " + dataFinalFormatada + ".pdf");

        } catch (Exception e) {
            gerarLogErro("geracao do relatorio de pedidos por representada", e);
            return null;
        }
    }
}
