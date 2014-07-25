package br.com.plastecno.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.vendas.login.UsuarioInfo;

/**
 * Classe responsvel por montar a barra de menu de opcoes. Sera executada apenas
 * um vez ja que os links do menu vao injetar as paginas em uma frame contudo no
 * corpo da pagina principal.
 */
@Resource
public class MenuController extends AbstractController {

    public MenuController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.verificarPermissaoAcesso("acessoAdministracaoPermitido", TipoAcesso.ADMINISTRACAO);
        this.verificarPermissaoAcesso("acessoManutencaoPermitido", TipoAcesso.MANUTENCAO);
        this.verificarPermissaoAcesso("acessoRelatorioClienteRegiaoPermitido",
                TipoAcesso.CONSULTA_RELATORIO_CLIENTE_REGIAO);
        this.verificarPermissaoAcesso("acessoRelatorioVendasRepresentadaPermitido",
                TipoAcesso.CONSULTA_RELATORIO_VENDAS_REPRESENTADA);
        this.verificarPermissaoAcesso("acessoRelatorioEntregaPermitido", TipoAcesso.CONSULTA_RELATORIO_ENTREGA);
        this.verificarPermissaoAcesso("acessoRelatorioPedidoRepresentadaPermitido", TipoAcesso.ADMINISTRACAO,
                TipoAcesso.OPERACAO_CONTABIL);
        this.verificarPermissaoAcesso("acessoRelatorioClienteRamoAtividadePermitido",
                TipoAcesso.ADMINISTRACAO, TipoAcesso.GERENCIA_VENDAS);
        this.verificarPermissaoAcesso("acessoRelatorioClienteRamoAtividadePermitido", TipoAcesso.ADMINISTRACAO);
    }

    @Get("/")
    public void menuHome() {
    }

}
