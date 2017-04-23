package br.com.plastecno.vendas.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ConfiguracaoSistemaService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoUF;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.DadosNFe;
import br.com.plastecno.service.nfe.DeclaracaoImportacao;
import br.com.plastecno.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.ICMSInterestadual;
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.IdentificacaoNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.ProdutoServicoNFe;
import br.com.plastecno.service.nfe.TransportadoraNFe;
import br.com.plastecno.service.nfe.TransporteNFe;
import br.com.plastecno.service.nfe.constante.TipoAliquotaICMSInterestadual;
import br.com.plastecno.service.nfe.constante.TipoAliquotaICMSPartilha;
import br.com.plastecno.service.nfe.constante.TipoDesoneracaoICMS;
import br.com.plastecno.service.nfe.constante.TipoDestinoOperacao;
import br.com.plastecno.service.nfe.constante.TipoEmissao;
import br.com.plastecno.service.nfe.constante.TipoFinalidadeEmissao;
import br.com.plastecno.service.nfe.constante.TipoFormaPagamento;
import br.com.plastecno.service.nfe.constante.TipoImpressaoNFe;
import br.com.plastecno.service.nfe.constante.TipoIntermediacaoImportacao;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMS;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMSST;
import br.com.plastecno.service.nfe.constante.TipoModalidadeFrete;
import br.com.plastecno.service.nfe.constante.TipoNFe;
import br.com.plastecno.service.nfe.constante.TipoOperacaoConsumidorFinal;
import br.com.plastecno.service.nfe.constante.TipoOperacaoNFe;
import br.com.plastecno.service.nfe.constante.TipoOrigemMercadoria;
import br.com.plastecno.service.nfe.constante.TipoPresencaComprador;
import br.com.plastecno.service.nfe.constante.TipoRegimeTributacao;
import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;
import br.com.plastecno.service.nfe.constante.TipoTributacaoISS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.json.ProdutoServicoJson;
import br.com.plastecno.vendas.json.SerializacaoJson;
import br.com.plastecno.vendas.json.TransportadoraJson;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class EmissaoNFeController extends AbstractController {
    @Servico
    private ClienteService clienteService;

    @Servico
    private ConfiguracaoSistemaService configuracaoSistemaService;

    private List<Object[]> listaCfop = null;

    @Servico
    private NFeService nFeService;
    @Servico
    private PedidoService pedidoService;
    @Servico
    private TransportadoraService transportadoraService;

    public EmissaoNFeController(HttpServletRequest request, Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Ramo de atividade");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.CADASTRO_BASICO);

        inicializarListaCfop(request);
    }

    @Get("emissaoNFe/valorICMSInterestadual")
    public void calcularValorICMSInterestadual(ICMSInterestadual icms) {
        icms.carregarValores();
        icms.setValorFCPDestino(NumeroUtils.arredondarValorMonetario(icms.getValorFCPDestino()));
        icms.setValorUFDestino(NumeroUtils.arredondarValorMonetario(icms.getValorUFDestino()));
        icms.setValorUFRemetente(NumeroUtils.arredondarValorMonetario(icms.getValorUFRemetente()));
        serializarJson(new SerializacaoJson("icms", icms));
    }

    @Get("emissaoNFe")
    public void emissaoNFeHome() {
        addAtributo("listaTipoAliquotaICMSInterestadual", TipoAliquotaICMSInterestadual.values());
        addAtributo("listaTipoAliquotaICMSPartilha", TipoAliquotaICMSPartilha.values());
        addAtributo("listaTipoUF", TipoUF.values());
        addAtributo("listaTipoIntermediacaoImportacao", TipoIntermediacaoImportacao.values());
        addAtributo("listaTipoPresencaComprador", TipoPresencaComprador.values());
        addAtributo("listaTipoDestinoOperacao", TipoDestinoOperacao.values());
        addAtributo("listaTipoOperacaoConsumidorFinal", TipoOperacaoConsumidorFinal.values());
        addAtributo("listaTipoOperacao", TipoOperacaoNFe.values());
        addAtributo("listaTipoFinalidadeEmissao", TipoFinalidadeEmissao.values());
        addAtributo("listaTipoFormaPagamento", TipoFormaPagamento.values());
        addAtributo("listaTipoEmissao", TipoEmissao.values());
        addAtributo("listaTipoTributacaoICMS", TipoTributacaoICMS.values());
        addAtributo("listaTipoOrigemMercadoria", TipoOrigemMercadoria.values());
        addAtributo("listaTipoModalidadeDeterminacaoBCICMS", TipoModalidadeDeterminacaoBCICMS.values());
        addAtributo("listaTipoModalidadeDeterminacaoBCICMSST", TipoModalidadeDeterminacaoBCICMSST.values());
        addAtributo("listaTipoDesoneracao", TipoDesoneracaoICMS.values());
        addAtributo("listaTipoTributacaoIPI", TipoTributacaoIPI.values());
        addAtributo("listaTipoTributacaoPIS", TipoTributacaoPIS.values());
        addAtributo("listaTipoTributacaoCOFINS", TipoTributacaoCOFINS.values());
        addAtributo("listaTipoTributacaoISS", TipoTributacaoISS.values());
        addAtributo("listaTipoModalidadeFrete", TipoModalidadeFrete.values());
        addAtributo("listaTipoImpressao", TipoImpressaoNFe.values());
        addAtributo("listaTipoRegimeTributacao", TipoRegimeTributacao.values());
        addAtributo("percentualCofins",
                configuracaoSistemaService.pesquisar(ParametroConfiguracaoSistema.PERCENTUAL_COFINS));
        addAtributo("percentualPis", configuracaoSistemaService.pesquisar(ParametroConfiguracaoSistema.PERCENTUAL_PIS));
        addAtributo("listaCfop", listaCfop);

        // DEfinindo os valores padrao de pre-preenchimento da tela
        addAtributoPadrao("finalidadeEmissaoSelecionada", TipoFinalidadeEmissao.NORMAL.getCodigo());
        addAtributoPadrao("formaPagamentoSelecionada", TipoFormaPagamento.PRAZO.getCodigo());
        addAtributoPadrao("tipoEmissaoSelecionada", TipoEmissao.NORMAL.getCodigo());
        addAtributoPadrao("tipoImpressaoSelecionada", TipoImpressaoNFe.RETRATO.getCodigo());
        addAtributoPadrao("tipoPresencaSelecionada", TipoPresencaComprador.NAO_PRESENCIAL_OUTROS.getCodigo());
        addAtributoPadrao("tipoOperacaoSelecionada", TipoOperacaoNFe.SAIDA.getCodigo());
        addAtributoPadrao("modalidadeFreteSelecionada", TipoModalidadeFrete.DESTINATARIO_REMETENTE.getCodigo());
    }

    @Post("emissaoNFe/emitirNFe")
    public void emitirNFe(DadosNFe nf, TipoNFe tipoNFe, Logradouro logradouro, String telefoneDestinatario,
            Integer idPedido) {
        String numeroNFe = null;
        try {

            nf.getIdentificacaoDestinatarioNFe().setEnderecoDestinatarioNFe(
                    nFeService.gerarEnderecoNFe(logradouro, telefoneDestinatario));
            formatarDatas(nf, false);
            ordenarListaDetalhamentoProduto(nf);

            if (tipoNFe == null) {
                tipoNFe = TipoNFe.ENTRADA;
            }

            // REFATORAR ESSA IMPLEMENTACAO POIS AQUI EH UM PONTO DE
            // COMPLEXIDADE ACICLOMATIA. UTILIZAR LAMBDA EXPESSIONS.
            if (TipoNFe.DEVOLUCAO.equals(tipoNFe)) {
                numeroNFe = nFeService.emitirNFeDevolucao(new NFe(nf), idPedido);
            } else if (TipoNFe.ENTRADA.equals(tipoNFe)) {
                numeroNFe = nFeService.emitirNFeEntrada(new NFe(nf), idPedido);
            } else if (TipoNFe.TRIANGULARIZACAO.equals(tipoNFe)) {
                numeroNFe = nFeService.emitirNFeTriangularizacao(new NFe(nf), idPedido);
            }

            gerarMensagemSucesso("A NFe de número " + numeroNFe + " do pedido No. " + idPedido
                    + " foi gerada com sucesso.");
        } catch (BusinessException e) {
            try {
                formatarDatas(nf, true);
            } catch (BusinessException e1) {
                e.addMensagem(e1.getListaMensagem());
            }
            popularNFe(nf, idPedido);
            gerarListaMensagemErro(e);
            redirecTo(this.getClass()).emissaoNFeHome();
            irTopoPagina();
        } catch (Exception e) {
            gerarLogErro("Emissão da NFe", e);
        }

        irTopoPagina();
    }

    // Aqui uma excessao eh lancada pois devemos concatenar com as outras
    // mensagens vindos do servidor, veja o metodo de missao de nfe
    private void formatarDatas(DadosNFe nf, boolean fromServidor) throws BusinessException {
        // Formatando as duplicatas
        List<DuplicataNFe> lista = null;
        if (nf != null && (lista = nf.getListaDuplicata()) == null) {
            return;
        }

        String pTo = null;
        String pFrom = null;
        if (fromServidor) {
            pTo = "dd/MM/yyyy";
            pFrom = "yyyy-MM-dd";
        } else {
            pTo = "yyyy-MM-dd";
            pFrom = "dd/MM/yyyy";
        }
        SimpleDateFormat from = new SimpleDateFormat(pFrom);
        SimpleDateFormat to = new SimpleDateFormat(pTo);

        for (DuplicataNFe d : lista) {
            try {
                if (d.getDataVencimento() == null) {
                    continue;
                }
                d.setDataVencimento(to.format(from.parse(d.getDataVencimento())));
            } catch (ParseException e) {
                throw new BusinessException("Não foi possível formatar a data de vencimento da duplicata "
                        + d.getNumero() + ". O valor enviado é \"" + d.getDataVencimento() + "\"");
            }
        }

        if (nf.getListaDetalhamentoProdutoServicoNFe() != null) {
            ProdutoServicoNFe p = null;
            for (DetalhamentoProdutoServicoNFe d : nf.getListaDetalhamentoProdutoServicoNFe()) {
                p = d.getProduto();
                if (p.contemImportacao()) {
                    for (DeclaracaoImportacao i : p.getListaDeclaracaoImportacao()) {
                        try {
                            if (i.getDataDesembaraco() != null) {
                                i.setDataDesembaraco(to.format(from.parse(i.getDataDesembaraco())));
                            }
                        } catch (ParseException e) {
                            throw new BusinessException(
                                    "Não foi possível formatar a data de desembaraço da importação do produto "
                                            + d.getNumeroItem() + ". O valor enviado é \"" + i.getDataDesembaraco()
                                            + "\"");
                        }

                        try {
                            if (i.getDataImportacao() != null) {
                                i.setDataImportacao(to.format(from.parse(i.getDataImportacao())));
                            }
                        } catch (ParseException e) {
                            throw new BusinessException("Não foi possível formatar a data da importação do produto "
                                    + d.getNumeroItem() + ". O valor enviado é \"" + i.getDataImportacao() + "\"");
                        }
                    }
                }
            }
        }

        IdentificacaoNFe i = nf.getIdentificacaoNFe();
        String dh = null;
        if (!fromServidor && (StringUtils.isNotEmpty(dh = i.getDataSaida()))) {
            try {
                Calendar c = Calendar.getInstance();
                c.setTime(from.parse(dh));
                String[] hora = null;
                if (StringUtils.isNotEmpty(i.getHoraSaida()) && (hora = i.getHoraSaida().split(":")).length > 0) {
                    c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora[0]));
                    c.set(Calendar.MINUTE, Integer.parseInt(hora[1]));
                }
                i.setDataHoraEntradaSaidaProduto(StringUtils.formatarDataHoraTimezone(c.getTime()));
            } catch (ParseException e) {
                throw new BusinessException(
                        "Não foi possível formatar a data/hora de entrada/saida do produto. O valor enviado é \"" + dh
                                + "\"");
            }
        } else if (fromServidor && StringUtils.isNotEmpty(dh = i.getDataHoraEntradaSaidaProduto())) {
            Date dt = StringUtils.gerarDataHoraTimezone(dh);
            i.setDataSaida(StringUtils.formatarData(dt));
            i.setHoraSaida(StringUtils.formatarHora(dt));
        }
    }

    private List<Object[]> gerarListaCfop() {
        List<Object[]> l = nFeService.pesquisarCFOP();
        for (Object[] cfop : l) {
            cfop[1] = cfop[0] + " - " + cfop[1];
            if (cfop[1].toString().length() > 150) {
                cfop[1] = cfop[1].toString().substring(0, 150);
            }
        }
        return l;
    }

    private List<ProdutoServicoJson> gerarListaProduto(List<Object[]> listaValores) {
        List<ProdutoServicoJson> l = new ArrayList<ProdutoServicoJson>();

        if (listaValores == null || listaValores.size() <= 0) {
            return l;
        }
        ProdutoServicoJson p = null;
        for (Object[] val : listaValores) {
            p = new ProdutoServicoJson();
            p.setAliquotaICMS(NumeroUtils.arredondarValorMonetario((Double) val[0]));
            p.setAliquotaIPI(NumeroUtils.arredondarValorMonetario((Double) val[1]));
            p.setCfop((String) val[2]);
            p.setDescricao((String) val[3]);
            p.setNcm((String) val[4]);
            p.setNumeroItem((Integer) val[5]);
            p.setQuantidade((Integer) val[6]);
            p.setUnidadeComercial((String) val[7]);
            p.setValorTotalBruto(NumeroUtils.arredondarValorMonetario((Double) val[8]));
            p.setValorUnitarioComercializacao(NumeroUtils.arredondarValorMonetario((Double) val[9]));
            p.setValorUnitarioTributavel(NumeroUtils.arredondarValorMonetario((Double) val[10]));
            p.setValorICMS(NumeroUtils.arredondarValorMonetario((Double) val[11]));
            p.setValorIPI(NumeroUtils.arredondarValorMonetario((Double) val[12]));
            p.setCodigo((String) val[13]);
            l.add(p);
        }
        return l;
    }

    private List<ProdutoServicoJson> gerarListaProdutoDetalhamento(List<DetalhamentoProdutoServicoNFe> listaDetalhamento) {
        return gerarListaProduto(gerarListaValoresDetalhamento(listaDetalhamento));
    }

    private List<ProdutoServicoJson> gerarListaProdutoItemPedido(List<ItemPedido> listaItem) {
        return gerarListaProduto(gerarListaValoresItemPedido(listaItem));
    }

    private List<Object[]> gerarListaValoresDetalhamento(List<DetalhamentoProdutoServicoNFe> lista) {
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        List<Object[]> l = new ArrayList<Object[]>();
        Object[] val = null;
        ProdutoServicoNFe p = null;
        for (DetalhamentoProdutoServicoNFe i : lista) {
            p = i.getProduto();
            val = new Object[14];
            val[0] = i.contemICMS() ? i.getTributos().getIcms().getTipoIcms().getAliquota() : (Double) 0.0;
            val[1] = i.contemIPI() ? i.getTributos().getIpi().getTipoIpi().getAliquota() : (Double) 0.0;
            val[2] = p.getCfop();
            val[3] = p.getDescricao();
            val[4] = p.getNcm();
            val[5] = i.getNumeroItem();
            val[6] = p.getQuantidadeTributavel();
            val[7] = p.getUnidadeComercial();
            val[8] = p.getValorTotalBruto();
            val[9] = p.getValorUnitarioComercializacao();
            val[10] = p.getValorUnitarioComercializacao();
            val[11] = i.contemICMS() ? i.getTributos().getIcms().getTipoIcms().getValor() : (Double) 0.0;
            val[12] = i.contemIPI() ? i.getTributos().getIpi().getTipoIpi().getValor() : (Double) 0.0;
            val[13] = p.getCodigo();
            l.add(val);
        }
        return l;
    }

    private List<Object[]> gerarListaValoresItemPedido(List<ItemPedido> lista) {
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        List<Object[]> l = new ArrayList<Object[]>();
        Object[] val = null;
        for (ItemPedido i : lista) {
            val = new Object[14];
            val[0] = NumeroUtils.gerarPercentual(i.getAliquotaICMS());
            val[1] = NumeroUtils.gerarPercentual(i.getAliquotaIPI());
            val[2] = "";
            val[3] = i.getDescricaoItemMaterial();
            val[4] = StringUtils.removerMascaraDocumento(i.getNcm());
            val[5] = i.getSequencial();
            val[6] = i.getQuantidade();
            val[7] = i.getTipoVenda().toString();
            val[8] = NumeroUtils.arredondarValorMonetario(i.calcularPrecoTotal());
            val[9] = NumeroUtils.arredondarValorMonetario(i.getPrecoUnidade());
            val[10] = NumeroUtils.arredondarValorMonetario(i.getPrecoUnidade());
            val[11] = NumeroUtils.arredondarValorMonetario(i.calcularValorICMS());
            val[12] = NumeroUtils.arredondarValorMonetario(i.calcularValorIPI());
            val[13] = i.gerarCodigo();
            l.add(val);
        }
        return l;
    }

    private TipoFormaPagamento gerarTipoFormaPagamento(int numeroDuplicata) {
        if (numeroDuplicata <= 0) {
            return TipoFormaPagamento.VISTA;
        }
        return TipoFormaPagamento.PRAZO;
    }

    @SuppressWarnings("unchecked")
    private void inicializarListaCfop(HttpServletRequest request) {
        if ((listaCfop = (List<Object[]>) request.getServletContext().getAttribute("listaCfop")) != null) {
            return;
        }
        listaCfop = gerarListaCfop();
        request.getServletContext().setAttribute("listaCfop", listaCfop);
    }

    private void ordenarListaDetalhamentoProduto(DadosNFe nf) {
        if (nf == null || nf.getListaDetalhamentoProdutoServicoNFe() == null) {
            return;
        }
        Collections.sort(nf.getListaDetalhamentoProdutoServicoNFe(), new Comparator<DetalhamentoProdutoServicoNFe>() {
            @Override
            public int compare(DetalhamentoProdutoServicoNFe d1, DetalhamentoProdutoServicoNFe d2) {
                Integer n1 = d1.getNumeroItem();
                Integer n2 = d2.getNumeroItem();
                return n1 != null ? n1.compareTo(n2) : -1;
            }
        });

    }

    @Get("emissaoNFe/NFe")
    public void pesquisarNFe(Integer numeroNFe) {
        NFe nFe = null;
        try {
            nFe = nFeService.gerarNFeByNumero(numeroNFe);
        } catch (BusinessException e) {
            gerarListaMensagemErroLogException(e);
        }

        if (nFe != null) {
            popularNFe(nFe.getDadosNFe(), nFeService.pesquisarIdPedidoByNumeroNFe(numeroNFe));
            try {
                formatarDatas(nFe.getDadosNFe(), true);
            } catch (BusinessException e) {
                gerarListaMensagemErro(e);
            }
            IdentificacaoNFe i = nFe.getDadosNFe().getIdentificacaoNFe();
            addAtributo("dataSaida", i.getDataSaida());
            addAtributo("horaSaida", i.getHoraSaida());
        }
        irTopoPagina();
    }

    @Get("emissaoNFe/pedido")
    public void pesquisarPedidoById(Integer idPedido) {

        try {
            /*
             * Essa validacao eh necessaria pois o usuario nao pode acessar um
             * pedido que nao podera ser emitido
             */
            nFeService.validarEmissaoNFePedido(idPedido);

            Cliente cliente = pedidoService.pesquisarClienteResumidoByIdPedido(idPedido);
            List<DuplicataNFe> listaDuplicata = nFeService.gerarDuplicataDataLatinaByIdPedido(idPedido);
            Object[] telefone = pedidoService.pesquisarTelefoneContatoByIdPedido(idPedido);

            /*
             * Acho que podemos usar um metodo que carregue menos informacoes
             * dos itens. Estamos trazendo tudo do banco de dados
             */
            // Pesquisando apenas os itens que tem quantidade para ser
            // fracionada
            List<ItemPedido> listaItem = nFeService.pesquisarQuantitadeItemRestanteByIdPedido(idPedido);

            String nomeVend = pedidoService.pesquisarNomeVendedorByIdPedido(idPedido);
            addAtributo("idPedido", idPedido);
            addAtributo("infoAdFisco",
                    "MATERIAL ISENTO DE ST; MATERIAL NÃO DESTINADO PARA CONSTRUÇÃO CIVIL E NEM PARA AUTOPEÇAS; PEDIDO NÚMERO "
                            + idPedido + ". VENDEDOR: " + nomeVend);

            Date dtAtual = new Date();
            addAtributo("dataSaida", StringUtils.formatarData(dtAtual));
            addAtributo("horaSaida", StringUtils.formatarHora(dtAtual));
            addAtributo("listaNumeroNFe", nFeService.pesquisarNumeroNFeByIdPedido(idPedido));
            addAtributo("listaProduto", gerarListaProdutoItemPedido(listaItem));
            addAtributo("listaDuplicata", listaDuplicata);
            addAtributo("cliente", cliente);
            addAtributo("formaPagamentoSelecionada", gerarTipoFormaPagamento(listaDuplicata.size()).getCodigo());

            Transportadora t = pedidoService.pesquisarTransportadoraByIdPedido(idPedido);
            addAtributo("transportadora", t != null ? new TransportadoraJson(t, t.getLogradouro()) : null);
            addAtributo("logradouro",
                    cliente != null ? clienteService.pesquisarLogradouroFaturamentoById(cliente.getId()) : null);
            addAtributo(
                    "telefoneContatoPedido",
                    telefone.length > 0 ? String.valueOf(telefone[0])
                            + String.valueOf(telefone[1]).replaceAll("\\D+", "") : "");
            try {
                Integer[] numNFe = nFeService.gerarNumeroSerieModeloNFe();
                addAtributo("numeroNFe", numNFe[0]);
                addAtributo("serieNFe", numNFe[1]);
                addAtributo("modeloNFe", numNFe[2]);
            } catch (BusinessException e) {
                gerarListaMensagemAlerta(e);
            }

        } catch (BusinessException e1) {
            gerarListaMensagemErro(e1.getListaMensagem());
            addAtributo("idPedido", idPedido);
        }

        irTopoPagina();
    }

    @Get("emissaoNFe/transportadora/id")
    public void pesquisarTransportadoraById(Integer id) {
        Transportadora t = transportadoraService.pesquisarTransportadoraLogradouroById(id);
        t.setEnderecoFormatado(t.getEnderecoNumeroBairro());
        t.setMunicipioFormatado(t.getMunicipio());
        t.setUfFormatado(t.getUf());
        serializarJson(new SerializacaoJson("transportadora", t));
    }

    private void popularDestinatario(DadosNFe nf) {
        IdentificacaoDestinatarioNFe d = nf.getIdentificacaoDestinatarioNFe();
        if (d != null) {
            Cliente c = new Cliente();
            c.setRazaoSocial(d.getRazaoSocial());
            c.setCnpj(d.getCnpj());
            c.setInscricaoEstadual(d.getInscricaoEstadual());
            c.setInscricaoSUFRAMA(d.getInscricaoSUFRAMA());
            c.setCpf(d.getCpf());
            c.setEmail(d.getEmail());

            EnderecoNFe e = d.getEnderecoDestinatarioNFe();

            Logradouro l = new Logradouro();
            if (e != null) {
                l.setBairro(e.getBairro());
                l.setCep(e.getCep());
                l.setComplemento(e.getComplemento());
                l.setEndereco(e.getLogradouro());
                l.setCidade(e.getNomeMunicipio());
                l.setPais(e.getNomePais());
                l.setNumero(e.getNumero() == null || e.getNumero().trim().isEmpty() ? null : e.getNumero());
                l.setUf(e.getUF());
                l.setCodigoMunicipio(e.getCodigoMunicipio());
                addAtributo("telefoneContatoPedido", e.getTelefone());
            }
            addAtributo("cliente", c);
            addAtributo("logradouro", l);
        }
    }

    private void popularNFe(DadosNFe nf, Integer idPedido) {

        if (nf == null || idPedido == null) {
            return;
        }

        emissaoNFeHome();

        popularDestinatario(nf);
        popularTransporte(nf);

        addAtributo("idPedido", idPedido);
        addAtributo("listaNumeroNFe", nFeService.pesquisarNumeroNFeByIdPedido(idPedido));
        addAtributo("nf", nf);
        addAtributo("listaProduto", gerarListaProdutoDetalhamento(nf.getListaDetalhamentoProdutoServicoNFe()));
        addAtributo("infoAdFisco", nf.getInformacoesAdicionaisNFe() != null ? nf.getInformacoesAdicionaisNFe()
                .getInformacoesAdicionaisInteresseFisco() : null);

        IdentificacaoNFe iNFe = null;
        if ((iNFe = nf.getIdentificacaoNFe()) != null) {
            addAtributo("dataSaida", iNFe.getDataSaida());
            addAtributo("horaSaida", iNFe.getHoraSaida());
            addAtributo("finalidadeEmissaoSelecionada", iNFe.getFinalidadeEmissao());
            addAtributo("formaPagamentoSelecionada", iNFe.getIndicadorFormaPagamento());
            addAtributo("tipoEmissaoSelecionada", iNFe.getTipoEmissao());
            addAtributo("tipoImpressaoSelecionada", iNFe.getTipoImpressao());
            addAtributo("tipoOperacaoConsumidorSelecionada", iNFe.getOperacaoConsumidorFinal());
            addAtributo("tipoOperacaoSelecionada", iNFe.getTipoOperacao());
            addAtributo("tipoDestinoOperacaoSelecionada", iNFe.getDestinoOperacao());
            addAtributo("tipoPresencaSelecionada", iNFe.getTipoPresencaComprador());
            addAtributo("numeroNFe", iNFe.getNumero());
            addAtributo("serieNFe", iNFe.getSerie());
            addAtributo("modeloNFe", iNFe.getModelo());
        }

        if (nf.contemDuplicata()) {
            addAtributo("listaDuplicata", nf.getCobrancaNFe().getListaDuplicata());
        }
    }

    private void popularTransporte(DadosNFe nf) {
        TransporteNFe transporte = null;
        TransportadoraNFe tnfe = null;
        if (nf != null && (transporte = nf.getTransporteNFe()) != null) {
            if ((tnfe = transporte.getTransportadoraNFe()) != null) {
                Transportadora t = new Transportadora();
                t.setRazaoSocial(tnfe.getRazaoSocial());
                t.setCnpj(tnfe.getCnpj());
                t.setInscricaoEstadual(tnfe.getInscricaoEstadual());
                t.setEndereco(tnfe.getEnderecoCompleto());
                t.setCidade(tnfe.getMunicipio());
                t.setUf(tnfe.getUf());
                addAtributo("transportadora", new TransportadoraJson(t, t.getLogradouro()));
            }
            addAtributo("modalidadeFreteSelecionada", transporte.getModalidadeFrete());
        }

    }

    @Post("emissaoNFe/remocao")
    public void removerNFe(Integer numeroNFe) {
        nFeService.removerNFe(numeroNFe);
        irTopoPagina();
    }
}
