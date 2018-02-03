package br.com.svr.vendas.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.ClienteService;
import br.com.svr.service.EstoqueService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.relatorio.RelatorioService;
import br.com.svr.service.wrapper.RelatorioWrapper;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class EmpacotamentoRevendaController extends AbstractController {
    @Servico
    private ClienteService clienteService;

    @Servico
    private EstoqueService estoqueService;

    @Servico
    private PedidoService pedidoService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private RepresentadaService representadaService;

    public EmpacotamentoRevendaController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    private void carregarRelatorioEmpacotamento(Integer idCliente) {
        RelatorioWrapper<Integer, ItemPedido> relatorio = relatorioService
                .gerarRelatorioRevendaEmpacotamento(idCliente);

        addAtributo("relatorio", relatorio);
    }

    @Get("empacotamento")
    public void empacotamentoRevendaHome() {
        // Pode ser que essas datas ja tenham sido preenchidas em outra
        // navegacao pois esse metodo eh reaproveitado.
        configurarFiltroPediodoMensal();

        // Carregando os dados dos pedidos para empacotamento para que haja um
        // acesso mais rapido a informacao
        if (!contemAtributo("relatorio")) {
            carregarRelatorioEmpacotamento(null);
        }
    }

    @Post("empacotamento/itens/inclusao")
    public void empacotarItem(List<Integer> listaIdPedido, Date dataInicial, Date dataFinal, Integer idCliente) {
        if (listaIdPedido != null) {
            estoqueService.empacotarPedido(listaIdPedido);
            gerarMensagemSucesso("Pedido(s) No. " + Arrays.deepToString(listaIdPedido.toArray())
                    + " empacotado(s) com sucesso");

        } else {
            gerarMensagemAlerta("Não há pedidos selecionados para serem empacotados. Selecione algum item novamente.");

        }
        pesquisarRevendaEmpacotamento(idCliente);
    }

    @Post("empacotamento/inclusaodadosnf")
    public void inserirDadosNotaFiscal(Pedido pedido, Integer idCliente) {
        pedidoService.inserirDadosNotaFiscal(pedido);
        pesquisarRevendaEmpacotamento(idCliente);
    }

    @Get("empacotamento/item/pesquisadadosnf")
    public void pesquisarDadosNotaFiscal(Integer idItemPedido, Integer idCliente) {
        Pedido pedido = pedidoService.pesquisarDadosNotaFiscalByIdItemPedido(idItemPedido);
        if (pedido != null) {
            formatarPedido(pedido);
            addAtributo("pedido", pedido);
        }
        pesquisarRevendaEmpacotamento(idCliente);
        irTopoPagina();
    }

    @Post("empacotamento/revenda/listagem")
    public void pesquisarRevendaEmpacotamento(Integer idCliente) {
        carregarRelatorioEmpacotamento(idCliente);
        if (contemAtributo("permanecerTopo")) {
            irTopoPagina();
        } else {
            irRodapePagina();
        }

        addAtributo("cliente", clienteService.pesquisarClienteResumidoById(idCliente));
    }

    @Post("empacotamento/item/reencomenda")
    public void reencomendarItemPedido(Integer idItemPedido, Integer idCliente, Date dataInicial, Date dataFinal) {
        try {
            pedidoService.reencomendarItemPedido(idItemPedido);
            gerarMensagemSucesso("O item foi enviado para ser reencomendado pelo setor de compras");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        addAtributo("permanecerTopo", true);
        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        pesquisarRevendaEmpacotamento(idCliente);
    }

}
