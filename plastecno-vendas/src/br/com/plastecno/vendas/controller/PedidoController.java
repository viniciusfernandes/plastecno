package br.com.plastecno.vendas.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.FormaMaterialService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.TipoEntregaService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.ClienteJson;
import br.com.plastecno.vendas.json.ItemPedidoJson;
import br.com.plastecno.vendas.json.PedidoJson;
import br.com.plastecno.vendas.json.RepresentadaJson;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;
import br.com.plastecno.vendas.relatorio.conversor.GeradorRelatorioPDF;

@Resource
public class PedidoController extends AbstractController {

    private class MaterialAutocomplete extends Autocomplete {
        private Boolean importado;

        public MaterialAutocomplete(Integer valor, String label, boolean importado) {
            super(valor, label);
            this.importado = importado;
        }

        @SuppressWarnings(value = {"unused"})
        public Boolean getImportado() {
            return importado;
        }

        @SuppressWarnings(value = {"unused"})
        public void setImportado(Boolean importado) {
            this.importado = importado;
        }
    }

    private class PedidoPDFWrapper {
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

    @Servico
    private ClienteService clienteService;

    private String diretorioTemplateRelatorio;

    @Servico
    private EstoqueService estoqueService;

    @Servico
    private FormaMaterialService formaMaterialService;

    private GeradorRelatorioPDF geradorRelatorio;

    @Servico
    private MaterialService materialService;

    @Servico
    private PedidoService pedidoService;

    @Servico
    private RepresentadaService representadaService;

    @Servico
    private TipoEntregaService tipoEntregaService;

    @Servico
    private TransportadoraService transportadoraService;

    @Servico
    private UsuarioService usuarioService;

    public PedidoController(Result result, UsuarioInfo usuarioInfo, GeradorRelatorioPDF gerador,
            HttpServletRequest request) {
        super(result, usuarioInfo);
        this.verificarPermissaoAcesso("acessoCadastroPedidoPermitido", TipoAcesso.CADASTRO_PEDIDO_VENDAS,
                TipoAcesso.CADASTRO_PEDIDO_COMPRA);
        this.geradorRelatorio = gerador;
        this.diretorioTemplateRelatorio = request.getServletContext().getRealPath("/templates");
    }

    @Post("pedido/cancelamento")
    public void cancelarPedido(Integer idPedido, TipoPedido tipoPedido) {
        try {
            this.pedidoService.cancelarPedido(idPedido);
            this.gerarMensagemSucesso("Pedido No. " + idPedido + " cancelado com sucesso");
            configurarTipoPedido(tipoPedido);
            redirectByTipoPedido(tipoPedido);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e.getListaMensagem());
            pesquisarPedidoById(idPedido, tipoPedido);
        }
    }

    private void configurarTipoPedido(TipoPedido tipoPedido) {
        if (TipoPedido.COMPRA.equals(tipoPedido)) {
            addAtributo("tipoPedido", tipoPedido);
            if (!contemAtributo("proprietario")) {
                addAtributo("proprietario", usuarioService.pesquisarById(getCodigoUsuario()));
            }
        }
    }

    @Get("pedido/pdf")
    public Download downloadPedidoPDF(Integer idPedido, TipoPedido tipoPedido) {
        try {
            PedidoPDFWrapper wrapper = this.gerarPDF(idPedido, tipoPedido);
            final Pedido pedido = wrapper.getPedido();

            final StringBuilder titulo = new StringBuilder(pedido.isOrcamento() ? "Orcamento " : "Pedido ")
                    .append("No. ").append(idPedido).append(" - ").append(pedido.getCliente().getNomeFantasia())
                    .append(".pdf");

            return gerarDownload(wrapper.getArquivoPDF(), titulo.toString());
        } catch (Exception e) {
            this.gerarLogErro("geração do relatório de pedido", e);
            // Estamos retornando null porque no caso de falhas nao devemos
            // efetuar o download do arquivo
            return null;
        }
    }

