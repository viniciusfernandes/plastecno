package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.EstoqueService;
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
public class EmpacotamentoRevendaController extends AbstractController {
    @Servico
    private RepresentadaService representadaService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private PedidoService pedidoService;

    @Servico
    private EstoqueService estoqueService;

    @Servico
    private ClienteService clienteService;

    public EmpacotamentoRevendaController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("empacotamento/pedido/pdf")
    public Download downloadPedidoPDF(Integer idPedido) {
        return redirecTo(PedidoController.class).downloadPedidoPDF(idPedido, TipoPedido.REVENDA);
    }

    @Get("empacotamento")
    public void empacotamentoRevendaHome() {
        // Pode ser que essas datas ja tenham sido preenchidas em outra
        // navegacao pois esse metodo eh reaproveitado.
        configurarFiltroPediodoMensal();
    }

    @Post("empacotamento/item/inclusao")
    public void empacotarItem(Integer idItemPedido, Date dataInicial, Date dataFinal, Cliente cliente) {
        estoqueService.empacotarItemPedido(idItemPedido);
        pesquisarRevendaEmpacotamento(dataInicial, dataFinal, cliente);
    }

    @Get("empacotamento/revenda/listagem")
    public void pesquisarRevendaEmpacotamento(Date dataInicial, Date dataFinal, Cliente cliente) {
        try {
            Periodo periodo = new Periodo(dataInicial, dataFinal);
            RelatorioWrapper<Integer, ItemPedido> relatorio = relatorioService.gerarRelatorioRevendaEmpacotamento(
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
