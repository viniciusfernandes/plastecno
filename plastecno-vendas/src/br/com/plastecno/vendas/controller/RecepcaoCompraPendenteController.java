package br.com.plastecno.vendas.controller;

import java.util.Calendar;
import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RecepcaoCompraPendenteController extends AbstractController {
    @Servico
    private RepresentadaService representadaService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private PedidoService pedidoService;

    @Servico
    private EstoqueService estoqueService;

    public RecepcaoCompraPendenteController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("compra/pdf")
    public Download downloadPedidoPDF(Integer idPedido) {
        return redirecTo(PedidoController.class).downloadPedidoPDF(idPedido, TipoPedido.COMPRA);
    }

    @Post("compra/item/edicao")
    public void inserirItemPedido(ItemPedido itemPedido, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        try {
            pedidoService.inserirItemPedido(itemPedido);
            gerarMensagemSucesso("O item de compra foi alterado com sucesso. Essas alterações já podem ser incluidas no estoque.");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        redirecTo(this.getClass()).pesquisarCompraPendente(dataInicial, dataFinal, idRepresentada);
    }

    @Get("compra/recepcao/listagem")
    public void pesquisarCompraPendente(Date dataInicial, Date dataFinal, Integer idRepresentada) {

        try {
            Periodo periodo = new Periodo(dataInicial, dataFinal);
            RelatorioWrapper<Integer, ItemPedido> relatorio = relatorioService.gerarRelatorioCompraPendente(
                    idRepresentada, periodo);

            addAtributo("relatorio", relatorio);
            irRodapePagina();
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }

        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("idRepresentadaSelecionada", idRepresentada);
        addAtributo("listaRepresentada", representadaService.pesquisar());
    }

    @Get("compra/item")
    public void pesquisarItemCompraById(Integer idItemPedido, Date dataInicial, Date dataFinal, Integer idRepresentada) {
        ItemPedido itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);
        if (itemPedido == null) {
            gerarListaMensagemErro("Item de compra não existe no sistema");
        } else {
            addAtributo("itemPedido", itemPedido);
        }
        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("idRepresentadaSelecionada", idRepresentada);
        redirecTo(this.getClass()).pesquisarCompraPendente(new Date(), new Date(), idRepresentada);
    }

    @Get("compra/recepcao")
    public void recepcaoCompraPendenteHome() {
        // Pode ser que essas datas ja tenham sido preenchidas em outra
        // navegacao pois esse metodo eh reaproveitado.
        if (!contemAtributo("dataInicial") && !contemAtributo("dataFinal")) {
            Calendar dataInicial = Calendar.getInstance();
            dataInicial.set(Calendar.DAY_OF_MONTH, 1);

            addAtributo("dataInicial", StringUtils.formatarData(dataInicial.getTime()));
            addAtributo("dataFinal", StringUtils.formatarData(new Date()));

        }
        addAtributo("listaRepresentada", representadaService.pesquisar());
        addAtributo("listaFormaMaterial", FormaMaterial.values());
    }

    @Post("compra/item/recepcao")
    public void recepcionarItemCompra(Date dataInicial, Date dataFinal, Integer idRepresentada, Integer idItemPedido) {
        estoqueService.inserirItemEstoque(idItemPedido);
        redirecTo(this.getClass()).pesquisarCompraPendente(dataInicial, dataFinal, idRepresentada);
    }

    @Post("compra/item/remocao")
    public void removerItemCompra(Date dataInicial, Date dataFinal, Integer idRepresentada, Integer idItemPedido) {
        try {
            pedidoService.removerItemPedido(idItemPedido);
        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
        }
        redirecTo(this.getClass()).pesquisarCompraPendente(dataInicial, dataFinal, idRepresentada);
    }
}