    @Post("pedido/envio")
    public void enviarPedido(Integer idPedido, TipoPedido tipoPedido) {
        try {
            final PedidoPDFWrapper wrapper = this.gerarPDF(idPedido, tipoPedido);
            final Pedido pedido = wrapper.getPedido();

            this.pedidoService.enviarPedido(idPedido, wrapper.getArquivoPDF());

            final String mensagem = pedido.isOrcamento() ? "Orçamento No. " + idPedido
                    + " foi enviado com sucesso para o cliente " + pedido.getCliente().getNomeFantasia()
                    : "Pedido No. " + idPedido + " foi enviado com sucesso para a representada "
                            + pedido.getRepresentada().getNomeFantasia();

            gerarMensagemSucesso(mensagem);
            redirectByTipoPedido(tipoPedido);
        } catch (NotificacaoException e) {
            gerarLogErro("envio de email do pedido No. " + idPedido, e);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            // populando a tela de pedidos
            redirecTo(this.getClass()).pesquisarPedidoById(idPedido, tipoPedido);
        } catch (Exception e) {
            gerarLogErro("envio de email do pedido No. " + idPedido, e);
        }

        configurarTipoPedido(tipoPedido);
    }

    private void gerarListaRepresentada(Pedido pedido) {
        // Verificando se a lista de representada ja foi preenchida em outro
        // fluxo
        if (!contemAtributo("listaRepresentada")) {
            TipoPedido tipoPedido = pedido == null ? null : pedido.getTipoPedido();
            addAtributo("listaRepresentada", representadaService.pesquisarRepresentadaAtivoByTipoPedido(tipoPedido));
        }

        if (pedido != null) {
            addAtributo("idRepresentadaSelecionada", pedido.getRepresentada().getId());
            addAtributo("ipiDesabilitado", !pedido.getRepresentada().isIPIHabilitado());
        }
    }

    private Set<SituacaoPedido> gerarListaSituacaoPedido() {
        Set<SituacaoPedido> listaSituacao = new HashSet<SituacaoPedido>();
        listaSituacao.add(SituacaoPedido.DIGITACAO);
        listaSituacao.add(SituacaoPedido.ORCAMENTO);
        return listaSituacao;
    }

    /*
     * Metodo dedicado a paginar os pedidos no caso em que o usuario seja um
     * administrador, sendo assim, ele podera consultar os pedidos de todos os
     * vendedores
     */
    private PaginacaoWrapper<Pedido> gerarPaginacaoPedido(Integer idCliente, Integer idVendedor, Integer idFornecedor,
            boolean isCompra, Integer paginaSelecionada) {
        final int indiceRegistroInicial = calcularIndiceRegistroInicial(paginaSelecionada);

        // Essa variavel eh utilizada para decidirmos se queremos recuperar
        // todos os pedidos de um determinado cliente independentemente do
        // vendedor. Essa acao sera disparada por qualquer um que seja
        // adiministrador do sistema, podendo ser um outro vendedor ou nao.
        boolean pesquisarTodos = isAcessoPermitido(TipoAcesso.ADMINISTRACAO) && !getCodigoUsuario().equals(idVendedor);

        return this.pedidoService.paginarPedido(idCliente, pesquisarTodos ? null : idVendedor, idFornecedor, isCompra,
                indiceRegistroInicial, getNumerRegistrosPorPagina());
    }

