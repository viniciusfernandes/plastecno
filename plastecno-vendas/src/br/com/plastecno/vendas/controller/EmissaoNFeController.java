package br.com.plastecno.vendas.controller;

import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.DadosNFe;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.NFe;
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
    private NFeService nFeService;

    @Servico
    private PedidoService pedidoService;

    public EmissaoNFeController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Ramo de atividade");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.CADASTRO_BASICO);
    }

    @Get("emissaoNFe")
    public void emissaoNFeHome() {
        addAtributo("listaTipoFinalidadeEmissao", TipoFinalidadeEmissao.values());
        addAtributo("finalidadeEmissaoPadrao", TipoFinalidadeEmissao.NORMAL);

        addAtributo("listaTipoFormaPagamento", TipoFormaPagamento.values());
        addAtributo("formaPagamentoPadrao", TipoFormaPagamento.PRAZO);

        addAtributo("listaTipoEmissao", TipoEmissao.values());
        addAtributo("tipoEmissaoPadrao", TipoEmissao.NORMAL);
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

    }

    @Post("emissaoNFe/emitirNFe")
    public void emitirNFe(DadosNFe nf, Logradouro logradouro, Integer idPedido) {
        String xml = null;
        try {
            String telefone = nf.getIdentificacaoDestinatarioNFe().getEnderecoDestinatarioNFe().getTelefone();
            nf.getIdentificacaoDestinatarioNFe().setEnderecoDestinatarioNFe(
                    nFeService.gerarEnderecoNFe(logradouro, telefone));
            xml = nFeService.emitirNFe(new NFe(nf), idPedido);
        } catch (BusinessException e) {
            gerarListaMensagemErroLogException(e);
        }
        redirecTo(this.getClass()).nfexml(xml);
        // irTopoPagina();
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
}
