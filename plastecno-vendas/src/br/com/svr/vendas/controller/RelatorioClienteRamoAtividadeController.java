package br.com.svr.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.RamoAtividadeService;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.relatorio.RelatorioService;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class RelatorioClienteRamoAtividadeController extends AbstractController {

    @Servico
    private RamoAtividadeService ramoAtividadeService;

    @Servico
    private RelatorioService relatorioService;

    public RelatorioClienteRamoAtividadeController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/cliente/ramoAtividade/listagem")
    public void gerarRelatorioClienteRamoAtividade(Integer idRamoAtividade) {
        try {
            addAtributo("relatorio", relatorioService.gerarRelatorioClienteRamoAtividade(idRamoAtividade));
        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
        }
        addAtributo("ramoAtividadeSelecionado", idRamoAtividade);
        irTopoPagina();
    }

    @Get("relatorio/cliente/ramoAtividade")
    public void relatorioClienteRamoAtividadeHome() {
        addAtributo("listaRamoAtividade", ramoAtividadeService.pesquisar());
    }
}