    private PedidoPDFWrapper gerarPDF(Integer idPedido, TipoPedido tipoPedido) throws BusinessException {
        Pedido pedido = pesquisarPedido(idPedido, tipoPedido);
        if (pedido == null) {
            throw new BusinessException("Não é possível gerar o PDF do pedido de numero " + idPedido
                    + " pois não existe no sistema");
        }

        pedido.setListaLogradouro(this.pedidoService.pesquisarLogradouro(idPedido));

        final List<ItemPedido> listaItem = this.pedidoService.pesquisarItemPedidoByIdPedido(idPedido);

        formatarItemPedido(listaItem);
        formatarPedido(pedido);

        Cliente cliente = pedido.getCliente();
        this.formatarDocumento(cliente);
        cliente.setListaLogradouro(this.clienteService.pesquisarLogradouro(cliente.getId()));

        Transportadora transportadora = pedido.getTransportadora();
        if (transportadora != null) {
            transportadora.setListaContato(this.transportadoraService.pesquisarContato(transportadora.getId()));
            transportadora.setLogradouro(this.transportadoraService.pesquisarLogradorouro(transportadora.getId()));
        }

        transportadora = pedido.getTransportadoraRedespacho();
        if (transportadora != null) {
            transportadora.setListaContato(this.transportadoraService.pesquisarContato(transportadora.getId()));
            transportadora.setLogradouro(this.transportadoraService.pesquisarLogradorouro(transportadora.getId()));
        }

        final Logradouro logradouroFaturamento = pedido.getLogradouro(TipoLogradouro.FATURAMENTO);
        final Logradouro logradouroEntrega = pedido.getLogradouro(TipoLogradouro.ENTREGA);
        final Logradouro logradouroCobranca = pedido.getLogradouro(TipoLogradouro.COBRANCA);

        String tipo = pedido.isVenda() ? "Venda" : "Compra";

        geradorRelatorio.addAtributo("tipoRelacionamento", pedido.isVenda() ? "Represent." : "Forneced.");
        geradorRelatorio.addAtributo("tipoProprietario", pedido.isVenda() ? "Vendedor" : "Comprador");
        geradorRelatorio.addAtributo("titulo", pedido.isOrcamento() ? "Orçamento de " + tipo : "Pedido de " + tipo);
        geradorRelatorio.addAtributo("tipoPedido", tipo);
        geradorRelatorio.addAtributo("pedido", pedido);
        geradorRelatorio.addAtributo("logradouroFaturamento",
                logradouroFaturamento != null ? logradouroFaturamento.getDescricao() : "");
        geradorRelatorio.addAtributo("logradouroEntrega", logradouroEntrega != null ? logradouroEntrega.getDescricao()
                : "");
        geradorRelatorio.addAtributo("logradouroCobranca",
                logradouroCobranca != null ? logradouroCobranca.getDescricao() : "");
        geradorRelatorio.addAtributo("listaItem", listaItem);

        geradorRelatorio.processar(new File(diretorioTemplateRelatorio + "/pedido.html"));
        return new PedidoPDFWrapper(pedido, geradorRelatorio.gerarPDF());
    }

    private void inicializarListaSituacaoPedido() {
        final Set<SituacaoPedido> listaSituacao = this.gerarListaSituacaoPedido();

        final SituacaoPedido situacaoPedidoSelecionada = (SituacaoPedido) getAtributo("situacaoPedidoSelecionada");

        if (situacaoPedidoSelecionada == null) {
            addAtributo("situacaoPedidoSelecionada", SituacaoPedido.DIGITACAO);
        } else {
            /*
             * Vamos adicionar a situacao a lista pois na inicializacao nao
             * temos cancelamento e enviado pois no preenchimento de um novo
             * pedido o usuario nao tera acesso a essas opcoes, sendo que elas
             * aparecerao em fluxos de consulta.
             */
            listaSituacao.add(situacaoPedidoSelecionada);
        }
        addAtributo("listaSituacaoPedido", listaSituacao);
    }

    @Post("pedido/item/inclusao")
    public void inserirItemPedido(Integer numeroPedido, ItemPedido itemPedido, Double aliquotaIPI) {
        try {
            if (itemPedido.getMaterial().getId() == null) {
                itemPedido.setMaterial(null);
            }
            if (aliquotaIPI != null) {
                itemPedido.setAliquotaIPI(NumeroUtils.gerarAliquota(aliquotaIPI));
            }
            itemPedido.setAliquotaICMS(NumeroUtils.gerarAliquota(itemPedido.getAliquotaICMS()));

            final Integer idItemPedido = this.pedidoService.inserirItemPedido(numeroPedido, itemPedido);
            itemPedido.setId(idItemPedido);

            Double[] valorPedido = pedidoService.pesquisarValorPedidoByItemPedido(idItemPedido);
            itemPedido.setValorPedido(valorPedido[0]);
            itemPedido.setValorPedidoIPI(valorPedido[1]);

            formatarItemPedido(itemPedido);
            formatarPedido(itemPedido.getPedido());

            serializarJson(new SerializacaoJson("itemPedido", new ItemPedidoJson(itemPedido)));
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("inclusao/alteracao do item do pedido " + numeroPedido, e);
        }
    }

    @Post("pedido/inclusao")
    public void inserirPedido(Pedido pedido, Contato contato) {
        if (hasAtributo(contato)) {
            pedido.setContato(contato);
        }

        if (pedido.getSituacaoPedido() == null) {
            pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
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
            if (pedido.isOrcamento()) {
                pedidoService.inserirOrcamento(pedido);
            } else {
                pedidoService.inserir(pedido);
            }

            formatarPedido(pedido);
            serializarJson(new SerializacaoJson(pedido).incluirAtributo("situacaoPedido", "proprietario"));
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("inclusao/alteracao do pedido", e);
        }
    }

