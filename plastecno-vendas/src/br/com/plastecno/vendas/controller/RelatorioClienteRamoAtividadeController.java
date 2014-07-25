package br.com.plastecno.vendas.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.RamoAtividadeService;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioClienteRamoAtividadeController extends AbstractController {

    @Servico
    private RamoAtividadeService ramoAtividadeService;

    @Servico
    private RelatorioService relatorioService;

    public RelatorioClienteRamoAtividadeController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Get("relatorio/cliente/ramoAtividade")
    public void relatorioClienteRamoAtividadeHome() {
        addAtributo("listaRamoAtividade", this.ramoAtividadeService.pesquisar());
        addAtributo("relatorioGerado", contemAtributo("relatorio"));
    }

    @Get("relatorio/cliente/ramoAtividade/listagem")
    public void gerarRelatorioClienteRamoAtividade(Integer idRamoAtividade) {
        try {
            addAtributo("relatorio", this.relatorioService.gerarRelatorioClienteRamoAtividade(idRamoAtividade));
            addAtributo("relatorioGerado", true);
        } catch (BusinessException e) {
            this.gerarListaMensagemErro(e);
        }
        addAtributo("ramoAtividadeSelecionado", idRamoAtividade);
        irTopoPagina();
    }
}
