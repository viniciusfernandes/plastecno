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
import br.com.plastecno.util.NumeroUtils;
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
    public void alterarDuplicata(Integer idDuplicata, Date dataVencimento, Date dataInicial, Date dataFinal) {
        try {
            duplicataService.alterarDataVendimentoById(idDuplicata, dataVencimento);
            redirecTo(this.getClass()).gerarRelatorioDuplicata(dataInicial, dataFinal);
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
            irTopoPagina();
        }
    }

    @Get("relatorio/duplicata/listagem")
    public void gerarRelatorioDuplicata(Date dataInicial, Date dataFinal) {
        try {
            addAtributo("relatorio", relatorioService.gerarRelatorioDuplicata(new Periodo(dataInicial, dataFinal)));
        } catch (BusinessException e) {
            gerarListaMensagemErro(e);
        }

        // Aqui estamos indo para o topo da pagina pois o formulario de pesquisa
        // eh pequeno e nao ha a necessidade de rolar a pagina para baixo
        irTopoPagina();
        addAtributo("dataInicial", formatarData(dataInicial));
        addAtributo("dataFinal", formatarData(dataFinal));
    }

    @Get("duplicata/{idDuplicata}")
    public void pesquisarDuplicataById(Integer idDuplicata, Date dataInicial, Date dataFinal) {
        NFeDuplicata d = duplicataService.pesquisarDuplicataById(idDuplicata);
        if (d != null) {
            addAtributo("dataVencimento", StringUtils.formatarData(d.getDataVencimento()));
            addAtributo("valor", NumeroUtils.formatarValorMonetario(d.getValor()));
            addAtributo("idDuplicata", d.getId());
        }
        redirecTo(this.getClass()).gerarRelatorioDuplicata(dataInicial, dataFinal);

    }

    @Get("relatorio/duplicata")
    public void relatorioDuplicataHome() {
        configurarFiltroPediodoMensal();
    }
}