    private boolean isPedidoDesabilitado(Pedido pedido) {
        if (pedido == null) {
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

    @Get("pedido/limpar")
    public void limpar(TipoPedido tipoPedido) {
        configurarTipoPedido(tipoPedido);
        redirectByTipoPedido(tipoPedido);
    }

    @Get("pedido/compra")
    public void pedidoCompra() {
        configurarTipoPedido(TipoPedido.COMPRA);
        addAtributo("listaRepresentada", representadaService.pesquisarFornecedor(true));
        addAtributo("descricaoTipoPedido", TipoPedido.COMPRA.getDescricao());
        addAtributo("cliente", clienteService.pesquisarRevendedor());
        redirecTo(this.getClass()).pedidoHome();
    }

    @Get("pedido")
    public void pedidoHome() {
        inicializarListaSituacaoPedido();

        addAtributo("listaTipoEntrega", this.tipoEntregaService.pesquisar());

        gerarListaRepresentada(null);

        addAtributo("listaFormaMaterial", this.formaMaterialService.pesquisar());
        addAtributo("listaContatoDesabilitada", true);

        addAtributo("industrializacao", FinalidadePedido.INDUSTRIALIZACAO);
        addAtributo("consumo", FinalidadePedido.CONSUMO);
        addAtributo("revenda", FinalidadePedido.REVENDA);
        addAtributo("descricaoTipoPedido", TipoPedido.REPRESENTACAO.getDescricao());

        // verificando se o parametro para desabilitar ja foi incluido em outro
        // fluxo
        if (!contemAtributo("pedidoDesabilitado")) {
            addAtributo("pedidoDesabilitado", false);
        }
    }

    /*
     * Metodo disparado quando o usuario selecionar um cliente do autocomplete
     */
    @Get("pedido/cliente/{id}")
    public void pesquisarClienteById(Integer id) {
        Cliente cliente = this.clienteService.pesquisarById(id);
        cliente.setListaRedespacho(this.clienteService.pesquisarTransportadorasRedespacho(id));
        this.carregarVendedor(cliente);
        this.formatarDocumento(cliente);

        final ClienteJson json = new ClienteJson(cliente, this.transportadoraService.pesquisar());

        SerializacaoJson serializacaoJson = new SerializacaoJson("cliente", json)
                .incluirAtributo("listaTransportadora").incluirAtributo("listaRedespacho").incluirAtributo("vendedor");

        serializarJson(serializacaoJson);
    }

    @Get("pedido/cliente")
    public void pesquisarClienteByNomeFantasia(String nomeFantasia) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Cliente> listaCliente = this.clienteService.pesquisarByNomeFantasia(nomeFantasia);
        for (Cliente cliente : listaCliente) {
            lista.add(new Autocomplete(cliente.getId(), cliente.getNomeFantasia()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    @Get("pedido/item/{id}")
    public void pesquisarItemPedidoById(Integer id) {
        ItemPedido itemPedido = pedidoService.pesquisarItemPedido(id);
        if (itemPedido != null) {
            formatarItemPedido(itemPedido);
            serializarJson(new SerializacaoJson("itemPedido", new ItemPedidoJson(itemPedido)));
        } else {
            this.gerarRetornoErroAjax("Falha na pesquisa do item de pedido " + id
                    + ". Item do pedido não encontrado no sistema.");
        }
    }

    @Get("pedido/material")
    public void pesquisarMaterial(String sigla, Integer idRepresentada) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        if (sigla != null && idRepresentada != null) {
            List<Material> listaMaterial = this.materialService.pesquisarMaterialAtivoBySigla(sigla, idRepresentada);
            for (Material material : listaMaterial) {
                lista.add(new MaterialAutocomplete(material.getId(), material.getDescricaoFormatada(), material
                        .isImportado()));
            }
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    private Pedido pesquisarPedido(Integer idPedido, TipoPedido tipoPedido) {
        boolean isCompra = TipoPedido.COMPRA.equals(tipoPedido);

        Pedido pedido = null;
        // Temos que fazer essa distincao pois o usuario pode acessar pedidos de
        // compra a partir da tela de pedidos de venda, e vice-versa. Temos que
        // proibir isso.
        if (isCompra) {
            pedido = pedidoService.pesquisarCompraById(idPedido);
        } else {
            pedido = pedidoService.pesquisarVendaById(idPedido);
        }

        final Integer idUsuario = getCodigoUsuario();
        // Verificando se o usuario que esta tentando acessar os dados do pedido
        // eh o mesmo usuario que efetuou a venda.
        final boolean isPedidoPertenceAoUsuario = pedido != null && pedido.getVendedor() != null && idUsuario != null
                && idUsuario.equals(pedido.getVendedor().getId());
        // Verificando se tem poderes para visualizar o pedido. No caso de
        // compra

        // Verificando se o usuario eh um comprador e esta acessando um pedido
        // de compra.
        final boolean isAcessoCompraPermitida = isCompra && isAcessoPermitido(TipoAcesso.CADASTRO_PEDIDO_COMPRA);

        final boolean visualizacaoPermitida = isAcessoCompraPermitida || isPedidoPertenceAoUsuario
                || isAcessoPermitido(TipoAcesso.ADMINISTRACAO);
        return visualizacaoPermitida ? pedido : null;
    }

    @Get("pedido/{id}")
    public void pesquisarPedidoById(Integer id, TipoPedido tipoPedido) {

        // Se vendedor que pesquisa o pedido nao for o mesmo que efetuou a venda
        // nao devemos exibi-lo para o vendedor que pesquisa por questao de
        // sigilo.
        Pedido pedido = pesquisarPedido(id, tipoPedido);
        if (pedido == null) {
            this.gerarListaMensagemErro("Pedido não existe no sistema");
        } else {
            this.formatarPedido(pedido);
            this.formatarDocumento(pedido.getCliente());

            List<Transportadora> listaRedespacho = this.clienteService.pesquisarTransportadorasRedespacho(pedido
                    .getCliente().getId());

            List<Transportadora> listaTransportadora = this.clienteService.pesquisarTransportadorasDesassociadas(pedido
                    .getCliente().getId());

            if (!listaRedespacho.contains(pedido.getTransportadoraRedespacho())) {
                listaRedespacho.add(pedido.getTransportadoraRedespacho());
            }

            if (!listaTransportadora.contains(pedido.getTransportadora())) {
                listaTransportadora.add(pedido.getTransportadora());
            }

            List<ItemPedido> listaItem = this.pedidoService.pesquisarItemPedidoByIdPedido(pedido.getId());

            formatarItemPedido(listaItem);

            addAtributo("listaTransportadora", listaTransportadora);
            addAtributo("listaRedespacho", listaRedespacho);
            addAtributo("listaItemPedido", listaItem);
            addAtributo("contemItem", !listaItem.isEmpty());
            addAtributo("pedido", pedido);
            addAtributo("proprietario", pedidoService.pesquisarProprietario(id));
            addAtributo("cliente", pedido.getCliente());
            addAtributo("contato", pedido.getContato());
            addAtributo("situacaoPedidoSelecionada", pedido.getSituacaoPedido());
            this.gerarListaRepresentada(pedido);

            SituacaoPedido situacao = pedido.getSituacaoPedido();
            // Condicao indicadora de pedido pronto para enviar
            final boolean acessoEnvioPedidoPermitido = !SituacaoPedido.ENVIADO.equals(situacao)
                    && !SituacaoPedido.CANCELADO.equals(situacao) && !SituacaoPedido.COMPRA_RECEBIDA.equals(situacao);

            // Condicao indicadora para reenvio do pedido
            final boolean acessoReenvioPedidoPermitido = isAcessoPermitido(TipoAcesso.ADMINISTRACAO)
                    && (SituacaoPedido.ENVIADO.equals(situacao) || SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO
                            .equals(situacao));

            // Condicao indicadora de que apenas o administrador podera cancelar
            // pedidos ja enviados
            final boolean acessoCancelamentoPedidoPermitido = !SituacaoPedido.CANCELADO.equals(situacao)
                    || pedido.isVendaEfetuada() || pedido.isRevendaEfetuada()
                    || (pedido.isCompraEfetuada() && isAcessoPermitido(TipoAcesso.ADMINISTRACAO));

            final boolean acessoRefazerPedidoPermitido = (pedido.isVendaEfetuada() || pedido.isRevendaEfetuada())
                    && !SituacaoPedido.CANCELADO.equals(situacao) && !SituacaoPedido.DIGITACAO.equals(situacao);

            liberarAcesso("pedidoDesabilitado", isPedidoDesabilitado(pedido));
            liberarAcesso("acessoEnvioPedidoPermitido", acessoEnvioPedidoPermitido);
            liberarAcesso("acessoReenvioPedidoPermitido", acessoReenvioPedidoPermitido);
            liberarAcesso("acessoCancelamentoPedidoPermitido", acessoCancelamentoPedidoPermitido);
            liberarAcesso("acessoRefazerPedidoPermitido", acessoRefazerPedidoPermitido);
        }
        configurarTipoPedido(tipoPedido);
        redirectByTipoPedido(tipoPedido);
    }

    @Get("pedido/listagem")
    public void pesquisarPedidoByIdCliente(Integer idCliente, Integer idVendedor, Integer idFornecedor,
            TipoPedido tipoPedido, Integer paginaSelecionada) {
        if (idCliente == null) {
            gerarListaMensagemErro("Cliente é obrigatório para a pesquisa de pedidos");
            irTopoPagina();
        } else {
            boolean isCompra = TipoPedido.COMPRA.equals(tipoPedido);
            final PaginacaoWrapper<Pedido> paginacao = gerarPaginacaoPedido(idCliente, idVendedor, idFornecedor,
                    isCompra, paginaSelecionada);

            final Collection<Pedido> listaPedido = paginacao.getLista();
            if (!listaPedido.isEmpty()) {
                for (Pedido pedido : listaPedido) {
                    this.formatarPedido(pedido);
                    this.formatarDocumento(pedido.getCliente());
                }
            }
            this.inicializarPaginacao(paginaSelecionada, paginacao, "listaPedido");

            /*
             * Recuperando os dados do cliente no caso em que nao tenhamos
             * resultado na pesquisa de pedido, entao os dados do cliente devem
             * permanecer na tela
             */
            Cliente cliente = this.clienteService.pesquisarById(idCliente);
            
            formatarDocumento(cliente);
            carregarVendedor(cliente);
            addAtributo("cliente", cliente);
            addAtributo("proprietario", cliente.getVendedor());
            
            if (isCompra) {
                // Aqui estamos supondo que o usuario que acessou a tela eh um
                // comprador pois ele tem permissao para isso. E o campo com o
                // nome do comprador deve sempre estar preenchido.
                addAtributo("proprietario", usuarioService.pesquisarById(getCodigoUsuario()));
                addAtributo("listaRepresentada", representadaService.pesquisarFornecedor(true));
            } else {
                addAtributo("vendedor", cliente.getVendedor());
            }
            addAtributo("listaTransportadora", this.transportadoraService.pesquisar());
            addAtributo("listaRedespacho", this.transportadoraService.pesquisarTransportadoraByIdCliente(idCliente));
            addAtributo("idRepresentadaSelecionada", idFornecedor);
        }
        configurarTipoPedido(tipoPedido);
    }

    private void redirectByTipoPedido(TipoPedido tipoPedido) {
        if (TipoPedido.COMPRA.equals(tipoPedido)) {
            redirecTo(this.getClass()).pedidoCompra();
        } else {
            redirecTo(this.getClass()).pedidoHome();
        }
        irTopoPagina();
    }

    @Post("pedido/refazer")
    public void refazerPedido(Integer idPedido, TipoPedido tipoPedido) {
        try {
            Integer idPedidoClone = pedidoService.refazerPedido(idPedido);
            this.pesquisarPedidoById(idPedidoClone, tipoPedido);
            this.gerarMensagemSucesso("Pedido No. " + idPedidoClone + " inserido e refeito a partir do pedido No. "
                    + idPedido);

        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
            pesquisarPedidoById(idPedido, tipoPedido);
        } catch (Exception e) {
            gerarLogErroRequestAjax("copia do pedido de No. " + idPedido, e);
            pesquisarPedidoById(idPedido, tipoPedido);
        }

    }

    @Post("pedido/itempedido/remocao/{id}")
    public void removerItemPedido(Integer id) {
        try {
            final PedidoJson json = new PedidoJson(this.pedidoService.removerItemPedido(id));
            serializarJson(new SerializacaoJson("pedido", json));
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        }
    }

    @Get("pedido/representada/{idRepresentada}/aliquotaIPI/")
    public void verificarRepresentadaCalculaIPI(Integer idRepresentada) {
        final RepresentadaJson json = new RepresentadaJson(idRepresentada,
                this.representadaService.isCalculoIPIHabilitado(idRepresentada));

        serializarJson(new SerializacaoJson("representada", json));
    }

}
