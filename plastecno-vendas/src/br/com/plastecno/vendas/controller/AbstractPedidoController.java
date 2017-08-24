package br.com.plastecno.vendas.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.LogradouroPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.login.UsuarioInfo;
import br.com.plastecno.vendas.relatorio.conversor.GeradorRelatorioPDF;

public class AbstractPedidoController extends AbstractController {
    class PedidoPDFWrapper {
        private final byte[] arquivoPDF;
        private final Pedido pedido;

        public PedidoPDFWrapper(Pedido pedido, byte[] arquivoPDF) {
            this.pedido = pedido;
            this.arquivoPDF = arquivoPDF;
        }

        public byte[] getArquivoPDF() {
            return arquivoPDF;
        }

        public Pedido getPedido() {
            return pedido;
        }
    }

    private ClienteService clienteService;
    private PedidoService pedidoService;
    private TransportadoraService transportadoraService;

    public AbstractPedidoController(Result result, UsuarioInfo usuarioInfo, GeradorRelatorioPDF geradorRelatorioPDF,
            HttpServletRequest request) {
        super(result, usuarioInfo, geradorRelatorioPDF, request);
    }

    Download downloadPDFPedido(Integer idPedido, TipoPedido tipoPedido) {
        try {
            PedidoPDFWrapper wrapper = gerarPDF(idPedido, tipoPedido);
            final Pedido pedido = wrapper.getPedido();

            final StringBuilder titulo = new StringBuilder(pedido.isOrcamento() ? "Orcamento " : "Pedido ")
                    .append("No. ").append(idPedido).append(" - ").append(pedido.getCliente().getNomeFantasia())
                    .append(".pdf");

            return gerarDownloadPDF(wrapper.getArquivoPDF(), titulo.toString());
        } catch (BusinessException e) {
            gerarMensagemAlerta(e.getMensagemEmpilhada());
            // Estamos retornando null porque no caso de falhas nao devemos
            // efetuar o download do arquivo
            return null;
        } catch (Exception e) {
            gerarLogErro("geração do relatório de pedido", e);
            // Estamos retornando null porque no caso de falhas nao devemos
            // efetuar o download do arquivo
            return null;
        }
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

    PedidoPDFWrapper gerarPDF(Integer idPedido, TipoPedido tipoPedido) throws BusinessException {
        // Se vendedor que pesquisa o pedido nao estiver associado ao cliente
        // nao devemos exibi-lo para o vendedor que pesquisa por questao de
        // sigilo.
        if (!isVisulizacaoPermitida(idPedido, tipoPedido)) {
            throw new BusinessException("O usuário não tem permissão de acesso ao pedido.");
        } else {
            Pedido pedido = pedidoService.pesquisarPedidoById(idPedido, TipoPedido.COMPRA.equals(tipoPedido));
            if (pedido == null) {
                throw new BusinessException("Não é possível gerar o PDF do pedido de numero " + idPedido
                        + " pois não existe no sistema.");
            }

            final List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);

            formatarItemPedido(listaItem);
            formatarPedido(pedido);
            formatarDocumento(pedido.getCliente());
            formatarDocumento(pedido.getRepresentada());

            String template = pedido.isOrcamento() ? "orcamento.html" : "pedido.html";
            String tipo = pedido.isVenda() ? "Venda" : "Compra";
            String titulo = pedido.isOrcamento() ? "Orçamento de " + tipo : "Pedido de " + tipo;
            if (pedido.isOrcamento()) {
                titulo += " No. " + pedido.getId() + " - " + StringUtils.formatarData(pedido.getDataEnvio());
                pedido.formatarContato();
                pedido.setListaLogradouro(pedidoService.pesquisarLogradouro(idPedido, TipoLogradouro.FATURAMENTO));
            } else {
                pedido.setListaLogradouro(pedidoService.pesquisarLogradouro(idPedido));
                Transportadora transportadora = pedido.getTransportadora();
                if (transportadora != null) {
                    transportadora.setListaContato(transportadoraService.pesquisarContato(transportadora.getId()));
                    transportadora.setLogradouro(transportadoraService.pesquisarLogradorouro(transportadora.getId()));
                }

                transportadora = pedido.getTransportadoraRedespacho();
                if (transportadora != null) {
                    transportadora.setListaContato(transportadoraService.pesquisarContato(transportadora.getId()));
                    transportadora.setLogradouro(transportadoraService.pesquisarLogradorouro(transportadora.getId()));
                }
                final LogradouroPedido logradouroEntrega = recuperarLogradouro(pedido, TipoLogradouro.ENTREGA);
                final LogradouroPedido logradouroCobranca = recuperarLogradouro(pedido, TipoLogradouro.COBRANCA);
                addAtributoPDF("logradouroEntrega", logradouroEntrega != null ? logradouroEntrega.getDescricao() : "");
                addAtributoPDF("logradouroCobranca", logradouroCobranca != null ? logradouroCobranca.getDescricao()
                        : "");
            }

            LogradouroPedido logradouroFaturamento = recuperarLogradouro(pedido, TipoLogradouro.FATURAMENTO);

            addAtributoPDF("tipoRelacionamento", pedido.isVenda() ? "Represent." : "Forneced.");
            addAtributoPDF("tipoProprietario", pedido.isVenda() ? "Vendedor" : "Comprador");
            addAtributoPDF("titulo", titulo);
            addAtributoPDF("tipoPedido", tipo);
            addAtributoPDF("pedido", pedido);
            addAtributoPDF("logradouroFaturamento",
                    logradouroFaturamento != null ? logradouroFaturamento.getDescricao() : "");

            addAtributoPDF("listaItem", listaItem);

            processarPDF(template);
            // Alterando as medidas do PDF gerado.
            int alt = 0;
            int larg = 0;
            int tot = listaItem.size();

            if (pedido.isOrcamento()) {
                larg = 550;
                alt = 260;
            } else {
                larg = 550;
                alt = 650;
            }
            if (tot >= 5) {
                tot = 5;
            }
            // Aqui estamos adicionando um valor de 15 pixels para cada item do
            // pedido, pois assim a altura do PDF ficara de acordo com o total
            // de
            // itens. Note que o numero total foi limitado pois a a partir do
            // limite
            // o pdf devera ser pagina, caso contrario podemos ter um pdf com 50
            // itens na mesma pagino e isso complica a impressao do arquivo.
            alt += 15 * tot;
            return new PedidoPDFWrapper(pedido, gerarPDF(larg, alt));
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

    boolean isVisulizacaoClientePermitida(Integer idCliente) {
        // Aqui temos que verificar se o usuario eh o vendedor associado ao
        // cliente.
        boolean isGestor = isAcessoPermitido(TipoAcesso.ADMINISTRACAO, TipoAcesso.GERENCIA_VENDAS);
        if (isGestor) {
            return true;
        }
        Integer idVend = clienteService.pesquisarIdVendedorByIdCliente(idCliente);
        Integer idUsu = getCodigoUsuario();
        final boolean isAcessoVendaPermitido = (idVend == null || idUsu.equals(idVend))
                && (isAcessoPermitido(TipoAcesso.CADASTRO_PEDIDO_VENDAS));
        return isAcessoVendaPermitido;
    }

    boolean isVisulizacaoPermitida(Integer idPedido, TipoPedido tipoPedido) {
        boolean isAdm = isAcessoPermitido(TipoAcesso.ADMINISTRACAO);
        if (isAdm) {
            return true;
        }
        boolean isCompra = TipoPedido.COMPRA.equals(tipoPedido);
        boolean isVenda = !isCompra;
        final boolean isAcessoCompraPermitida = isCompra && isAcessoPermitido(TipoAcesso.CADASTRO_PEDIDO_COMPRA);
        if (isAcessoCompraPermitida) {
            return true;
        }
        Integer idCli = pedidoService.pesquisarIdClienteByIdPedido(idPedido);

        // Aqui temos que verificar se o usuario eh o vendedor associado ao
        // cliente.
        final boolean isAcessoVendaPermitido = isVenda && isVisulizacaoClientePermitida(idCli);
        return isAcessoVendaPermitido;
    }

    LogradouroPedido recuperarLogradouro(Pedido p, TipoLogradouro t) {
        if (p.getListaLogradouro() == null || p.getListaLogradouro().isEmpty()) {
            return null;
        }
        for (LogradouroPedido l : p.getListaLogradouro()) {
            if (t.equals(l.getTipoLogradouro())) {
                return l;
            }
        }
        return null;
    }

    public void setClienteService(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public void setTransportadoraService(TransportadoraService transportadoraService) {
        this.transportadoraService = transportadoraService;
    }
}
