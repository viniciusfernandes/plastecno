package br.com.svr.vendas.controller;

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
import br.com.svr.service.ClienteService;
import br.com.svr.service.ConfiguracaoSistemaService;
import br.com.svr.service.NFeService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.TransportadoraService;
import br.com.svr.service.constante.ParametroConfiguracaoSistema;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.constante.TipoUF;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.LogradouroCliente;
import br.com.svr.service.entity.LogradouroEndereco;
import br.com.svr.service.entity.Transportadora;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.nfe.DadosNFe;
import br.com.svr.service.nfe.DeclaracaoImportacao;
import br.com.svr.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.svr.service.nfe.DuplicataNFe;
import br.com.svr.service.nfe.EnderecoNFe;
import br.com.svr.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.svr.service.nfe.IdentificacaoNFe;
import br.com.svr.service.nfe.NFe;
import br.com.svr.service.nfe.ProdutoServicoNFe;
import br.com.svr.service.nfe.TransportadoraNFe;
import br.com.svr.service.nfe.TransporteNFe;
import br.com.svr.service.nfe.constante.TipoAliquotaICMSInterestadual;
import br.com.svr.service.nfe.constante.TipoAliquotaICMSPartilha;
import br.com.svr.service.nfe.constante.TipoDesoneracaoICMS;
import br.com.svr.service.nfe.constante.TipoDestinoOperacao;
import br.com.svr.service.nfe.constante.TipoEmissao;
import br.com.svr.service.nfe.constante.TipoFinalidadeEmissao;
import br.com.svr.service.nfe.constante.TipoFormaPagamento;
import br.com.svr.service.nfe.constante.TipoImpressaoNFe;
import br.com.svr.service.nfe.constante.TipoIntermediacaoImportacao;
import br.com.svr.service.nfe.constante.TipoModalidadeDeterminacaoBCICMS;
import br.com.svr.service.nfe.constante.TipoModalidadeDeterminacaoBCICMSST;
import br.com.svr.service.nfe.constante.TipoModalidadeFrete;
import br.com.svr.service.nfe.constante.TipoNFe;
import br.com.svr.service.nfe.constante.TipoOperacaoConsumidorFinal;
import br.com.svr.service.nfe.constante.TipoOperacaoNFe;
import br.com.svr.service.nfe.constante.TipoOrigemMercadoria;
import br.com.svr.service.nfe.constante.TipoPresencaComprador;
import br.com.svr.service.nfe.constante.TipoRegimeTributacao;
import br.com.svr.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.svr.service.nfe.constante.TipoTributacaoICMS;
import br.com.svr.service.nfe.constante.TipoTributacaoIPI;
import br.com.svr.service.nfe.constante.TipoTributacaoISS;
import br.com.svr.service.nfe.constante.TipoTributacaoPIS;
import br.com.svr.util.NumeroUtils;
import br.com.svr.util.StringUtils;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.json.ProdutoServicoJson;
import br.com.svr.vendas.json.SerializacaoJson;
import br.com.svr.vendas.json.TransportadoraJson;
import br.com.svr.vendas.login.UsuarioInfo;

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
        setNomeTela("Ramo de atividade");
        verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.ADMINISTRACAO, TipoAcesso.CADASTRO_BASICO);

        inicializarListaCfop(request);
    }

    private Double calcularPesoLiquido(List<ItemPedido> listaItem) {
        double peso = 0;
        for (ItemPedido i : listaItem) {
            if (i.getPeso() != null) {
                peso += i.getPeso();
            }
        }
        return NumeroUtils.arredondarValor2Decimais(peso);
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
        addAtributoCondicional("finalidadeEmissaoSelecionada", TipoFinalidadeEmissao.NORMAL.getCodigo());
        addAtributoCondicional("formaPagamentoSelecionada", TipoFormaPagamento.PRAZO.getCodigo());
        addAtributoCondicional("tipoEmissaoSelecionada", TipoEmissao.NORMAL.getCodigo());
        addAtributoCondicional("tipoImpressaoSelecionada", TipoImpressaoNFe.RETRATO.getCodigo());
        addAtributoCondicional("tipoPresencaSelecionada", TipoPresencaComprador.NAO_PRESENCIAL_OUTROS.getCodigo());
        addAtributoCondicional("tipoOperacaoSelecionada", TipoOperacaoNFe.SAIDA.getCodigo());
        addAtributoCondicional("modalidadeFreteSelecionada", TipoModalidadeFrete.DESTINATARIO_REMETENTE.getCodigo());
    }

    @Post("emissaoNFe/emitirNFe")
    public void emitirNFe(DadosNFe nf, TipoNFe tipoNFe, LogradouroCliente logradouro, String telefoneDestinatario,
            Integer idPedido) {
        try {
            nf.getIdentificacaoDestinatarioNFe().setEnderecoDestinatarioNFe(
                    nFeService.gerarEnderecoNFe(logradouro, telefoneDestinatario));
            formatarDatas(nf, false);
            ordenarListaDetalhamentoProduto(nf);
            String numeroNFe = nFeService.emitirNFe(new NFe(nf), tipoNFe, idPedido);

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
            p.setAliquotaICMS(NumeroUtils.arredondarValor2Decimais((Double) val[0]));
            p.setAliquotaIPI(NumeroUtils.arredondarValor2Decimais((Double) val[1]));
            p.setCfop((String) val[2]);
            p.setDescricao((String) val[3]);
            p.setNcm((String) val[4]);
            p.setNumeroItem((Integer) val[5]);
            p.setQuantidade((Integer) val[6]);
            p.setUnidadeComercial((String) val[7]);
            p.setValorTotalBruto(NumeroUtils.arredondarValor2Decimais((Double) val[8]));
            p.setValorUnitarioComercializacao(NumeroUtils.arredondarValor2Decimais((Double) val[9]));
            p.setValorUnitarioTributavel(NumeroUtils.arredondarValor2Decimais((Double) val[10]));
            p.setValorICMS(NumeroUtils.arredondarValor2Decimais((Double) val[11]));
            p.setValorIPI(NumeroUtils.arredondarValor2Decimais((Double) val[12]));
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
            val[0] = NumeroUtils.gerarPercentualInteiro(i.getAliquotaICMS());
            val[1] = NumeroUtils.gerarPercentualInteiro(i.getAliquotaIPI());
            val[2] = "";
            val[3] = i.getDescricaoItemMaterial();
            val[4] = StringUtils.removerMascaraDocumento(i.getNcm());
            val[5] = i.getSequencial();
            val[6] = i.getQuantidade();
            val[7] = i.getTipoVenda().toString();
            val[8] = NumeroUtils.arredondarValor2Decimais(i.calcularPrecoTotalVenda());
            val[9] = NumeroUtils.arredondarValor2Decimais(i.getPrecoUnidade());
            val[10] = NumeroUtils.arredondarValor2Decimais(i.getPrecoUnidade());
            val[11] = NumeroUtils.arredondarValor2Decimais(i.calcularValorICMS());
            val[12] = NumeroUtils.arredondarValor2Decimais(i.calcularValorIPI());
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

            double vFrete = NumeroUtils.arredondarValor2Decimais(pedidoService
                    .calcularValorFretePorItemByIdPedido(idPedido));
            addAtributo("valorFrete", vFrete);
            String numPedCli = pedidoService.pesquisarNumeroPedidoClienteByIdPedido(idPedido);
            if (numPedCli == null) {
                numPedCli = "";
            }

            String nomeVend = pedidoService.pesquisarNomeVendedorByIdPedido(idPedido);
            addAtributo("idPedido", idPedido);
            addAtributo("infoAdFisco",
                    "MATERIAL ISENTO DE ST; MATERIAL NÃO DESTINADO PARA CONSTRUÇÃO CIVIL E NEM PARA AUTOPEÇAS; PEDIDO NÚMERO "
                            + idPedido + "; NÚM. PEDIDO CLIENTE " + numPedCli + ". VENDEDOR: " + nomeVend);
            addAtributo("numeroPedidoCliente", numPedCli);

            double peso = calcularPesoLiquido(listaItem);
            Double[] valFreteUni = pedidoService.calcularValorFreteUnidadeByIdPedido(idPedido);
            List<ProdutoServicoJson> listaProduto = gerarListaProdutoItemPedido(listaItem);

            addAtributo("quantidade", 1);
            addAtributo("valorFretePedido", valFreteUni[0]);
            addAtributo("valorFreteUnidade", NumeroUtils.arredondarValor2Decimais(valFreteUni[1]));
            addAtributo("pesoLiquido", peso);
            addAtributo("pesoBruto", peso);
            addAtributo("especieVolume", "VOLUME");

            Date dtAtual = new Date();
            addAtributo("dataSaida", StringUtils.formatarData(dtAtual));
            addAtributo("horaSaida", StringUtils.formatarHora(dtAtual));
            addAtributo("listaNumeroNFe", nFeService.pesquisarNumeroNFeByIdPedido(idPedido));
            addAtributo("listaProduto", listaProduto);
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
            if (listaItem.isEmpty()) {
                gerarMensagemAlerta("O pedido No. " + idPedido + " não possui itens para emissão.");
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

            LogradouroEndereco l = new LogradouroEndereco();
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
        List<DetalhamentoProdutoServicoNFe> listaDet = nf.getListaDetalhamentoProdutoServicoNFe();
        emissaoNFeHome();

        popularDestinatario(nf);
        popularTransporte(nf);

        addAtributo("idPedido", idPedido);
        addAtributo("listaNumeroNFe", nFeService.pesquisarNumeroNFeByIdPedido(idPedido));
        addAtributo("nf", nf);
        addAtributo("listaProduto", gerarListaProdutoDetalhamento(listaDet));
        addAtributo("infoAdFisco", nf.getInformacoesAdicionaisNFe() != null ? nf.getInformacoesAdicionaisNFe()
                .getInformacoesAdicionaisInteresseFisco() : null);

        Double[] valFreteUni = pedidoService.calcularValorFreteUnidadeByIdPedido(idPedido);

        addAtributo("valorFretePedido", valFreteUni[0]);
        addAtributo("valorFreteUnidade", NumeroUtils.arredondarValor2Decimais(valFreteUni[1]));

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
