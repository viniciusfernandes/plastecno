package br.com.plastecno.vendas.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class ItemAguardandoCompraController extends AbstractController {

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private RepresentadaService representadaService;

    @Servico
    private PedidoService pedidoService;

    public ItemAguardandoCompraController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("itemAguardandoCompra/pdf")
    public Download downloadPedidoPDF(Integer idPedido) {
        return forwardTo(PedidoController.class).downloadPedidoPDF(idPedido, TipoPedido.REVENDA);
    }

    @Post("itemAguardandoCompra/item/compra")
    public void encomendarItemPedido(Date dataInicial, Date dataFinal, Integer idRepresentadaFornecedora,
            Cliente cliente, List<Integer> listaIdItem) {
        try {
            final Set<Integer> ids = listaIdItem == null ? new HashSet<Integer>() : new HashSet<Integer>(listaIdItem);
            Integer idPedidoCompra = pedidoService
                    .comprarItemPedido(getCodigoUsuario(), idRepresentadaFornecedora, ids);
            addAtributo("dataInicial", formatarData(dataInicial));
            addAtributo("dataFinal", formatarData(dataFinal));
            addAtributo("cliente", cliente);

            redirecTo(PedidoController.class).pesquisarPedidoById(idPedidoCompra, TipoPedido.COMPRA);
        } catch (BusinessException e) {
            addAtributo("permanecerTopo", true);
            gerarListaMensagemErro(e);
            pesquisarItemAguardandoCompra(dataInicial, dataFinal, cliente);
        }
    }

    @Post("itemAguardandoCompra/empacotamento")
    public void enviarEncomendaEmpacotamento(Date dataInicial, Date dataFinal, Integer idPedido, Cliente cliente) {
        boolean empacotamentoOK;
        try {
            empacotamentoOK = pedidoService.enviarRevendaAguardandoEncomendaEmpacotamento(idPedido);
            if (empacotamentoOK) {
                gerarMensagemSucesso("O pedido No. " + idPedido + " foi enviado para o empacotamento com sucesso");
            } else {
                gerarListaMensagemErro("O pedido No. " + idPedido
                        + " não pode ser enviado para o empacotamento pois algum de seus itens no existe no estoque");
            }
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            addAtributo("permanecerTopo", true);
        }
        redirecTo(this.getClass()).pesquisarItemAguardandoCompra(dataInicial, dataFinal, cliente);
    }

    @Get("itemAguardandoCompra")
    public void itemAguardandoCompraHome() {
        addAtributo("listaFornecedor", this.representadaService.pesquisarFornecedorAtivo());
    }

    @Get("itemAguardandoCompra/item/listagem")
    public void pesquisarItemAguardandoCompra(Date dataInicial, Date dataFinal, Cliente cliente) {
        try {
            Periodo periodo = Periodo.gerarPeriodo(dataInicial, dataFinal);
            RelatorioWrapper<Integer, ItemPedido> relatorio = relatorioService.gerarRelatorioItemAguardandoCompra(
                    cliente.getId(), periodo);

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
        addAtributo("cliente", cliente);
    }
}
