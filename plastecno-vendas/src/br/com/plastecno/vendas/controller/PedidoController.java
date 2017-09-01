package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.FormaMaterialService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.TipoEntregaService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoCST;
import br.com.plastecno.service.constante.TipoFinalidadePedido;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.mensagem.email.AnexoEmail;
import br.com.plastecno.service.relatorio.RelatorioService;
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
public class PedidoController extends AbstractPedidoController {

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

    @Servico
    private ClienteService clienteService;

    @Servico
    private ComissaoService comissaoService;

    @Servico
    private EstoqueService estoqueService;

    @Servico
    private FormaMaterialService formaMaterialService;

    @Servico
    private MaterialService materialService;

    @Servico
    private NFeService nFeService;

    @Servico
    private PedidoService pedidoService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private RepresentadaService representadaService;

    @Servico
    private TipoEntregaService tipoEntregaService;

    @Servico
    private TransportadoraService transportadoraService;

    @Servico
    private UsuarioService usuarioService;

    public PedidoController(Result result, UsuarioInfo usuarioInfo, GeradorRelatorioPDF geradorRelatorioPDF,
            HttpServletRequest request) {
        super(result, usuarioInfo, geradorRelatorioPDF, request);
        verificarPermissaoAcesso("acessoCadastroPedidoPermitido", TipoAcesso.CADASTRO_PEDIDO_VENDAS,
                TipoAcesso.CADASTRO_PEDIDO_COMPRA);

        super.setClienteService(clienteService);
        super.setPedidoService(pedidoService);
        super.setTransportadoraService(transportadoraService);
        super.setRepresentadaService(representadaService);
        super.setUsuarioService(usuarioService);
        super.setRelatorioService(relatorioService);
    }

    @Get("pedido/pesoitem")
    public void calcularPesoItem(ItemPedido item) {
        try {
            Double peso = pedidoService.calcularPesoItemPedido(item);
            String pesoFormatado = peso == null ? "" : String.valueOf(NumeroUtils.arredondarValorMonetario(peso));
            serializarJson(new SerializacaoJson("peso", pesoFormatado));
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErroRequestAjax("c�lculo do peso do item do pedido", e);
        }
    }

