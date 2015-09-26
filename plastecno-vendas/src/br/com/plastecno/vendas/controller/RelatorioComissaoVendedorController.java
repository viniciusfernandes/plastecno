package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioComissaoVendedorController extends AbstractController {
    @Servico
    private RelatorioService relatorioService;

    @Servico
    private UsuarioService usuarioService;

    public RelatorioComissaoVendedorController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/comissao/pedido/pdf")
    public Download downloadPedidoPDF(Integer idPedido, TipoPedido tipoPedido) {
        return redirecTo(PedidoController.class).downloadPedidoPDF(idPedido, tipoPedido);
    }

    @Post("relatorio/comissao/vendedor/listagem")
    public void gerarRelatorioComissaoVendedor(Date dataInicial, Date dataFinal, Usuario vendedor) {
        try {
            if (!isAcessoPermitido(TipoAcesso.ADMINISTRACAO)) {
                vendedor = usuarioService.pesquisarUsuarioResumidoById(vendedor.getId());
            }
            if (vendedor == null || vendedor.getId() == null) {
                addAtributo("relatorio",
                        relatorioService.gerarRelatorioComissaoVendedores(new Periodo(dataInicial, dataFinal)));
                addAtributo("isRelatorioVendedores", true);
            } else {
                addAtributo("relatorio", relatorioService.gerarRelatorioComissaoVendedor(vendedor.getId(), new Periodo(
                        dataInicial, dataFinal)));
                addAtributo("isRelatorioVendedores", false);
            }
            irRodapePagina();
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }

        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("vendedor", vendedor);
    }

    @Get("relatorio/comissao/vendedor")
    public void relatorioComissaoVendedorHome() {
        configurarFiltroPediodoMensal();
        addAtributo("acessoPesquisaComissaoPermitido", true);
        if (!isAcessoPermitido(TipoAcesso.ADMINISTRACAO)) {
            addAtributo("acessoPesquisaComissaoPermitido", false);
            addAtributo("vendedor", usuarioService.pesquisarById(getCodigoUsuario()));
        }
    }
}
