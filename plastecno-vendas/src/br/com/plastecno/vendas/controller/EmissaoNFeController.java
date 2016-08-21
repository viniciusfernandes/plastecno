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
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.TransportadoraNFe;
import br.com.plastecno.service.nfe.constante.TipoEmissao;
import br.com.plastecno.service.nfe.constante.TipoFinalidadeEmissao;
import br.com.plastecno.service.nfe.constante.TipoFormaPagamento;
import br.com.plastecno.service.nfe.constante.TipoImpressaoNFe;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMS;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMSST;
import br.com.plastecno.service.nfe.constante.TipoModalidadeFrete;
import br.com.plastecno.service.nfe.constante.TipoMotivoDesoneracaoICMS;
import br.com.plastecno.service.nfe.constante.TipoOrigemMercadoria;
import br.com.plastecno.service.nfe.constante.TipoRegimeTributacao;
import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;
import br.com.plastecno.service.nfe.constante.TipoTributacaoISS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
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
        NFe nFe = null;
        try {
            String telefone = nf.getIdentificacaoDestinatarioNFe().getEnderecoDestinatarioNFe().getTelefone();
            nf.getIdentificacaoDestinatarioNFe().setEnderecoDestinatarioNFe(
                    nFeService.gerarEnderecoNFe(logradouro, telefone));

            nFe = new NFe(nf);
            formatarDuplicata(nFe, false);

            redirecTo(this.getClass()).nfexml(nFeService.emitirNFe(nFe, idPedido));
        } catch (BusinessException e) {
            try {
                formatarDuplicata(nFe, true);
            } catch (BusinessException e1) {
                e.addMensagem(e1.getListaMensagem());
            }
            popularNFe(nf, idPedido);

            gerarListaMensagemErro(e);
            redirecTo(this.getClass()).emissaoNFeHome();
        } catch (Exception e) {
            gerarLogErro("Emissão da NFe", e);
        }

    }

    private void formatarDuplicata(NFe nFe, boolean fromServidor) throws BusinessException {
        List<DuplicataNFe> lista = null;
        if ((lista = nFe.getDadosNFe().getListaDuplicata()) == null) {
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
        addAtributo("telefoneContatoPedido",
                telefone.length > 0 ? String.valueOf(telefone[0]) + String.valueOf(telefone[1]).replaceAll("\\D+", "")
                        : "");
        irTopoPagina();
    }

    private void popularDestinatario(DadosNFe nf) {
        IdentificacaoDestinatarioNFe d = nf.getIdentificacaoDestinatarioNFe();
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
        l.setNumero(e.getNumero() == null ? null : Integer.parseInt(e.getNumero()));
        l.setUf(e.getUF());

        addAtributo("cliente", c);
        addAtributo("logradouro", l);
        addAtributo("telefoneContatoPedido", d.getEnderecoDestinatarioNFe().getTelefone());
    }

    private void popularNFe(DadosNFe nf, Integer idPedido) {
        emissaoNFeHome();

        popularDestinatario(nf);
        popularTransporte(nf);

        List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
        formatarItemPedido(listaItem);

        addAtributo("idPedido", idPedido);
        addAtributo("nf", nf);
        addAtributo("listaItem", listaItem);

        addAtributo("finalidadeEmissaoSelecionada", nf.getIdentificacaoNFe().getFinalidadeEmissao());
        addAtributo("formaPagamentoSelecionada", nf.getIdentificacaoNFe().getIndicadorFormaPagamento());
        addAtributo("tipoEmissaoSelecionada", nf.getIdentificacaoNFe().getTipoEmissao());
        addAtributo("tipoImpressaoSelecionada", nf.getIdentificacaoNFe().getTipoImpressao());
        addAtributo("listaDuplicata", nf.getCobrancaNFe().getListaDuplicata());
    }

    private void popularTransporte(DadosNFe nf) {
        TransportadoraNFe tnfe = nf.getTransporteNFe().getTransportadoraNFe();
        Transportadora t = new Transportadora();
        t.setRazaoSocial(tnfe.getRazaoSocial());
        t.setCnpj(tnfe.getCnpj());
        t.setInscricaoEstadual(tnfe.getInscricaoEstadual());
        t.setEndereco(tnfe.getEnderecoCompleto());
        t.setCidade(tnfe.getMunicipio());
        t.setUf(tnfe.getUf());

        addAtributo("modalidadeFreteSelecionada", nf.getTransporteNFe().getModalidadeFrete());
        addAtributo("transportadora", t);
    }
}
