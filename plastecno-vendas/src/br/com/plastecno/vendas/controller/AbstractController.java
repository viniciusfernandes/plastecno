package br.com.plastecno.vendas.controller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.ByteArrayDownload;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.view.Results;
import br.com.plastecno.service.TipoLogradouroService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.controller.exception.ControllerException;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.login.UsuarioInfo;
import br.com.plastecno.vendas.util.ServiceLocator;
import br.com.plastecno.vendas.util.exception.ServiceLocatorException;

public abstract class AbstractController {

    private final String cssMensagemAlerta = "mensagemAlerta";
    private final String cssMensagemErro = "mensagemErro";
    private final String cssMensagemSucesso = "mensagemSucesso";
    private String homePath;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private String nomeTela;

    private final Integer numerRegistrosPorPagina = 10;
    private Picklist picklist;
    private final String possuiMultiplosLogradouros = "possuiMultiplosLogradouros";
    private Result result;
    private TipoLogradouroService tipoLogradouroService;

    private UsuarioInfo usuarioInfo;
    private UsuarioService usuarioService;

    public AbstractController(Result result) {
        this.result = result;
        try {
            this.init();

        } catch (ServiceLocatorException e) {
            this.logger.log(Level.SEVERE, "Falha no lookup de algum servico", e);
            this.result.include("erro",
                    "Falha no localizacao de algum servico. Verifique o log do servidor para maiores detalhes. CAUSA: "
                            + e.getMessage());
            irTelaErro();
        }

        try {
            this.inicializarPathHome();
        } catch (ControllerException e) {
            this.logger.log(Level.SEVERE, "O metodo home do controller " + this.getClass().getName()
                    + " nao foi bem definido", e);
            this.result.include("erro", "Falha inicializacao do PATH do metodo home do controller "
                    + this.getClass().getName() + ". Verifique o log do servidor para maiores detalhes.");
            irTelaErro();
        }
    }

    /*
     * Construtor utilizado para inicializar a sessao do usuario
     */
    public AbstractController(Result result, UsuarioInfo usuarioInfo) {
        this(result);
        this.usuarioInfo = usuarioInfo;
    }

    void addAtributo(String nomeAtributo, Object valorAtributo) {
        this.result.include(nomeAtributo, valorAtributo);
    }

    int calcularIndiceRegistroInicial(Integer paginaSelecionada) {
        if (paginaSelecionada == null || paginaSelecionada <= 1) {
            return 0;
        } else {
            return numerRegistrosPorPagina * (paginaSelecionada - 1);
        }
    }

    private int calcularTotalPaginas(Long totalRegistros) {
        if (totalRegistros == null || totalRegistros <= 0) {
            return 1;
        }
        return (int) Math.ceil(((double) totalRegistros / numerRegistrosPorPagina));
    }

    void carregarVendedor(Cliente cliente) {

        Usuario vendedor = this.usuarioService.pesquisarVendedorByIdCliente(cliente.getId());
        if (vendedor == null) {
            /*
             * Vamos sinalizar o usuario que o cliente que ele pretende efetuar
             * as pesquisas nao possui vendedor.
             */
            vendedor = new Usuario(null, "NÃO POSSUI VENDEDOR");
            vendedor.setSobrenome("");
            vendedor.setEmail("");
        }
        cliente.setVendedor(vendedor);
    }

    void configurarFiltroPediodoMensal() {
        if (!contemAtributo("dataInicial") && !contemAtributo("dataFinal")) {
            Calendar dataInicial = Calendar.getInstance();
            dataInicial.set(Calendar.DAY_OF_MONTH, 1);

            addAtributo("dataInicial", StringUtils.formatarData(dataInicial.getTime()));
            addAtributo("dataFinal", StringUtils.formatarData(new Date()));

        }
    }

    boolean contemAtributo(String nomeAtributo) {
        return this.result.included().containsKey(nomeAtributo);
    }

