package br.com.plastecno.vendas.controller;

import java.util.Date;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioEntregaController extends AbstractController {

    @Servico
    private RelatorioService relatorioService;

    public RelatorioEntregaController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/entrega/listagem")
    public void pesquisarEntregas(Date dataInicial, Date dataFinal) {
        try {
            List<Pedido> listaPedido = this.relatorioService.pesquisarEntregas(new Periodo(dataInicial, dataFinal));
            for (Pedido pedido : listaPedido) {
                this.formatarPedido(pedido);
            }
            addAtributo("listaPedido", listaPedido);
            addAtributo(
                    "tituloRelatorio",
                    "Relátorio de Acompanhamento de Entregas de " + this.formatarData(dataInicial) + " à "
                            + this.formatarData(dataFinal));
            addAtributo("relatorioGerado", true);
        } catch (InformacaoInvalidaException e) {
            this.gerarListaMensagemErro(e);
        }

        addAtributo("dataInicial", this.formatarData(dataInicial));
        addAtributo("dataFinal", this.formatarData(dataFinal));
        this.irPaginaHome();
    }

    @Get("relatorio/entrega")
    public void relatorioEntregaHome() {
    }

}
