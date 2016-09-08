package br.com.plastecno.vendas.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ConfiguracaoSistemaService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.constante.TipoAcesso;
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
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.IdentificacaoNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.TransportadoraNFe;
import br.com.plastecno.service.nfe.TransporteNFe;
import br.com.plastecno.service.nfe.constante.TipoDestinoOperacao;
import br.com.plastecno.service.nfe.constante.TipoEmissao;
import br.com.plastecno.service.nfe.constante.TipoFinalidadeEmissao;
import br.com.plastecno.service.nfe.constante.TipoFormaPagamento;
import br.com.plastecno.service.nfe.constante.TipoImpressaoNFe;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMS;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMSST;
import br.com.plastecno.service.nfe.constante.TipoModalidadeFrete;
import br.com.plastecno.service.nfe.constante.TipoMotivoDesoneracaoICMS;
import br.com.plastecno.service.nfe.constante.TipoOperacaoConsumidorFinal;
import br.com.plastecno.service.nfe.constante.TipoOperacaoNFe;
import br.com.plastecno.service.nfe.constante.TipoOrigemMercadoria;
import br.com.plastecno.service.nfe.constante.TipoRegimeTributacao;
import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;
import br.com.plastecno.service.nfe.constante.TipoTributacaoISS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
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

    @Get("emissaoNFe")
    public void emissaoNFeHome() {

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
        addAtributo("listaTipoMotivoDesoneracao", TipoMotivoDesoneracaoICMS.values());
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

        addAtributo("finalidadeEmissaoSelecionada", TipoFinalidadeEmissao.NORMAL.getCodigo());
        addAtributo("formaPagamentoSelecionada", TipoFormaPagamento.PRAZO.getCodigo());
        addAtributo("tipoEmissaoSelecionada", TipoEmissao.NORMAL.getCodigo());
        addAtributo("tipoImpressaoSelecionada", TipoImpressaoNFe.RETRATO.getCodigo());

    }

    @Post("emissaoNFe/emitirNFe")
    public void emitirNFe(DadosNFe nf, Logradouro logradouro, Integer idPedido) {
        try {
            if (nf != null && nf.getIdentificacaoDestinatarioNFe() != null
                    && nf.getIdentificacaoDestinatarioNFe().getEnderecoDestinatarioNFe() != null) {

                String telefone = nf.getIdentificacaoDestinatarioNFe().getEnderecoDestinatarioNFe().getTelefone();
                nf.getIdentificacaoDestinatarioNFe().setEnderecoDestinatarioNFe(
                        nFeService.gerarEnderecoNFe(logradouro, telefone));
            }

            formatarDatas(nf, false);

            redirecTo(this.getClass()).nfexml(nFeService.emitirNFe(new NFe(nf), idPedido));
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
                d.setDataVencimento(to.format(from.parse(d.getDataVencimento())));
            } catch (ParseException e) {
                throw new BusinessException("Não foi possível formatar a data de vencimento da duplicata "
                        + d.getNumero() + ". O valor enviado é \"" + d.getDataVencimento() + "\"");
            }
        }

        for (DetalhamentoProdutoServicoNFe d : nf.getListaDetalhamentoProdutoServicoNFe()) {
            if (d.getListaDeclaracaoImportacao() != null) {
                for (DeclaracaoImportacao i : d.getListaDeclaracaoImportacao()) {
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

        // Formatando a data de hora/entrada do produto
        if (!fromServidor) {
            to = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        }
        IdentificacaoNFe i = nf.getIdentificacaoNFe();
        String dh = i.getDataHoraEntradaSaidaProduto();
        if (!StringUtils.isEmpty(dh)) {
            try {
                i.setDataHoraEntradaSaidaProduto(to.format(from.parse(dh)));
            } catch (ParseException e) {
                throw new BusinessException(
                        "Não foi possível formatar a data/hora de entrada/saida do produto. O valor enviado é \"" + dh
                                + "\"");
            }
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

    @SuppressWarnings("unchecked")
    private void inicializarListaCfop(HttpServletRequest request) {
        if ((listaCfop = (List<Object[]>) request.getServletContext().getAttribute("listaCfop")) != null) {
            return;
        }
        listaCfop = gerarListaCfop();
        request.getServletContext().setAttribute("listaCfop", listaCfop);
    }

    public Download nfexml(String xml) {
        return gerarDownload(xml.getBytes(), "nfe.xml", "application/octet-stream");
    }

    @Get("emissaoNFe/pedido")
    public void pesquisarPedidoById(Integer idPedido) {
        NFe nFe = null;
        try {
            nFe = nFeService.gerarNFeByIdPedido(idPedido);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e.getListaMensagem());
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

            formatarItemPedido(listaItem);

            addAtributo("listaDuplicata", listaDuplicata);
            addAtributo("cliente", cliente);
            addAtributo("transportadora", pedidoService.pesquisarTransportadoraByIdPedido(idPedido));
            addAtributo("logradouro", clienteService.pesquisarLogradouroFaturamentoById(cliente.getId()));
            addAtributo("listaItem", listaItem);
            addAtributo("idPedido", idPedido);
            addAtributo(
                    "telefoneContatoPedido",
                    telefone.length > 0 ? String.valueOf(telefone[0])
                            + String.valueOf(telefone[1]).replaceAll("\\D+", "") : "");
        }
        irTopoPagina();
    }

    private void popularDestinatario(DadosNFe nf) {
        IdentificacaoDestinatarioNFe d = nf.getIdentificacaoDestinatarioNFe();
        if (d != null) {
            Cliente c = new Cliente();
            c.setRazaoSocial(d.getNomeFantasia());
            c.setCnpj(d.getCnpj());
            c.setInscricaoEstadual(d.getInscricaoEstadual());
            c.setCpf(d.getCpf());
            c.setEmail(d.getEmail());

            EnderecoNFe e = d.getEnderecoDestinatarioNFe();

            Logradouro l = new Logradouro();
            l.setBairro(e.getBairro());
            l.setCep(e.getCep());
            l.setComplemento(e.getComplemento());
            l.setEndereco(e.getLogradouro());
            l.setCidade(e.getNomeMunicipio());
            l.setPais(e.getNomePais());
            l.setNumero(e.getNumero() == null || e.getNumero().trim().isEmpty() ? null
                    : Integer.parseInt(e.getNumero()));
            l.setUf(e.getUF());

            addAtributo("cliente", c);
            addAtributo("logradouro", l);
            addAtributo("telefoneContatoPedido", d.getEnderecoDestinatarioNFe().getTelefone());
        }
    }

    private void popularNFe(DadosNFe nf, Integer idPedido) {

        if (nf == null || idPedido == null) {
            return;
        }

        emissaoNFeHome();

        popularDestinatario(nf);
        popularTransporte(nf);

        List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
        formatarItemPedido(listaItem);

        addAtributo("idPedido", idPedido);
        addAtributo("nf", nf);
        addAtributo("listaItem", listaItem);

        IdentificacaoNFe iNFe = null;
        if ((iNFe = nf.getIdentificacaoNFe()) != null) {
            addAtributo("finalidadeEmissaoSelecionada", iNFe.getFinalidadeEmissao());
            addAtributo("formaPagamentoSelecionada", iNFe.getIndicadorFormaPagamento());
            addAtributo("tipoEmissaoSelecionada", iNFe.getTipoEmissao());
            addAtributo("tipoImpressaoSelecionada", iNFe.getTipoImpressao());
            addAtributo("tipoOperacaoConsumidorSelecionada", iNFe.getOperacaoConsumidorFinal());
            addAtributo("tipoOperacaoSelecionada", iNFe.getTipoOperacao());
            addAtributo("tipoDestinoOperacaoSelecionada", iNFe.getDestinoOperacao());
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