    void formatarAliquotaItemEstoque(ItemEstoque item) {
        item.setAliquotaICMSFormatado(NumeroUtils.formatarPercentual(item.getAliquotaICMS()));
        item.setAliquotaIPIFormatado(NumeroUtils.formatarPercentual(item.getAliquotaIPI()));
        item.setMargemMinimaLucro(NumeroUtils.gerarPercentual(item.getMargemMinimaLucro()));
    }

    void formatarAliquotaItemPedido(ItemPedido item) {
        item.setAliquotaICMSFormatado(NumeroUtils.formatarPercentual(item.getAliquotaICMS()));
        item.setAliquotaIPIFormatado(NumeroUtils.formatarPercentual(item.getAliquotaIPI()));
    }

    String formatarCNPJ(String conteudo) {
        return StringUtils.formatarCNPJ(conteudo);
    }

    String formatarCPF(String conteudo) {
        return StringUtils.formatarCPF(conteudo);
    }

    String formatarData(Date dataHora) {
        return StringUtils.formatarData(dataHora);
    }

    String formatarDataHora(Date dataHora) {
        return StringUtils.formatarDataHora(dataHora);
    }

    void formatarDocumento(Cliente cliente) {
        cliente.setCnpj(this.formatarCNPJ(cliente.getCnpj()));
        cliente.setCpf(this.formatarCPF(cliente.getCpf()));
        cliente.setInscricaoEstadual(this.formatarInscricaoEstadual(cliente.getInscricaoEstadual()));
    }

    String formatarInscricaoEstadual(String conteudo) {
        return StringUtils.formatarInscricaoEstadual(conteudo);
    }

