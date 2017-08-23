package br.com.plastecno.vendas.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;
import br.com.plastecno.vendas.relatorio.conversor.GeradorRelatorioPDF;

public class AbstractPedidoController extends AbstractController {
    
    @Servico
    private ClienteService clienteService;

    @Servico
    private MaterialService materialService;

    @Servico
    private PedidoService pedidoService;

    @Servico
    private RepresentadaService representadaService;

    @Servico
    private UsuarioService usuarioService;

    public AbstractPedidoController(Result result, UsuarioInfo usuarioInfo, GeradorRelatorioPDF geradorRelatorioPDF,
            HttpServletRequest request) {
        super(result, usuarioInfo, geradorRelatorioPDF, request);
    }

    void formatarDocumento(Cliente cliente) {
        cliente.setCnpj(formatarCNPJ(cliente.getCnpj()));
        cliente.setCpf(formatarCPF(cliente.getCpf()));
        cliente.setInscricaoEstadual(formatarInscricaoEstadual(cliente.getInscricaoEstadual()));
    }

    void formatarItemPedido(ItemPedido item) {
        item.setAliquotaICMSFormatado(NumeroUtils.formatarPercentualInteiro(item.getAliquotaICMS()));
        item.setAliquotaIPIFormatado(NumeroUtils.formatarPercentualInteiro(item.getAliquotaIPI()));
        item.setPrecoUnidadeFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoUnidade()));
        item.setPrecoUnidadeIPIFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoUnidadeIPI()));
        item.setPrecoVendaFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoVenda()));
        item.setPrecoItemFormatado(NumeroUtils.formatarValorMonetario(item.calcularPrecoItem()));
        item.setMedidaExternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaExterna()));
        item.setMedidaInternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaInterna()));
        item.setComprimentoFormatado(NumeroUtils.formatarValorMonetario(item.getComprimento()));
        item.setValorPedidoFormatado(NumeroUtils.formatarValorMonetario(item.getValorPedido()));
        item.setValorPedidoIPIFormatado(NumeroUtils.formatarValorMonetario(item.getValorPedidoIPI()));
        item.setValorICMSFormatado(String.valueOf(NumeroUtils.arredondarValorMonetario(item.getValorICMS())));
        item.setValorIPIFormatado(String.valueOf(NumeroUtils.arredondarValorMonetario(item.getPrecoUnidadeIPI())));

        if (item.contemAliquotaComissao()) {
            item.setAliquotaComissaoFormatado(NumeroUtils.formatarPercentualInteiro(item.getAliquotaComissao()));
        }
    }

    void formatarItemPedido(List<ItemPedido> itens) {
        for (ItemPedido item : itens) {
            formatarItemPedido(item);
        }
    }

    boolean isPedidoDesabilitado(Pedido pedido) {
        if (pedido == null || isAcessoPermitido(TipoAcesso.ADMINISTRACAO, TipoAcesso.GERENCIA_VENDAS)) {
            return false;
        } else {
            SituacaoPedido situacao = pedido.getSituacaoPedido();
            boolean isCompraFinalizada = pedido.isCompra() && SituacaoPedido.COMPRA_RECEBIDA.equals(situacao);
            boolean isVendaFinalizada = pedido.isVenda()
                    && (SituacaoPedido.ENVIADO.equals(situacao)
                            || SituacaoPedido.ITEM_AGUARDANDO_COMPRA.equals(situacao)
                            || SituacaoPedido.REVENDA_AGUARDANDO_EMPACOTAMENTO.equals(situacao)
                            || SituacaoPedido.EMPACOTADO.equals(situacao)
                            || SituacaoPedido.COMPRA_ANDAMENTO.equals(situacao) || SituacaoPedido.ITEM_AGUARDANDO_MATERIAL
                                .equals(situacao));
            return SituacaoPedido.CANCELADO.equals(situacao) || isCompraFinalizada || isVendaFinalizada;
        }
    }
}
