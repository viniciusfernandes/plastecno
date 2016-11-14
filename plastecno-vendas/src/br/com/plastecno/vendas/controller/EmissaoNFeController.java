package br.com.plastecno.vendas.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public EmissaoNFeController(HttpServletRequest request, Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Ramo de atividade");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.CADASTRO_BASICO);

        inicializarListaCfop(request);
    }

    private void arredondarValoresItemPedido(List<ItemPedido> listaItem) {
        if (listaItem == null || listaItem.isEmpty()) {
            return;
        }
        for (ItemPedido i : listaItem) {
            i.setPrecoUnidadeFormatado(String.valueOf(NumeroUtils.arredondarValorMonetario(i.getPrecoUnidade())));
            i.setValorTotalFormatado(String.valueOf(NumeroUtils.arredondarValorMonetario(i.calcularPrecoTotal())));
            i.setValorICMSFormatado(String.valueOf(NumeroUtils.arredondarValorMonetario(i.calcularValorICMS())));
            i.setValorIPIFormatado(String.valueOf(NumeroUtils.arredondarValorMonetario(i.calcularPrecoUnidadeIPI())));
            i.setAliquotaICMS(NumeroUtils.gerarPercentual(i.getAliquotaICMS()));
            i.setAliquotaIPI(NumeroUtils.gerarPercentual(i.getAliquotaIPI()));
        }
    }

    @Get("emissaoNFe/valorICMSInterestadual")
    public void calcularValorICMSInterestadual(ICMSInterestadual icms) {
        icms.carregarValores();
        icms.setValorFCPDestino(NumeroUtils.arredondarValorMonetario(icms.getValorFCPDestino()));
        icms.setValorUFDestino(NumeroUtils.arredondarValorMonetario(icms.getValorUFDestino()));
        icms.setValorUFRemetente(NumeroUtils.arredondarValorMonetario(icms.getValorUFRemetente()));
        serializarJson(new SerializacaoJson("icms", icms));
    };

    public void carregarNFe(Integer idPedido, boolean isTriangulacao) {

        NFe nFe = null;
        try {
            nFe = nFeService.gerarNFeByIdPedido(idPedido, isTriangulacao);
        } catch (BusinessException e) {
            gerarListaMensagemErroLogException(e);
        }

        if (nFe != null) {
            popularNFe(nFe.getDadosNFe(), idPedido);
            try {
                formatarDatas(nFe.getDadosNFe(), true);
            } catch (BusinessException e) {
                gerarListaMensagemErro(e);
            }
        } else {
            Cliente cliente = pedidoService.pesquisarClienteResumidoByIdPedido(idPedido);
            List<DuplicataNFe> listaDuplicata = nFeService.gerarDuplicataByIdPedido(idPedido);
            Object[] telefone = pedidoService.pesquisarTelefoneContatoByIdPedido(idPedido);
            List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
            arredondarValoresItemPedido(listaItem);

            addAtributo("listaProduto", gerarListaProdutoItemPedido(listaItem));
            addAtributo("listaDuplicata", listaDuplicata);
            addAtributo("cliente", cliente);
            addAtributo("transportadora", pedidoService.pesquisarTransportadoraByIdPedido(idPedido));
            addAtributo("logradouro",
                    cliente != null ? clienteService.pesquisarLogradouroFaturamentoById(cliente.getId()) : null);
            addAtributo("idPedido", idPedido);
            addAtributo(
                    "telefoneContatoPedido",
                    telefone.length > 0 ? String.valueOf(telefone[0])
                            + String.valueOf(telefone[1]).replaceAll("\\D+", "") : "");
            try {
                Object[] numNFe = nFeService.gerarNumeroSerieModeloNFe();
                addAtributo("numeroNFe", numNFe[0]);
                addAtributo("serieNFe", numNFe[1]);
                addAtributo("modeloNFe", numNFe[2]);
            } catch (BusinessException e) {
                gerarListaMensagemAlerta(e);
            }

        }
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

        addAtributoPadrao("finalidadeEmissaoSelecionada", TipoFinalidadeEmissao.NORMAL.getCodigo());
        addAtributoPadrao("formaPagamentoSelecionada", TipoFormaPagamento.PRAZO.getCodigo());
        addAtributoPadrao("tipoEmissaoSelecionada", TipoEmissao.NORMAL.getCodigo());
        addAtributoPadrao("tipoImpressaoSelecionada", TipoImpressaoNFe.RETRATO.getCodigo());
        addAtributoPadrao("tipoPresencaSelecionada", TipoPresencaComprador.NAO_PRESENCIAL_OUTROS.getCodigo());
        addAtributoPadrao("tipoOperacaoSelecionada", TipoOperacaoNFe.SAIDA.getCodigo());
    }

    @Post("emissaoNFe/emitirNFe")
    public void emitirNFe(DadosNFe nf, Logradouro logradouro, Integer idPedido, boolean isTriangulacao) {
        try {
            nFeService.validarEmissaoNFePedido(idPedido);
            try {
                // Verificando condicao para gerar o endereco do destinatario a
                // partir do logradouro
                if (nf != null && nf.getIdentificacaoDestinatarioNFe() != null
                        && nf.getIdentificacaoDestinatarioNFe().getEnderecoDestinatarioNFe() != null) {

                    String telefone = nf.getIdentificacaoDestinatarioNFe().getEnderecoDestinatarioNFe().getTelefone();
                    nf.getIdentificacaoDestinatarioNFe().setEnderecoDestinatarioNFe(
                            nFeService.gerarEnderecoNFe(logradouro, telefone));
                }

                formatarDatas(nf, false);
                nFeService.emitirNFe(new NFe(nf), idPedido, isTriangulacao);
                gerarMensagemSucesso("A NFe do pedido No. " + idPedido + " foi gerado com sucesso.");
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
        } catch (BusinessException e2) {
            gerarListaMensagemErro(e2.getListaMensagem());
            addAtributo("idPedido", idPedido);
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
                                        + d.getNumeroItem() + ". O valor enviado é \"" + i.getDataDesembaraco() + "\"");
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
            val = new Object[13];
            val[0] = i.contemICMS() ? i.getTributos().getIcms().getTipoIcms().getAliquota() : 0;
            val[1] = i.contemIPI() ? i.getTributos().getIpi().getTipoIpi().getAliquota() : 0;
            val[2] = p.getCfop();
            val[3] = p.getDescricao();
            val[4] = p.getNcm();
            val[5] = i.getNumeroItem();
            val[6] = p.getQuantidadeTributavel();
            val[7] = p.getUnidadeComercial();
            val[8] = p.getValorTotalBruto();
            val[9] = p.getValorUnitarioComercializacao();
            val[10] = p.getValorUnitarioComercializacao();
            val[11] = i.contemICMS() ? i.getTributos().getIcms().getTipoIcms().getValor() : 0d;
            val[12] = i.contemIPI() ? i.getTributos().getIpi().getTipoIpi().getValor() : 0d;

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
            val = new Object[13];
            val[0] = i.getAliquotaICMS();
            val[1] = i.getAliquotaIPI();
            val[2] = "";
            val[3] = i.getDescricaoSemFormatacao();
            val[4] = i.getNcm();
            val[5] = i.getSequencial();
            val[6] = i.getQuantidade();
            val[7] = i.getTipoVenda().toString();
            val[8] = i.getValorTotal();
            val[9] = i.getPrecoUnidade();
            val[10] = i.getPrecoUnidade();
            val[11] = i.getValorICMS();
            val[12] = i.getPrecoUnidadeIPI();

            l.add(val);
        }
        return l;
    }

    @SuppressWarnings("unchecked")
    private void inicializarListaCfop(HttpServletRequest request) {
        if ((listaCfop = (List<Object[]>) request.getServletContext().getAttribute("listaCfop")) != null) {
            return;
        }
        listaCfop = gerarListaCfop();
        request.getServletContext().setAttribute("listaCfop", listaCfop);
    }

    @Get("emissaoNFe/NFe")
    public void pesquisarNFe(Integer numeroNFe, boolean isTriangulacao) {
        carregarNFe(nFeService.pesquisarIdPedidoByNumeroNFe(numeroNFe, isTriangulacao), isTriangulacao);
        addAtributo("isTriangulacao", isTriangulacao);
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
            carregarNFe(idPedido, false);

        } catch (BusinessException e1) {
            gerarListaMensagemErro(e1.getListaMensagem());
            addAtributo("idPedido", idPedido);
        }

        irTopoPagina();
    }

    private void popularDestinatario(DadosNFe nf) {
        IdentificacaoDestinatarioNFe d = nf.getIdentificacaoDestinatarioNFe();
        if (d != null) {
            Cliente c = new Cliente();
            c.setRazaoSocial(d.getRazaoSocial());
            c.setCnpj(d.getCnpj());
            c.setInscricaoEstadual(d.getInscricaoEstadual());
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
        addAtributo("nf", nf);
        addAtributo("listaProduto", gerarListaProdutoDetalhamento(nf.getListaDetalhamentoProdutoServicoNFe()));

        IdentificacaoNFe iNFe = null;
        if ((iNFe = nf.getIdentificacaoNFe()) != null) {
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
            Transportadora t = null;
            if ((tnfe = transporte.getTransportadoraNFe()) != null) {
                t = new Transportadora();
                t.setRazaoSocial(tnfe.getRazaoSocial());
                t.setCnpj(tnfe.getCnpj());
                t.setInscricaoEstadual(tnfe.getInscricaoEstadual());
                t.setEndereco(tnfe.getEnderecoCompleto());
                t.setCidade(tnfe.getMunicipio());
                t.setUf(tnfe.getUf());
            }
            addAtributo("modalidadeFreteSelecionada", transporte.getModalidadeFrete());
            addAtributo("transportadora", t);
        }

    }
}