    void formatarItemEstoque(ItemEstoque item) {
        item.setMedidaExternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaExterna()));
        item.setMedidaInternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaInterna()));
        item.setComprimentoFormatado(NumeroUtils.formatarValorMonetario(item.getComprimento()));
        item.setPrecoMedioFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoMedio()));
        item.setMargemMinimaLucro(NumeroUtils.gerarPercentual(item.getMargemMinimaLucro()));
    }

    void formatarItemEstoque(List<ItemEstoque> itens) {
        for (ItemEstoque item : itens) {
            this.formatarItemEstoque(item);
        }
    }

    void formatarItemPedido(ItemPedido item) {
        item.setAliquotaICMSFormatado(NumeroUtils.formatarPercentual(item.getAliquotaICMS()));
        item.setAliquotaIPIFormatado(NumeroUtils.formatarPercentual(item.getAliquotaIPI()));
        item.setPrecoUnidadeFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoUnidade()));
        item.setPrecoUnidadeIPIFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoUnidadeIPI()));
        item.setPrecoVendaFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoVenda()));
        item.setPrecoItemFormatado(NumeroUtils.formatarValorMonetario(item.calcularPrecoItem()));
        item.setMedidaExternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaExterna()));
        item.setMedidaInternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaInterna()));
        item.setComprimentoFormatado(NumeroUtils.formatarValorMonetario(item.getComprimento()));
        item.setValorPedidoFormatado(NumeroUtils.formatarValorMonetario(item.getValorPedido()));
        item.setValorPedidoIPIFormatado(NumeroUtils.formatarValorMonetario(item.getValorPedidoIPI()));
        item.setAliquotaComissaoFormatado(NumeroUtils.formatarPercentual(item.getAliquotaComissao()));
        
        item.setValorICMSFormatado(String.valueOf(NumeroUtils.arredondarValorMonetario(item.getValorICMS())));
        item.setValorIPIFormatado(String.valueOf(NumeroUtils.arredondarValorMonetario(item.getPrecoUnidadeIPI())));
    }

    void formatarItemPedido(List<ItemPedido> itens) {
        for (ItemPedido item : itens) {
            formatarItemPedido(item);
        }
    }

    void formatarPedido(Pedido pedido) {
        pedido.setDataEnvioFormatada(formatarData(pedido.getDataEnvio()));
        pedido.setDataEntregaFormatada(formatarData(pedido.getDataEntrega()));
        pedido.setValorPedidoFormatado(NumeroUtils.formatarValorMonetario(pedido.getValorPedido()));
        pedido.setValorPedidoIPIFormatado(NumeroUtils.formatarValorMonetario(pedido.getValorPedidoIPI()));
        pedido.setDataEmissaoNFFormatada(formatarData(pedido.getDataEmissaoNF()));
        pedido.setDataVencimentoNFFormatada(formatarData(pedido.getDataVencimentoNF()));
    }

    <T extends AbstractController> T forwardTo(Class<T> classe) {
        if (!result.used()) {
            return this.result.forwardTo(classe);
        }
        return result.of(classe);
    }

    void forwardTo(String path) {
        if (!result.used()) {
            result.forwardTo(path);
        }
    }

    Download gerarDownload(byte[] bytesArquivo, String nomeArquivo, String contentType) {
        return new ByteArrayDownload(bytesArquivo, contentType, StringUtils.removerAcentuacao(nomeArquivo));
    }

    Download gerarDownloadPDF(byte[] bytesArquivo, String nomeArquivo) {
        return gerarDownload(bytesArquivo, nomeArquivo, "application/pdf;");
    }

    void gerarListaMensagemAjax(String mensagem, String categoria) {
        final List<String> lista = new ArrayList<String>();
        lista.add(mensagem);
        this.result.use(Results.json()).from(lista, categoria).serialize();
    }

    void gerarListaMensagemAlerta(BusinessException e) {
        this.result.include("listaMensagem", e.getListaMensagem());
        this.result.include("cssMensagem", cssMensagemAlerta);
    }

    void gerarListaMensagemAlerta(String mensagem) {
        this.result.include("listaMensagem", new String[] {mensagem});
        this.result.include("cssMensagem", cssMensagemAlerta);
    }

    void gerarListaMensagemErro(BusinessException e) {
        this.gerarListaMensagemErro(e.getListaMensagem());
    }

    void gerarListaMensagemErro(List<String> listaMensagem) {
        this.result.include("listaMensagem", listaMensagem);
        this.result.include("cssMensagem", cssMensagemErro);
    }

    void gerarListaMensagemErro(String mensagem) {
        this.result.include("listaMensagem", new String[] {mensagem});
        this.result.include("cssMensagem", cssMensagemErro);
    }

    void gerarListaMensagemErroLogException(BusinessException e) {
        gerarListaMensagemErro(e);
        logger.log(Level.SEVERE, e.getMensagemConcatenada(), e);
    }

    void gerarListaMensagemSucesso(Object o, String nomeAtributoExibicao, TipoOperacao tipoOperacao)
            throws ControllerException {
        Method metodo = null;
        try {
            if (o != null) {
                metodo = o.getClass().getMethod(
                        "get" + nomeAtributoExibicao.substring(0, 1).toUpperCase() + nomeAtributoExibicao.substring(1));
                this.gerarMensagemSucesso(nomeTela + " " + tipoOperacao.getDescricao() + " com sucesso: "
                        + metodo.invoke(o, (Object[]) null));
            } else {
                this.gerarMensagemSucesso(nomeTela + " " + tipoOperacao.getDescricao() + " com sucesso.");
            }

        } catch (Exception e) {
            throw new ControllerException("Não foi possível montar a menagem de sucesso para a inclusao do(a) "
                    + nomeTela);
        }
    }

    void gerarLogErro(String descricaoOperacao) {
        this.logger.log(Level.SEVERE, "Falha na operacao " + descricaoOperacao);
        this.result.include("cssMensagem", cssMensagemErro);
        this.result.include("listaMensagem", new String[] {"Falha na operacao " + descricaoOperacao
                + " .Verifique o log do servidor para maiores detalhes."});
        irTelaErro();
    }

    /*
     * Metodo responsavel por logar ua excecao e redirecionar o usuario para a
     * tela de erro generico.
     */
    void gerarLogErro(String descricaoOperacao, Exception e) {
        this.logger.log(Level.SEVERE, "Falha na operacao " + descricaoOperacao, e);
        this.result.include("cssMensagem", cssMensagemErro);
        this.result.include("listaMensagem", new String[] {"Falha na operacao " + descricaoOperacao
                + " .Verifique o log do servidor para maiores detalhes. CAUSA: " + e.getMessage()});
        irTelaErro();
    }

    void gerarLogErroInclusao(String nomeTela, Exception e) {
        this.gerarLogErro(" inclusao/alteracao de " + nomeTela, e);
    }

    void gerarLogErroNavegacao(String nomeTela, Exception e) {
        this.gerarLogErro(" inicializacao da tela de " + nomeTela, e);
    }

    /*
     * Metodo utilizado para efetuar o log da excecao e enviar o "response" via
     * ajax. Como esperamos que seja uma resposta de uma chamada ajax nao
     * devemos efetuar navegacao alguma tal como, irTopoPagina, redirect,
     * forward, etc
     */
    void gerarLogErroRequestAjax(String descricaoOperacao, Exception e) {
        this.logger.log(Level.SEVERE, "Falha na operacao " + descricaoOperacao, e);
        this.gerarRetornoErroAjax("Não foi possível inserir o pedido. Veja o log para obter mais informações. "
                + "Mensagem: " + e.getMessage());
    }

    void gerarMensagemAlerta(String mensagem) {
        List<String> mensagens = new ArrayList<String>();
        mensagens.add(mensagem);
        this.result.include("listaMensagem", mensagens);
        this.result.include("cssMensagem", cssMensagemAlerta);
    }

    void gerarMensagemCadastroSucesso(Object o, String nomeAtributoExibicao) throws ControllerException {
        this.gerarListaMensagemSucesso(o, nomeAtributoExibicao, TipoOperacao.CADASTRO);
    }

    void gerarMensagemRemocaoSucesso() throws ControllerException {
        this.gerarListaMensagemSucesso(null, null, TipoOperacao.REMOCAO);
    }

    void gerarMensagemSucesso(String mensagem) {
        List<String> mensagens = new ArrayList<String>();
        mensagens.add(mensagem);
        this.result.include("listaMensagem", mensagens);
        this.result.include("cssMensagem", cssMensagemSucesso);
    }

    List<PicklistElement> gerarPicklistElement() {
        return null;
    }

    void gerarRetornoAlertaAjax(String mensagem) {
        this.gerarListaMensagemAjax(mensagem, "alertas");
    }

    void gerarRetornoErroAjax(String mensagem) {
        this.gerarListaMensagemAjax(mensagem, "erros");
    }

    Object getAtributo(String nomeAtributo) {
        return this.result.included().get(nomeAtributo);
    }

    Integer getCodigoUsuario() {
        return usuarioInfo.getCodigoUsuario();
    }

    String getNomeTela() {
        return nomeTela;
    }

    Integer getNumerRegistrosPorPagina() {
        return numerRegistrosPorPagina;
    }

    Usuario getUsuario() {
        return usuarioService.pesquisarById(getCodigoUsuario());
    }

    void habilitarMultiplosLogradouros() {
        this.result.include(possuiMultiplosLogradouros, true);
    }

    boolean hasAtributo(Object obj) {
        for (Method metodo : obj.getClass().getMethods()) {
            try {
                if (!"getClass".equals(metodo.getName()) && metodo.getName().startsWith("get")
                        && metodo.invoke(obj, (Object[]) null) != null) {
                    return true;
                }
            } catch (Exception e) {
                continue;
            }
        }
        return false;
    }

    void inicializarComboTipoLogradouro() {
        this.result.include("listaTipoLogradouro", this.tipoLogradouroService.pesquisar());
        this.result.include("tipoLogradouroRenderizado", true);
    }

    /*
     * Esse metodo ja garante que o usuario sera navegado para o rodape da
     * pagina
     */
    private void inicializarPaginacao(Integer paginaSelecionada, Integer totalPaginas, Object objetoPaginado,
            String nomeObjetoPaginado) {
        if (paginaSelecionada == null || paginaSelecionada <= 1) {
            paginaSelecionada = 1;
        }
        this.result.include("paginaSelecionada", paginaSelecionada);
        this.result.include("totalPaginas", totalPaginas);
        this.result.include(nomeObjetoPaginado, objetoPaginado);
        irRodapePagina();
    }

    <T> void inicializarPaginacao(Integer paginaSelecionada, PaginacaoWrapper<T> paginacao, String nomeLista) {
        inicializarPaginacao(paginaSelecionada, calcularTotalPaginas(paginacao.getTotalPaginado()),
                paginacao.getLista(), nomeLista);
    }

    /*
     * Metodo utilizado para definir qual eh o metodo HOME definido em cada
     * controller, isto eh, o PATH que aponta para a tela inicial
     */
    private void inicializarPathHome() throws ControllerException {
        String nome = this.getClass().getSimpleName().replace("Controller", "");
        nome += "Home";
        Method[] metodos = this.getClass().getDeclaredMethods();
        for (Method metodo : metodos) {
            if (metodo.getName().equalsIgnoreCase(nome)) {
                Get get = metodo.getAnnotation(Get.class);
                if (get == null) {
                    throw new ControllerException("É obrigatório que o método home " + metodo.getName()
                            + " seja anotado com @Get");
                }
                this.homePath = "/" + get.value()[0];
                break;
            }
        }
    }

    void inicializarPicklist(String tituloBloco, String tituloElementosNaoAssociados, String tituloElementosAssociados,
            String nomeAtributoValor, String nomeAtributoLabel) {

        this.inicializarPicklist(tituloBloco, tituloElementosNaoAssociados, tituloElementosAssociados,
                nomeAtributoValor, nomeAtributoLabel, true);
    }

    final void inicializarPicklist(String tituloBloco, String tituloElementosNaoAssociados,
            String tituloElementosAssociados, String nomeAtributoValor, String nomeAtributoLabel,
            boolean preenchimentoObrigatorio) {

        this.picklist = new Picklist(this.result);
        this.picklist.setTituloBloco(tituloBloco);
        this.picklist.setTituloElementosNaoAssociados(tituloElementosNaoAssociados);
        this.picklist.setTituloElementosAssociados(tituloElementosAssociados);
        this.picklist.setNomeAtributoValor(nomeAtributoValor);
        this.picklist.setNomeAtributoLabel(nomeAtributoLabel);
        this.picklist.setPreenchimentoObrigatorio(preenchimentoObrigatorio);
    }

    void inicializarPicklist(String tituloBloco, String tituloElementosNaoAssociados, String tituloElementosAssociados,
            String nomeAtributoValor, String nomeAtributoLabel, boolean preenchimentoObrigatorio,
            String tituloCampoPesquisa) {
        this.inicializarPicklist(tituloBloco, tituloElementosNaoAssociados, tituloElementosAssociados,
                nomeAtributoValor, nomeAtributoLabel);
        this.picklist.setTituloCampoPesquisa(tituloCampoPesquisa);
    }

    void inicializarPicklist(String tituloBloco, String tituloElementosNaoAssociados, String tituloElementosAssociados,
            String nomeAtributoValor, String nomeAtributoLabel, String tituloCampoPesquisa) {
        this.inicializarPicklist(tituloBloco, tituloElementosNaoAssociados, tituloElementosAssociados,
                nomeAtributoValor, nomeAtributoLabel, true, tituloCampoPesquisa);
    }

    <T, K> void inicializarRelatorioPaginado(Integer paginaSelecionada, RelatorioWrapper<T, K> relatorio,
            String nomeRelatorio) {
        inicializarPaginacao(paginaSelecionada,
                calcularTotalPaginas((Long) relatorio.getPropriedade("totalPesquisado")), relatorio, nomeRelatorio);
    }

    void init() throws ServiceLocatorException {
        this.tipoLogradouroService = ServiceLocator.locate(TipoLogradouroService.class);
        this.usuarioService = ServiceLocator.locate(UsuarioService.class);

        Field[] listaCampos = this.getClass().getDeclaredFields();
        for (Field campo : listaCampos) {
            if (campo.isAnnotationPresent(Servico.class)) {
                try {
                    campo.setAccessible(true);
                    campo.set(this, ServiceLocator.locate(campo.getType()));
                } catch (Exception e) {
                    throw new ServiceLocatorException("Falha na inicilizacao dos servicos do controller", e);
                } finally {
                    campo.setAccessible(false);
                }
            }
        }
    }

    void irPaginaHome() {
        if (this.homePath == null) {
            throw new IllegalStateException("O controller " + this.getClass().getName() + " nao possui um metodo HOME");
        }
        redirecTo(this.homePath);
    }

    final void irRodapePagina() {
        this.irPaginaHome();
        this.result.include("ancora", "rodape");
    }

    private void irTelaErro() {
        this.result.forwardTo(ErroController.class).erroHome();
        this.result.include("ancora", "topo");
    }

    void irTopoPagina() {
        this.irPaginaHome();
        this.result.include("ancora", "topo");
    }

    boolean isAcessoPermitido(TipoAcesso... tipoAcesso) {
        return this.usuarioInfo.isAcessoPermitido(tipoAcesso);
    }

    boolean isElementosAssociadosPreenchidosPicklist() {
        return this.picklist.isElementosAssociadosPreenchidos();
    }

    boolean isElementosNaoAssociadosPreenchidosPicklist() {
        return this.picklist.isElementosNaoAssociadosPreenchidos();
    }

    /*
     * Esse metodo ja garante que o usuario sera navegado para o rodape da
     * pagina
     */
    void paginarPesquisa(Integer paginaSelecionada, Long totalRegistros) {
        if (paginaSelecionada == null || paginaSelecionada <= 1) {
            paginaSelecionada = 1;
        }
        this.result.include("paginaSelecionada", paginaSelecionada);
        this.result.include("totalPaginas", this.calcularTotalPaginas(totalRegistros));
        irRodapePagina();
    }

    void popularPicklist(List<?> elementosNaoAssociados, List<?> elementosAssociados) throws ControllerException {
        this.picklist.popular(elementosNaoAssociados, elementosAssociados);
    }

    <T extends AbstractController> T redirecTo(Class<T> classe) {
        if (!result.used()) {
            return this.result.redirectTo(classe);
        }
        return result.of(classe);
    }

    void redirecTo(String path) {
        if (!result.used()) {
            result.redirectTo(path);
        }
    }

    String removerMascaraDocumento(String documento) {
        return StringUtils.removerMascaraDocumento(documento);
    }

    void serializarJson(SerializacaoJson serializacaoJson) {
        if (serializacaoJson == null) {
            return;
        }

        Serializer serializer = null;
        if (serializacaoJson.contemNome()) {
            serializer = this.result.use(Results.json()).from(serializacaoJson.getObjeto(), serializacaoJson.getNome());
        } else {
            serializer = this.result.use(Results.json()).from(serializacaoJson.getObjeto());
        }

        if (serializacaoJson.isRecursivo()) {
            serializer = serializer.recursive();
        }

        if (serializacaoJson.contemInclusaoAtributo()) {
            serializer.include(serializacaoJson.getAtributoInclusao());
        }

        if (serializacaoJson.contemExclusaoAtributo()) {
            serializer.exclude(serializacaoJson.getAtributoExclusao());
        }
        serializer.serialize();
    }

    void setNomeTela(String nomeTela) {
        this.nomeTela = nomeTela;
    }

    boolean temElementos(Collection<?> lista) {
        return lista != null && !lista.isEmpty();
    }

    void verificarPermissaoAcesso(String nomePermissaoAcesso, TipoAcesso... listaTipoAcesso) {

        if (this.usuarioInfo == null) {
            this.gerarLogErro("acesso a tela " + this.nomeTela + ", pois o usuario nao foi autenticado pelo sistema. "
                    + "Garanta que a sessao do usuario foi contruida utilizando "
                    + "o contrutor do controller adequado.");
        } else if (listaTipoAcesso != null) {
            for (TipoAcesso tipoAcesso : listaTipoAcesso) {
                if (this.usuarioInfo.isAcessoPermitido(tipoAcesso)) {
                    this.addAtributo(nomePermissaoAcesso, true);
                    break;
                }
            }
        }
    }
}

