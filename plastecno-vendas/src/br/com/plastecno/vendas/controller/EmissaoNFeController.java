package br.com.plastecno.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.TipoEmissao;
import br.com.plastecno.service.nfe.TipoFinalidadeEmissao;
import br.com.plastecno.service.nfe.TipoFormaPagamento;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class EmissaoNFeController extends AbstractController {
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
        addAtributo("listaItem", pedidoService.pesquisarItemPedidoByIdPedido(11620));
    }

    @Post("emissaoNFe/emitirNFe")
    public void emitirNFe(NFe nf) {
        try {
            nFeService.gerarXMLNfe(nf);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        irTopoPagina();
    }
}
