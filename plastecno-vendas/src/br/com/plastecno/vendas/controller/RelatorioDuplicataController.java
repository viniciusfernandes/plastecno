package br.com.plastecno.vendas.controller;

import java.util.Date;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.DuplicataService;
import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RelatorioDuplicataController extends AbstractController {
    @Servico
    private DuplicataService duplicataService;

    @Servico
    private RelatorioService relatorioService;

    public RelatorioDuplicataController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
    }

    @Post("duplicata/alteracaodata")
    public void alterarDuplicata(Integer idDuplicata, Date dataVencimento, Double valor, Date dataInicial,
            Date dataFinal) {
        try {
            duplicataService.alterarDataVendimentoValorById(idDuplicata, dataVencimento, valor);
            redirecTo(this.getClass()).gerarRelatorioDuplicata(dataInicial, dataFinal);
            gerarMensagemSucesso("Duplicata alterada com sucesso.");
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
    }

    private void gerarRelatorioByPeriodo(Date dataInicial, Date dataFinal) {
        if (contemAtributo("relatorio")) {
            return;
        }

        try {
            addAtributo("relatorio", relatorioService.gerarRelatorioDuplicata(new Periodo(dataInicial, dataFinal)));
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }
        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
    }

    @Get("relatorio/duplicata/listagem")
    public void gerarRelatorioDuplicata(Date dataInicial, Date dataFinal) {
        gerarRelatorioByPeriodo(dataInicial, dataFinal);
        irTopoPagina();
    }

    @Get("relatorio/duplicata/listagem/pedido")
    public void gerarRelatorioDuplicataByIdPedido(Integer idPedido) {
        try {
            addAtributo("relatorio", relatorioService.gerarRelatorioDuplicataByIdPedido(idPedido));
            addAtributo("idPedido", idPedido);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }

        // Aqui estamos indo para o topo da pagina pois o formulario de pesquisa
        // eh pequeno e nao ha a necessidade de rolar a pagina para baixo
        irTopoPagina();
    }

    @Get("relatorio/duplicata/listagem/nfe")
    public void gerarRelatorioDuplicataByNumeroNFe(Integer numeroNFe) {
        try {
            addAtributo("relatorio", relatorioService.gerarRelatorioDuplicataByNumeroNFe(numeroNFe));
            addAtributo("numeroNFe", numeroNFe);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }

        // Aqui estamos indo para o topo da pagina pois o formulario de pesquisa
        // eh pequeno e nao ha a necessidade de rolar a pagina para baixo
        irTopoPagina();
    }

    @Post("duplicata/liquidacao/{idDuplicata}")
    public void liquidarDuplicata(Integer idDuplicata, Date dataInicial, Date dataFinal) {
        try {
            duplicataService.liquidarDuplicataById(idDuplicata);
            redirecTo(this.getClass()).gerarRelatorioDuplicata(dataInicial, dataFinal);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
    }

    @Get("duplicata/{idDuplicata}")
    public void pesquisarDuplicataById(Integer idDuplicata, Date dataInicial, Date dataFinal) {
        NFeDuplicata d = duplicataService.pesquisarDuplicataById(idDuplicata);
        if (d != null) {
            addAtributo("dataVencimento", StringUtils.formatarData(d.getDataVencimento()));
            addAtributo("valor", d.getValor());
            addAtributo("idDuplicata", d.getId());
        }
        redirecTo(this.getClass()).gerarRelatorioDuplicata(dataInicial, dataFinal);

    }

    @Get("relatorio/duplicata")
    public void relatorioDuplicataHome() {
        gerarRelatorioByPeriodo(gerarDataInicioMes(), new Date());
    }

    @Post("duplicata/remocao/{idDuplicata}")
    public void removerDuplicata(Integer idDuplicata, Date dataInicial, Date dataFinal) {
        try {
            duplicataService.removerDuplicataById(idDuplicata);
            redirecTo(this.getClass()).gerarRelatorioDuplicata(dataInicial, dataFinal);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
    }
}
