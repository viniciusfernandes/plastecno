package br.com.plastecno.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.RamoAtividadeService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.controller.exception.ControllerException;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RamoAtividadeController extends AbstractController {

    @Servico
    private RamoAtividadeService ramoAtividadeService;

    public RamoAtividadeController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Ramo de atividade");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.CADASTRO_BASICO);
    }

    @Post("ramo/desativacao")
    public void desativar(Integer id) {
        this.ramoAtividadeService.desativar(id);
        List<String> mensagens = new ArrayList<String>();
        mensagens.add("Ramo de atividade desativado com sucesso");
        addAtributo("listaMensagemSucesso", mensagens);
        this.irPaginaHome();
    }

    @Post(value = "ramo/inclusao")
    public void inserir(RamoAtividade ramoAtividade) {
        try {
            ramoAtividade = this.ramoAtividadeService.inserir(ramoAtividade);
            gerarMensagemCadastroSucesso(ramoAtividade, "sigla");
        } catch (BusinessException e) {
            addAtributo("ramoAtividade", ramoAtividade);
            this.gerarListaMensagemErro(e);
        } catch (ControllerException e) {
            gerarLogErroNavegacao("ramo", e);
        } catch (Exception e) {
            gerarLogErroInclusao("Ramo de Atividade", e);
        }
        this.irPaginaHome();
    }

    @Get("ramo/{idRamo}")
    public void pesquisar(Integer idRamo) {
        addAtributo("ramoAtividade", this.ramoAtividadeService.pesquisarById(idRamo));
        irTopoPagina();
    }

    @Path(value = "ramo/listagem")
    public void pesquisar(RamoAtividade filtro, Integer paginaSelecionada) {

        final PaginacaoWrapper<RamoAtividade> paginacao = this.ramoAtividadeService.paginarRamoAtividade(filtro, null,
                this.calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());

        addAtributo("ramoAtividade", filtro);
        this.inicializarPaginacao(paginaSelecionada, paginacao, "listaRamoAtividade");
    }

    @Get("ramo")
    public void ramoAtividadeHome() {
    }
}
