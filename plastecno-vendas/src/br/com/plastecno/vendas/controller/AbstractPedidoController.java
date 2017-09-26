package br.com.plastecno.vendas.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.caelum.vraptor.validator.Message;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.LogradouroPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.GrupoWrapper;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.json.ItemPedidoJson;
import br.com.plastecno.vendas.json.PedidoJson;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;
import br.com.plastecno.vendas.relatorio.conversor.GeradorRelatorioPDF;
import br.com.plastecno.vendas.relatorio.conversor.exception.ConversaoHTML2PDFException;

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

    private RelatorioService relatorioService;

    private RepresentadaService representadaService;

    private TransportadoraService transportadoraService;

    private UsuarioService usuarioService;

    private Validator validator;

    public AbstractPedidoController(Result result, UsuarioInfo usuarioInfo, GeradorRelatorioPDF geradorRelatorioPDF,
            HttpServletRequest request) {
        super(result, usuarioInfo, geradorRelatorioPDF, request);
        verificarPermissaoAcesso("acessoCadastroPedidoPermitido", TipoAcesso.CADASTRO_PEDIDO_VENDAS);
        verificarPermissaoAcesso("acessoDadosNotaFiscalPermitido", TipoAcesso.ADMINISTRACAO,
                TipoAcesso.CADASTRO_PEDIDO_COMPRA);
    }

    public AbstractPedidoController(Result result, UsuarioInfo usuarioInfo, GeradorRelatorioPDF geradorRelatorioPDF,
            HttpServletRequest request, Validator validator) {
        this(result, usuarioInfo, geradorRelatorioPDF, request);
        this.validator = validator;
    }

    void configurarTipoPedido(TipoPedido tipoPedido) {
        if (TipoPedido.COMPRA.equals(tipoPedido)) {
            addAtributo("tipoPedido", tipoPedido);
            if (!contemAtributo("proprietario")) {
                addAtributo("proprietario", usuarioService.pesquisarById(getCodigoUsuario()));
            }
        }
    }

    Download downloadPDFPedido(Integer idPedido, TipoPedido tipoPedido) {
        try {
            PedidoPDFWrapper wrapper = gerarPDF(idPedido, tipoPedido);
            final Pedido pedido = wrapper.getPedido();

            final StringBuilder titulo = new StringBuilder(pedido.isOrcamento() ? "Orcamento " : "Pedido ")
                    .append("No. ").append(idPedido).append(" - ").append(pedido.getCliente().getNomeFantasia())
                    .append(".pdf");

            return gerarDownloadPDF(wrapper.getArquivoPDF(), titulo.toString());
        } catch (ConversaoHTML2PDFException e) {
            gerarLogErro("conversão do relatório de pedido", e);
            return null;
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
        item.setValorTotalPedidoSemFreteFormatado(NumeroUtils.formatarValorMonetario(item.getValorTotalPedidoSemFrete()));
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

            formatarPedido(pedido);
            formatarItemPedido(listaItem);
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
                alt = 310;
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

    /*
     * Metodo dedicado a gerar relatorio paginado dos itens dos pedidos no caso
     * em que o usuario seja um administrador, sendo assim, ele podera consultar
     * os pedidos de todos os vendedores
     */
    private RelatorioWrapper<Pedido, ItemPedido> gerarRelatorioPaginadoItemPedido(Integer idCliente,
            Integer idVendedor, Integer idFornecedor, boolean isOrcamento, boolean isCompra, Integer paginaSelecionada,
            ItemPedido itemVendido) {
        final int indiceRegistroInicial = calcularIndiceRegistroInicial(paginaSelecionada);

        // Essa variavel eh utilizada para decidirmos se queremos recuperar
        // todos os pedidos de um determinado cliente independentemente do
        // vendedor. Essa acao sera disparada por qualquer um que seja
        // adiministrador do sistema, podendo ser um outro vendedor ou nao.
        boolean pesquisarTodos = isAcessoPermitido(TipoAcesso.ADMINISTRACAO);
        RelatorioWrapper<Pedido, ItemPedido> relatorio = relatorioService
                .gerarRelatorioItemPedidoByIdClienteIdVendedorIdFornecedor(idCliente, pesquisarTodos ? null
                        : idVendedor, idFornecedor, isOrcamento, isCompra, indiceRegistroInicial,
                        getNumerRegistrosPorPagina(), itemVendido);

        for (GrupoWrapper<Pedido, ItemPedido> grupo : relatorio.getListaGrupo()) {
            formatarPedido(grupo.getId());

            for (ItemPedido itemPedido : grupo.getListaElemento()) {
                formatarItemPedido(itemPedido);
            }
        }

        return relatorio;
    }

    void inserirItemPedido(Integer numeroPedido, ItemPedido itemPedido, Double aliquotaIPI) {
        // Remover adiante
        verificarConversao();
        try {
            if (itemPedido.getMaterial() != null && itemPedido.getMaterial().getId() == null) {
                itemPedido.setMaterial(null);
            }

            if (aliquotaIPI != null) {
                itemPedido.setAliquotaIPI(NumeroUtils.gerarAliquota(aliquotaIPI));
            }

            itemPedido.setAliquotaComissao(itemPedido.getAliquotaComissao() == null
                    || itemPedido.getAliquotaComissao() == 0d ? null : NumeroUtils.gerarAliquota(itemPedido
                    .getAliquotaComissao()));

            itemPedido.setAliquotaICMS(NumeroUtils.gerarAliquota(itemPedido.getAliquotaICMS()));

            final Integer idItemPedido = pedidoService.inserirItemPedido(numeroPedido, itemPedido);
            itemPedido.setId(idItemPedido);

            Double[] valorPedido = pedidoService.pesquisarValorPedidoByItemPedido(idItemPedido);
            itemPedido.setValorPedido(valorPedido[0]);
            itemPedido.setValorPedidoIPI(valorPedido[1]);
            itemPedido.setValorTotalPedidoSemFrete(valorPedido[1] - (valorPedido[2] == null ? 0 : valorPedido[2]));

            formatarItemPedido(itemPedido);
            formatarPedido(itemPedido.getPedido());

            serializarJson(new SerializacaoJson("itemPedido", new ItemPedidoJson(itemPedido)));
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("inclusao/alteracao do item do pedido " + numeroPedido, e);
        }
    }

    void inserirPedido(Pedido pedido, Contato contato, boolean orcamento) {
        // Remover adiante
        verificarConversao();

        if (hasAtributo(contato)) {
            pedido.setContato(contato);
        }

        if (pedido.getSituacaoPedido() == null && !orcamento) {
            pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
        } else if (pedido.getSituacaoPedido() == null && orcamento) {
            pedido.setSituacaoPedido(SituacaoPedido.ORCAMENTO_DIGITACAO);
        }

        if (pedido.getTransportadora() != null && pedido.getTransportadora().getId() == null) {
            pedido.setTransportadora(null);
        }

        if (pedido.getTransportadoraRedespacho() != null && pedido.getTransportadoraRedespacho().getId() == null) {
            pedido.setTransportadoraRedespacho(null);
        }

        try {

            /*
             * Carregando as informacoes do vendedor DO PEDIDO. Caso seja um
             * pedido novo, vamos associa-lo ao vendedor, caso contrario,
             * recuperamos o usuario que efetuou a venda. Precisamo recuperar o
             * vendedor, pois o JSON devera conter o nome e email do vendedor.
             */
            final Usuario proprietario = pedido.getId() == null ? usuarioService
                    .pesquisarUsuarioResumidoById(getCodigoUsuario()) : pedidoService.pesquisarProprietario(pedido
                    .getId());

            pedido.setProprietario(proprietario);
            pedidoService.inserirPedido(pedido);

            addAtributo("orcamento", pedido.isOrcamento());
            addAtributo("isCompra", pedido.isCompra());
            formatarPedido(pedido);
            // Esse comando eh para configurar o id do cliente que sera
            // serializado para o preenchimento do id na tela quando o cliente
            // nao existe no caso do orcamento.
            pedido.setIdCliente(pedido.getCliente().getId());
            serializarJson(new SerializacaoJson(pedido).incluirAtributo("situacaoPedido", "proprietario"));

        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("inclusao/alteracao do pedido", e);
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
            return SituacaoPedido.isCancelado(situacao) || isCompraFinalizada || isVendaFinalizada;
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

    public void pesquisarPedidoByIdCliente(Integer idCliente, Integer idVendedor, Integer idFornecedor,
            TipoPedido tipoPedido, boolean orcamento, Integer paginaSelecionada, ItemPedido itemVendido) {
        boolean isCompra = TipoPedido.COMPRA.equals(tipoPedido);
        if (idCliente == null) {
            gerarListaMensagemAlerta("Cliente é obrigatório para a pesquisa de pedidos");
        } else if (!isVisulizacaoClientePermitida(idCliente)) {
            gerarListaMensagemAlerta("O usuário não tem permissão para pesquisar os pedidos do cliente.");
        } else {

            final RelatorioWrapper<Pedido, ItemPedido> relatorio = gerarRelatorioPaginadoItemPedido(idCliente,
                    idVendedor, idFornecedor, orcamento, isCompra, paginaSelecionada, itemVendido);
            inicializarRelatorioPaginadoSemRedirecionar(paginaSelecionada, relatorio, "relatorioItemPedido");

            /*
             * Recuperando os dados do cliente no caso em que nao tenhamos
             * resultado na pesquisa de pedido, entao os dados do cliente devem
             * permanecer na tela
             */
            Cliente cliente = clienteService.pesquisarById(idCliente);
            carregarVendedor(cliente);
            addAtributo("cliente", cliente);

            if (isCompra) {
                // Aqui estamos supondo que o usuario que acessou a tela eh um
                // comprador pois ele tem permissao para isso. E o campo com o
                // nome do comprador deve sempre estar preenchido.
                addAtributo("proprietario", usuarioService.pesquisarById(getCodigoUsuario()));
                addAtributo("listaRepresentada", representadaService.pesquisarFornecedor(true));
            } else {
                // Aqui ja foi carregado o vendedor resumido.
                addAtributo("proprietario", cliente.getVendedor());
            }

            LogradouroCliente l = clienteService.pesquisarLogradouroFaturamentoById(idCliente);
            if (l != null) {
                addAtributo("logradouroFaturamento", l.getCepEnderecoNumeroBairro());
            }

            if (!orcamento) {
                addAtributo("listaTransportadora", transportadoraService.pesquisarTransportadoraAtiva());
                addAtributo("listaRedespacho", transportadoraService.pesquisarTransportadoraByIdCliente(idCliente));
            }
            addAtributo("idRepresentadaSelecionada", idFornecedor);
        }
        addAtributo("tipoPedido", tipoPedido);
        addAtributo("orcamento", orcamento);
        addAtributo("isCompra", isCompra);
        configurarTipoPedido(tipoPedido);

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

    void removerItemPedido(Integer id) {
        try {
            final PedidoJson json = new PedidoJson(pedidoService.removerItemPedido(id));
            serializarJson(new SerializacaoJson("pedido", json));
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErro("Remoção do item do pedido", e);
        }
    }

    public void setClienteService(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public void setPedidoService(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public void setRelatorioService(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    public void setRepresentadaService(RepresentadaService representadaService) {
        this.representadaService = representadaService;
    }

    public void setTransportadoraService(TransportadoraService transportadoraService) {
        this.transportadoraService = transportadoraService;
    }

    public void setUsuarioService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private void verificarConversao() {
        if (validator != null && validator.hasErrors()) {
            StringBuilder s = new StringBuilder();
            for (Message m : validator.getErrors()) {
                s.append(m.getCategory() + "=>" + m.getMessage());
            }
            gerarLogErro("Coversao de dados pelo VRaptor: " + s.toString());
        }
    }
}
