package br.com.plastecno.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class PedidoFracionadoNFeController extends AbstractController {

    @Servico
    private NFeService nFeService;

    @Servico
    private RelatorioService relatorioService;

    public PedidoFracionadoNFeController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("pedidoFracionado/emissaoNFe")
    public void pedidoFracionadoEmissaoNFe(Integer idPedido) {
        redirecTo(EmissaoNFeController.class).pesquisarPedidoById(idPedido);
    }

    @Get("pedidoFracionadoNFe")
    public void pedidoFracionadoNFeHome() {
        addAtributo("relatorio", relatorioService.gerarRelatorioPedidoFracionado());
    }

    @Post("pedidoFracionado/remocao")
    public void removerItemFracionado(Integer idItemFracionado) {
        try {
            nFeService.removerItemFracionadoNFe(idItemFracionado);
            gerarMensagemSucesso("Item fracionado removido com sucesso.");
            irTopoPagina();
        } catch (Exception e) {
            gerarLogErro("Remocao do item fracionado", e);
        }
    }
}