class Autocomplete {
    private String label;
    private Integer valor;

    public Autocomplete(Integer valor, String label) {
        this.valor = valor;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getValor() {
        return valor;
    }
}

class Picklist {
    private final String associados = "listaElementosAssociados";
    private List<PicklistElement> listaElementosAssociados;
    private List<PicklistElement> listaElementosNaoAssociados;
    private final String naoAssociados = "listaElementosNaoAssociados";

    private String nomeAtributoLabel;
    private String nomeAtributoValor;

    private final Result result;

    public Picklist(Result result) {
        this.result = result;
    }

    public boolean isElementosAssociadosPreenchidos() {
        return this.result.included().get(associados) != null;
    }

    public boolean isElementosNaoAssociadosPreenchidos() {
        return this.result.included().get(naoAssociados) != null;
    }

    void popular(List<?> elementosNaoAssociados, List<?> elementosAssociados) throws ControllerException {

        if (this.nomeAtributoLabel == null || this.nomeAtributoValor == null) {
            throw new ControllerException(
                    "O nome do atributo para preencher o label e o nome atributo para preencher o valor dos elementos "
                            + "do picklist sao obrigatorios");
        }

        Field valor = null;
        Field label = null;
        if (elementosNaoAssociados != null) {

            this.listaElementosNaoAssociados = new ArrayList<PicklistElement>(30);

            for (Object object : elementosNaoAssociados) {
                try {
                    valor = object.getClass().getDeclaredField(this.nomeAtributoValor);
                    label = object.getClass().getDeclaredField(this.nomeAtributoLabel);

                    valor.setAccessible(true);
                    label.setAccessible(true);

                    this.listaElementosNaoAssociados.add(new PicklistElement(valor.get(object), label.get(object)));

                    valor.setAccessible(false);
                    label.setAccessible(false);
                } catch (Exception e) {
                    throw new IllegalStateException(
                            "Não foi possível montar o picklist da tela para os elementos nao associados", e);
                }
            }
        }

        if (elementosAssociados != null) {

            this.listaElementosAssociados = new ArrayList<PicklistElement>(30);

            for (Object object : elementosAssociados) {
                try {
                    valor = object.getClass().getDeclaredField(this.nomeAtributoValor);
                    label = object.getClass().getDeclaredField(this.nomeAtributoLabel);

                    valor.setAccessible(true);
                    label.setAccessible(true);

                    this.listaElementosAssociados.add(new PicklistElement(valor.get(object), label.get(object)));

                    valor.setAccessible(false);
                    label.setAccessible(false);
                } catch (Exception e) {
                    throw new IllegalStateException(
                            "Não foi possível montar o picklist da tela para os elementos associados", e);
                }
            }
        }

        this.result.include(naoAssociados, this.listaElementosNaoAssociados);
        this.result.include(associados, this.listaElementosAssociados);
    }

