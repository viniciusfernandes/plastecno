package br.com.svr.vendas.controller;

import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.MaterialService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.entity.Material;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.controller.exception.ControllerException;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public final class MaterialController extends AbstractController {
    @Servico
    private MaterialService materialService;
    @Servico
    private RepresentadaService representadaService;

    public MaterialController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Material");
        this.inicializarPicklist("Representadas", "Representadas Cadastradas", "Representadas Associadas", "id",
                "nomeFantasia");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.ADMINISTRACAO, TipoAcesso.CADASTRO_BASICO);
    }

    @Post("material/desativacao")
    public void desativar(Integer id) {
        this.materialService.desativar(id);
        gerarMensagemSucesso("Material desativado com sucesso");
        this.irPaginaHome();
    }

    @Post(value = "material/inclusao")
    public void inserir(Material material, List<Integer> listaIdRepresentadaAssociada) {
        try {

            this.materialService.inserir(material, listaIdRepresentadaAssociada);
            this.gerarMensagemCadastroSucesso(material, "sigla");
        } catch (BusinessException e) {
            addAtributo("material", material);
            try {
                popularPicklist(null, this.representadaService.pesquisarById(listaIdRepresentadaAssociada));
                this.gerarListaMensagemErro(e);
            } catch (ControllerException e1) {
                gerarLogErroNavegacao("Cliente", e);
            }

        } catch (Exception e) {
            gerarLogErroInclusao("Material", e);
        }
        this.irPaginaHome();
    }

    @Get("material")
    public void materialHome() {

        // No caso em que temos uma excecao na insercao de um novo registro nao
        // devemos popular o picklist
        if (!isElementosAssociadosPreenchidosPicklist()) {
            try {
                popularPicklist(this.representadaService.pesquisarRepresentadaEFornecedor(), null);
            } catch (ControllerException e) {
                gerarLogErroNavegacao("Material", e);
            }
        }
    }

    @Post("material/edicao")
    public void pesquisar(Integer id) {
        Material material = this.materialService.pesquisarById(id);
        addAtributo("material", material);

        try {
            popularPicklist(this.materialService.pesquisarRepresentadasNaoAssociadas(id),
                    this.materialService.pesquisarRepresentadasAssociadas(id));
        } catch (ControllerException e) {
            gerarLogErroNavegacao("Material", e);
        }

        irTopoPagina();
    }

    @Get("material/listagem")
    public void pesquisar(Material filtro, Integer paginaSelecionada) {
        final PaginacaoWrapper<Material> paginacao = this.materialService.paginarMaterial(filtro, null,
                calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());

        this.inicializarPaginacao(paginaSelecionada, paginacao, "listaMaterial");
        addAtributo("material", filtro);
    }
}
