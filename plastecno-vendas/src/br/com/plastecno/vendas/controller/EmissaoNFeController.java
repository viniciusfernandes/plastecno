package br.com.plastecno.vendas.controller;

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
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.constante.TipoEmissao;
import br.com.plastecno.service.nfe.constante.TipoFinalidadeEmissao;
import br.com.plastecno.service.nfe.constante.TipoFormaPagamento;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMS;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMSST;
import br.com.plastecno.service.nfe.constante.TipoModalidadeFrete;
import br.com.plastecno.service.nfe.constante.TipoMotivoDesoneracaoICMS;
import br.com.plastecno.service.nfe.constante.TipoOrigemMercadoria;
import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoIPI;
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
        addAtributo("listaTipoModalidadeFrete", TipoModalidadeFrete.values());

    }

    @Post("emissaoNFe/emitirNFe")
    public void emitirNFe(NFe nf, Logradouro logradouro, Integer idPedido) {
        String xml = null;
        try {
            nf.getIdentificacaoDestinatarioNFe().setEnderecoDestinatarioNFe(nFeService.gerarEnderecoNFe(logradouro));
            xml = nFeService.emitirNFe(nf, idPedido);
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
        Cliente cliente = pedidoService.pesquisarClienteResumidoEContatoByIdPedido(idPedido);

        addAtributo("cliente", cliente);
        addAtributo("transportadora", pedidoService.pesquisarTransportadoraByIdPedido(idPedido));
        addAtributo("logradouro", clienteService.pesquisarLogradouroFaturamentoById(cliente.getId()));
        addAtributo("listaItem", pedidoService.pesquisarItemPedidoByIdPedido(idPedido));
        addAtributo("idPedido", idPedido);

        irTopoPagina();
    }
}
