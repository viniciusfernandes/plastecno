package br.com.svr.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.ClienteService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.relatorio.RelatorioService;
import br.com.svr.service.wrapper.Periodo;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class RelatorioValorVendaClienteController extends AbstractController {

    @Servico
    private UsuarioService usuarioService;

    @Servico
    private RelatorioService relatorioService;

    @Servico
    private ClienteService clienteService;

    public RelatorioValorVendaClienteController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Post("relatorio/venda/cliente/listagem")
    public void gerarRelatorioValorVendaCliente(Date dataInicial, Date dataFinal, Cliente cliente, boolean orcamento) {
        try {
            addAtributo("relatorio", this.relatorioService.gerarRelatorioVendaCliente(orcamento, new Periodo(
                    dataInicial, dataFinal), cliente.getId()));
            irRodapePagina();
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }

        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
        addAtributo("orcamento", orcamento);
        addAtributo("cliente", cliente);
    }

    @Get("relatorio/venda/cliente")
    public void relatorioValorVendaClienteHome() {
        configurarFiltroPediodoMensal();
        addAtributo("relatorioGerado", contemAtributo("relatorio"));
        boolean acessoPesquisaVendaClientePermitido = isAcessoPermitido(TipoAcesso.ADMINISTRACAO,
                TipoAcesso.GERENCIA_VENDAS, TipoAcesso.OPERACAO_CONTABIL);
        addAtributo("acessoPesquisaVendaClientePermitido", acessoPesquisaVendaClientePermitido);
    }
}