    public void setNomeAtributoLabel(String nomeAtributoLabel) {
        this.nomeAtributoLabel = nomeAtributoLabel;
    }

    public void setNomeAtributoValor(String nomeAtributoValor) {
        this.nomeAtributoValor = nomeAtributoValor;
    }

    public void setPreenchimentoObrigatorio(boolean preenchimentoObrigatorio) {
        this.result.include("preenchimentoPicklistObrigatorio", preenchimentoObrigatorio);
    }

    public void setTituloBloco(String tituloBloco) {
        this.result.include("tituloPicklist", tituloBloco);
    }

    public void setTituloCampoPesquisa(String tituloCampoPesquisa) {
        this.result.include("tituloCampoPesquisa", tituloCampoPesquisa);
        this.result.include("possuiCampoPesquisa", true);
    }

    public void setTituloElementosAssociados(String tituloElementosAssociados) {
        this.result.include("labelElementosAssociados", tituloElementosAssociados);
    }

    public void setTituloElementosNaoAssociados(String tituloElementosNaoAssociados) {
        this.result.include("labelElementosNaoAssociados", tituloElementosNaoAssociados);
    }
}

enum TipoOperacao {
    CADASTRO("cadastro(a)"), ENVIO("Envio"), REMOCAO("removido(a)");
    private final String descricao;

    private TipoOperacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
