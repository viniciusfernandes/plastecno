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
        verificarPermissaoAcesso("acessoAdministracaoPermitido", TipoAcesso.ADMINISTRACAO);
        // verificarPermissaoAcesso("acessoManutencaoPermitido",
        // TipoAcesso.MANUTENCAO);
        verificarPermissaoAcesso("acessoRelatorioClienteRegiaoPermitido", TipoAcesso.CONSULTA_RELATORIO_CLIENTE_REGIAO);
        verificarPermissaoAcesso("acessoRelatorioVendasRepresentadaPermitido",
                TipoAcesso.CONSULTA_RELATORIO_VENDAS_REPRESENTADA);
        verificarPermissaoAcesso("acessoRelatorioEntregaPermitido", TipoAcesso.CONSULTA_RELATORIO_ENTREGA);
        verificarPermissaoAcesso("acessoRelatorioPedidoRepresentadaPermitido", TipoAcesso.ADMINISTRACAO,
                TipoAcesso.OPERACAO_CONTABIL);
        verificarPermissaoAcesso("acessoRelatorioClienteRamoAtividadePermitido", TipoAcesso.ADMINISTRACAO,
                TipoAcesso.GERENCIA_VENDAS);
        verificarPermissaoAcesso("acessoRelatorioClienteRamoAtividadePermitido", TipoAcesso.ADMINISTRACAO);
        verificarPermissaoAcesso("acessoVendaPermitido", TipoAcesso.ADMINISTRACAO, TipoAcesso.CADASTRO_PEDIDO_VENDAS);
        verificarPermissaoAcesso("acessoCompraPermitido", TipoAcesso.ADMINISTRACAO, TipoAcesso.CADASTRO_PEDIDO_COMPRA);
        verificarPermissaoAcesso("acessoRelatorioComissaoVendedor", TipoAcesso.ADMINISTRACAO,
                TipoAcesso.CADASTRO_PEDIDO_VENDAS);
        verificarPermissaoAcesso("acessoValorReceitaPermitido", TipoAcesso.ADMINISTRACAO);
        verificarPermissaoAcesso("acessoRecepcaoCompraPermitido", TipoAcesso.RECEPCAO_COMPRA);
        verificarPermissaoAcesso("acessoNFePermitido", TipoAcesso.ADMINISTRACAO, TipoAcesso.GERENCIA_VENDAS,
                TipoAcesso.FATURAMENTO);
    }

    @Get("/")
    public void menuHome() {
    }

}
