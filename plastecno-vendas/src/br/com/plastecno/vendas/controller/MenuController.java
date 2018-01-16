package br.com.plastecno.vendas.controller;

import static br.com.plastecno.service.constante.TipoAcesso.ADMINISTRACAO;
import static br.com.plastecno.service.constante.TipoAcesso.CADASTRO_PEDIDO_COMPRA;
import static br.com.plastecno.service.constante.TipoAcesso.CADASTRO_PEDIDO_VENDAS;
import static br.com.plastecno.service.constante.TipoAcesso.CONSULTA_RELATORIO_CLIENTE_REGIAO;
import static br.com.plastecno.service.constante.TipoAcesso.CONSULTA_RELATORIO_ENTREGA;
import static br.com.plastecno.service.constante.TipoAcesso.CONSULTA_RELATORIO_VENDAS_REPRESENTADA;
import static br.com.plastecno.service.constante.TipoAcesso.FATURAMENTO;
import static br.com.plastecno.service.constante.TipoAcesso.GERENCIA_VENDAS;
import static br.com.plastecno.service.constante.TipoAcesso.OPERACAO_CONTABIL;
import static br.com.plastecno.service.constante.TipoAcesso.RECEPCAO_COMPRA;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
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
        // verificarPermissaoAcesso("acessoManutencaoPermitido",
        // TipoAcesso.MANUTENCAO);
        verificarPermissaoAcesso("acessoAdministracaoPermitido", ADMINISTRACAO);
        verificarPermissaoAcesso("acessoRelatorioClienteRegiaoPermitido", CONSULTA_RELATORIO_CLIENTE_REGIAO);
        verificarPermissaoAcesso("acessoRelatorioVendasRepresentadaPermitido", CONSULTA_RELATORIO_VENDAS_REPRESENTADA);
        verificarPermissaoAcesso("acessoRelatorioEntregaPermitido", CONSULTA_RELATORIO_ENTREGA);
        verificarPermissaoAcesso("acessoRelatorioPedidoRepresentadaPermitido", ADMINISTRACAO, OPERACAO_CONTABIL);
        verificarPermissaoAcesso("acessoRelatorioClienteRamoAtividadePermitido", ADMINISTRACAO, GERENCIA_VENDAS);
        verificarPermissaoAcesso("acessoRelatorioClienteRamoAtividadePermitido", ADMINISTRACAO);
        verificarPermissaoAcesso("acessoVendaPermitido", ADMINISTRACAO, CADASTRO_PEDIDO_VENDAS);
        verificarPermissaoAcesso("acessoCompraPermitido", ADMINISTRACAO, CADASTRO_PEDIDO_COMPRA);
        verificarPermissaoAcesso("acessoRelatorioComissaoVendedorPermitido", ADMINISTRACAO, CADASTRO_PEDIDO_VENDAS,
                OPERACAO_CONTABIL);
        verificarPermissaoAcesso("acessoValorReceitaPermitido", ADMINISTRACAO);
        verificarPermissaoAcesso("acessoRecepcaoCompraPermitido", ADMINISTRACAO, RECEPCAO_COMPRA);
        verificarPermissaoAcesso("acessoNFePermitido", ADMINISTRACAO, GERENCIA_VENDAS, FATURAMENTO);
        verificarPermissaoAcesso("acessoRelatorioDuplicataPermitido", ADMINISTRACAO, FATURAMENTO);
        verificarPermissaoAcesso("acessoRelatorioFaturamentoPermitido", ADMINISTRACAO);
        verificarPermissaoAcesso("acessoPagamentoPermitido", ADMINISTRACAO, CADASTRO_PEDIDO_COMPRA, OPERACAO_CONTABIL);
        verificarPermissaoAcesso("acessoFluxoCaixaPermitido", ADMINISTRACAO, OPERACAO_CONTABIL);
    }

    @Get("/")
    public void menuHome() {
    }

}