    @Post("pedido/cancelamento")
    public void cancelarPedido(Integer idPedido, TipoPedido tipoPedido, boolean orcamento) {
        try {
            pedidoService.cancelarPedido(idPedido);
            gerarMensagemSucesso("Pedido No. " + idPedido + " cancelado com sucesso");
            configurarTipoPedido(tipoPedido);
            redirecionarHome(tipoPedido, orcamento, true);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e.getListaMensagem());
            pesquisarPedidoById(idPedido, tipoPedido, orcamento);
        }
    }

    @Get("pedido/venda/cliente")
    public void carregarCliente(Integer idCliente) {
        Cliente cliente = carregarDadosCliente(idCliente);
        Logradouro l = cliente.getLogradouro();
        addAtributo("cliente", cliente);
        addAtributo("logradouroFaturamento", l != null ? l.getCepEnderecoNumeroBairro() : "");
        addAtributo("proprietario", cliente.getVendedor());
        addAtributo("listaTransportadora", transportadoraService.pesquisarTransportadoraAtiva());
        addAtributo("listaRedespacho", clienteService.pesquisarTransportadorasRedespacho(idCliente));

        pedidoVendaHome();
    }

    private Cliente carregarDadosCliente(Integer idCliente) {
        Cliente cliente = clienteService.pesquisarClienteEContatoById(idCliente);
        cliente.setListaRedespacho(clienteService.pesquisarTransportadorasRedespacho(idCliente));
        cliente.setLogradouro(clienteService.pesquisarLogradouroFaturamentoById(idCliente));

        carregarVendedor(cliente);
        // Aqui devemos formatar os documentos do cliente pois eh uma requisicao
        // assincrona e a mascara em javascript nao eh executada.
        formatarDocumento(cliente);
        return cliente;
    }

    @Post("pedido/copia/{idPedido}")
    public void copiarPedido(Integer idPedido, TipoPedido tipoPedido, boolean orcamento) {
        try {
            Integer idPedidoClone = pedidoService.copiarPedido(idPedido, orcamento);
            pesquisarPedidoById(idPedidoClone, tipoPedido, orcamento);
            gerarMensagemSucesso("Pedido No. " + idPedidoClone + " inserido e copiado a partir do pedido No. "
                    + idPedido);
            addAtributo("orcamento", orcamento);

        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
            pesquisarPedidoById(idPedido, tipoPedido, orcamento);
        } catch (Exception e) {
            gerarLogErroRequestAjax("copia do pedido de No. " + idPedido, e);
            pesquisarPedidoById(idPedido, tipoPedido, orcamento);
        }
    }

    @Get("pedido/pdf")
    public Download downloadPDFPedido(Integer idPedido, TipoPedido tipoPedido) {
        return super.downloadPDFPedido(idPedido, tipoPedido);
    }

    @Get("pedidoassociado/pdf")
    public Download downloadPedidoAssociadoPDF(Integer idPedido, TipoPedido tipoPedido) {
        return downloadPDFPedido(idPedido, tipoPedido != null ? null : TipoPedido.COMPRA);
    }

    @Post("pedido/envio")
    public void enviarPedido(Integer idPedido, TipoPedido tipoPedido, boolean orcamento) {
        try {
            final PedidoPDFWrapper wrapper = gerarPDF(idPedido, tipoPedido);
            final Pedido pedido = wrapper.getPedido();

            pedidoService.enviarPedido(idPedido, new AnexoEmail(wrapper.getArquivoPDF()));

            final String mensagem = pedido.isOrcamento() ? "Or�amento No. " + idPedido
                    + " foi enviado com sucesso para o cliente " + pedido.getCliente().getNomeFantasia()
                    : "Pedido No. " + idPedido + " foi enviado com sucesso para a representada "
                            + pedido.getRepresentada().getNomeFantasia();

            gerarMensagemSucesso(mensagem);
            redirecionarHome(tipoPedido, orcamento, true);
        } catch (NotificacaoException e) {
            gerarLogErro("envio de email do pedido No. " + idPedido, e);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            // populando a tela de pedidos
            redirecTo(this.getClass()).pesquisarPedidoById(idPedido, tipoPedido, orcamento);
        } catch (Exception e) {
            gerarLogErro("envio de email do pedido No. " + idPedido, e);
        }
    }

    private String gerarComissao(Integer idRepresentada, Integer idVendedor) {
        boolean isRevenda = representadaService.isRevendedor(idRepresentada);
        TipoPedido t = isRevenda ? TipoPedido.REVENDA : TipoPedido.REPRESENTACAO;
        if (idVendedor == null) {
            idVendedor = getCodigoUsuario();
        }
        Double aliquota = comissaoService.pesquisarAliquotaComissaoByIdVendedor(idVendedor, t);
        return aliquota == null ? "" : NumeroUtils.gerarPercentual(aliquota, 2).toString();
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

    private void inicializarHome(TipoPedido tipoPedido, boolean orcamento) {
        addAtributo("orcamento", orcamento);
        addAtributo("listaTipoEntrega", tipoEntregaService.pesquisar());

        gerarListaRepresentada(null);

        addAtributo("listaFormaMaterial", formaMaterialService.pesquisar());
        addAtributo("listaContatoDesabilitada", true);

        addAtributo("listaTipoFinalidadePedido", TipoFinalidadePedido.values());
        addAtributo("descricaoTipoPedido", TipoPedido.REPRESENTACAO.getDescricao());
        addAtributo("inclusaoDadosNFdesabilitado", false);
        addAtributo("listaCST", TipoCST.values());
        addAtributo("acessoDadosNotaFiscalPermitido",
                isAcessoPermitido(TipoAcesso.ADMINISTRACAO, TipoAcesso.CADASTRO_PEDIDO_COMPRA));

        // verificando se o parametro para desabilitar ja foi incluido em outro
        // fluxo
        addAtributoCondicional("pedidoDesabilitado", false);
    }

    @Post("pedido/item/inclusao")
    public void inserirItemPedido(Integer numeroPedido, ItemPedido itemPedido, Double aliquotaIPI) {
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
    public void inserirPedido(Pedido pedido, Contato contato, boolean orcamento) {
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
            if (pedido.isOrcamento()) {
                pedidoService.inserirOrcamento(pedido);
            } else {
                pedidoService.inserir(pedido);
            }

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

    @Get("pedido/limpar")
    public void limpar(TipoPedido tipoPedido, boolean orcamento) {
        configurarTipoPedido(tipoPedido);
        redirecionarHome(tipoPedido, orcamento, true);
    }

    @Get("pedido/compra")
    public void pedidoCompraHome() {
        addAtributoCondicional("isCompra", true);
        configurarTipoPedido(TipoPedido.COMPRA);

        addAtributo("listaRepresentada", representadaService.pesquisarFornecedor(true));
        addAtributo("descricaoTipoPedido", TipoPedido.COMPRA.getDescricao());
        addAtributo("cliente", clienteService.pesquisarRevendedor());
        redirecTo(this.getClass()).pedidoHome(TipoPedido.COMPRA, false);
    }

    @Get("pedido")
    public void pedidoHome(TipoPedido tipoPedido, boolean orcamento) {
        inicializarHome(tipoPedido, orcamento);
    }

    @Get("pedido/venda")
    public void pedidoVendaHome() {
        redirecTo(this.getClass()).pedidoHome(TipoPedido.REVENDA, false);
    }

    /*
     * Metodo disparado quando o usuario selecionar um cliente do autocomplete
     */
    @Get("pedido/cliente/{id}")
    public void pesquisarClienteById(Integer id) {
        Cliente cliente = carregarDadosCliente(id);

        final ClienteJson json = new ClienteJson(cliente, transportadoraService.pesquisarTransportadoraAtiva(),
                cliente.getListaRedespacho(), cliente.getLogradouro());

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
        ItemPedido itemPedido = pedidoService.pesquisarItemPedidoById(id);
        if (itemPedido != null) {
            formatarItemPedido(itemPedido);
            serializarJson(new SerializacaoJson("itemPedido", new ItemPedidoJson(itemPedido)));
        } else {
            this.gerarRetornoErroAjax("Falha na pesquisa do item de pedido " + id
                    + ". Item do pedido n�o encontrado no sistema.");
        }
    }

    @Get("pedido/material")
    public void pesquisarMaterial(String sigla, Integer idRepresentada) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        if (sigla != null && idRepresentada != null) {
            List<Material> listaMaterial = materialService.pesquisarMaterialAtivoBySigla(sigla, idRepresentada);
            for (Material material : listaMaterial) {
                lista.add(new MaterialAutocomplete(material.getId(), material.getDescricaoFormatada(), material
                        .isImportado()));
            }
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    @Get("pedido/nfe")
    public void pesquisarNFeByNumero(Integer numeroNFe) {
        redirecTo(EmissaoNFeController.class).pesquisarNFe(numeroNFe);
    }

    @Get("pedido/{id}")
    public void pesquisarPedidoById(Integer id, TipoPedido tipoPedido, boolean orcamento) {
        Pedido pedido = null;
        // Se vendedor que pesquisa o pedido nao estiver associado ao cliente
        // nao devemos exibi-lo para o vendedor que pesquisa por questao de
        // sigilo.
        if (!isVisulizacaoPermitida(id, tipoPedido)) {
            gerarListaMensagemAlerta("O usu�rio n�o tem permiss�o de acesso ao pedido.");
        } else {
            pedido = pedidoService.pesquisarPedidoById(id, TipoPedido.COMPRA.equals(tipoPedido));
            if (pedido == null || pedido.isOrcamento()) {
                gerarListaMensagemAlerta("Pedido No. " + id + " n�o existe no sistema");
            } else {
                formatarPedido(pedido);
                List<Transportadora> listaRedespacho = clienteService.pesquisarTransportadorasRedespacho(pedido
                        .getCliente().getId());

                List<Transportadora> listaTransportadora = clienteService.pesquisarTransportadorasDesassociadas(pedido
                        .getCliente().getId());

                if (!listaRedespacho.contains(pedido.getTransportadoraRedespacho())) {
                    listaRedespacho.add(pedido.getTransportadoraRedespacho());
                }

                if (!listaTransportadora.contains(pedido.getTransportadora())) {
                    listaTransportadora.add(pedido.getTransportadora());
                }

                List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(pedido.getId());

                formatarItemPedido(listaItem);
                formatarPedido(pedido);

                LogradouroCliente l = clienteService.pesquisarLogradouroFaturamentoById(pedido.getCliente().getId());
                if (l != null) {
                    addAtributo("logradouroFaturamento", l.getCepEnderecoNumeroBairro());
                }

                if (pedido.isVenda()) {
                    addAtributo("listaNumeroNFe", nFeService.pesquisarNumeroNFeByIdPedido(pedido.getId()));
                }

                addAtributo("listaIdPedidoAssociado",
                        pedidoService.pesquisarIdPedidoAssociadoByIdPedidoOrigem(id, pedido.isCompra()));
                addAtributo("listaTransportadora", listaTransportadora);
                addAtributo("listaRedespacho", listaRedespacho);
                addAtributo("listaItemPedido", listaItem);
                addAtributo("contemItem", !listaItem.isEmpty());
                addAtributo("pedido", pedido);
                addAtributo("proprietario", pedidoService.pesquisarProprietario(id));
                addAtributo("cliente", pedido.getCliente());
                addAtributo("contato", pedido.getContato());
                addAtributo("situacaoPedidoSelecionada", pedido.getSituacaoPedido());
                addAtributo("orcamento", pedido.isOrcamento());
                addAtributo("tipoPedido", pedido.getTipoPedido());
                addAtributo("isCompra", pedido.isCompra());
                addAtributo("aliquotaComissao",
                        gerarComissao(pedido.getRepresentada().getId(), pedido.getVendedor().getId()));
                gerarListaRepresentada(pedido);

                SituacaoPedido situacao = pedido.getSituacaoPedido();
                // Condicao indicadora de pedido pronto para enviar
                final boolean acessoEnvioPedidoPermitido = !SituacaoPedido.ENVIADO.equals(situacao)
                        && !SituacaoPedido.CANCELADO.equals(situacao)
                        && !SituacaoPedido.COMPRA_RECEBIDA.equals(situacao);

                // Condicao indicadora para reenvio do pedido
                final boolean acessoReenvioPedidoPermitido = isAcessoPermitido(TipoAcesso.ADMINISTRACAO)
                        && (SituacaoPedido.ENVIADO.equals(situacao) || SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO
                                .equals(situacao));

                // Condicao indicadora de que apenas o administrador podera
                // cancelar
                // pedidos ja enviados
                final boolean acessoCancelamentoPedidoPermitido = !SituacaoPedido.CANCELADO.equals(situacao)
                        || pedido.isVendaEfetuada() || pedido.isRevendaEfetuada()
                        || (pedido.isCompraEfetuada() && isAcessoPermitido(TipoAcesso.ADMINISTRACAO));

                final boolean acessoRefazerPedidoPermitido = (pedido.isVendaEfetuada() || pedido.isRevendaEfetuada())
                        && !SituacaoPedido.CANCELADO.equals(situacao) && !SituacaoPedido.DIGITACAO.equals(situacao);

                final boolean acessoCompraPermitido = isAcessoPermitido(TipoAcesso.ADMINISTRACAO,
                        TipoAcesso.CADASTRO_PEDIDO_COMPRA);

                final boolean acessoAlteracaoComissaoPermitida = !pedido.isNovo() && pedido.isRevendaEfetuada()
                        && isAcessoPermitido(TipoAcesso.ADMINISTRACAO, TipoAcesso.GERENCIA_VENDAS);

                addAtributo("pedidoDesabilitado", isPedidoDesabilitado(pedido));
                addAtributo("acessoEnvioPedidoPermitido", acessoEnvioPedidoPermitido);
                addAtributo("acessoReenvioPedidoPermitido", acessoReenvioPedidoPermitido);
                addAtributo("acessoCancelamentoPedidoPermitido", acessoCancelamentoPedidoPermitido);
                addAtributo("acessoRefazerPedidoPermitido", acessoRefazerPedidoPermitido);
                addAtributo("acessoCompraPermitido", acessoCompraPermitido);
                addAtributo("acessoAlteracaoComissaoPermitida", acessoAlteracaoComissaoPermitida);
            }
        }
        configurarTipoPedido(tipoPedido);
        // Estamos verificando se o pedido pesquisado eh realmente um orcamento
        // pois, por conta do reaproveitamento de codigo, o usuario pode acessar
        // um pedido de vendas na tela de orcamento. Caso tenha sido pesquisado
        // um pedido de vendas na tela de orcamento devemos direcionar o usuario
        // para a tela de vendas
        redirecionarHome(tipoPedido, pedido == null ? orcamento : pedido.isOrcamento(), true);
    }

    @Get("pedido/listagem")
    public void pesquisarPedidoByIdCliente(Integer idCliente, Integer idVendedor, Integer idFornecedor,
            TipoPedido tipoPedido, boolean orcamento, Integer paginaSelecionada, ItemPedido itemVendido) {

        super.pesquisarPedidoByIdCliente(idCliente, idVendedor, idFornecedor, tipoPedido, orcamento, paginaSelecionada,
                itemVendido);

        configurarTipoPedido(tipoPedido);
        redirecionarHome(tipoPedido, orcamento, false);
    }

    private void redirecionarHome(TipoPedido tipoPedido, boolean orcamento, boolean topoPagina) {
        if (!orcamento && TipoPedido.COMPRA.equals(tipoPedido)) {
            redirecTo(this.getClass()).pedidoCompraHome();
        } else {
            redirecTo(this.getClass()).pedidoVendaHome();
        }
        if (topoPagina) {
            ancorarTopo();
        } else {
            ancorarRodape();
        }
    }

    @Post("pedido/refazer")
    public void refazerPedido(Integer idPedido, TipoPedido tipoPedido, boolean orcamento) {
        try {
            Integer idPedidoClone = pedidoService.refazerPedido(idPedido);
            pesquisarPedidoById(idPedidoClone, tipoPedido, orcamento);
            gerarMensagemSucesso("Pedido No. " + idPedidoClone + " inserido e refeito a partir do pedido No. "
                    + idPedido);
            addAtributo("orcamento", orcamento);

        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
            pesquisarPedidoById(idPedido, tipoPedido, orcamento);
        } catch (Exception e) {
            gerarLogErroRequestAjax("copia do pedido de No. " + idPedido, e);
            pesquisarPedidoById(idPedido, tipoPedido, orcamento);
        }
    }

    @Post("pedido/itempedido/remocao/{id}")
    public void removerItemPedido(Integer id) {
        try {
            final PedidoJson json = new PedidoJson(pedidoService.removerItemPedido(id));
            serializarJson(new SerializacaoJson("pedido", json));
        } catch (BusinessException e) {
            serializarJson(new SerializacaoJson("erros", e.getListaMensagem()));
        } catch (Exception e) {
            gerarLogErro("Remo��o do item do pedido", e);
        }
    }

    @Get("pedido/representada/{idRepresentada}/aliquotaIPI/")
    public void verificarRepresentadaCalculaIPI(Integer idRepresentada) {
        final RepresentadaJson json = new RepresentadaJson(idRepresentada,
                this.representadaService.isCalculoIPIHabilitado(idRepresentada));

        serializarJson(new SerializacaoJson("representada", json));
    }

}
