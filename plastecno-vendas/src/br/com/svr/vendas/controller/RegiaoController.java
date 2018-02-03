package br.com.svr.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.EnderecamentoService;
import br.com.svr.service.RegiaoService;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.entity.Bairro;
import br.com.svr.service.entity.Regiao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.controller.exception.ControllerException;
import br.com.svr.vendas.json.CidadeBairroJson;
import br.com.svr.vendas.json.SerializacaoJson;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class RegiaoController extends AbstractController {

    @Servico
    private RegiaoService regiaoService;

    @Servico
    private EnderecamentoService enderecamentoService;

    public RegiaoController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Região");
        /*
         * Estamos definindo idBairro e descricao pois sao esses atributos
         * utilizados no Json que ira popular o picklist. Temos tambem um campo
         * de pesquisa para o CEP.
         */
        this.inicializarPicklist("Bairros para Associar", "Bairros Encontrados", "Bairros da Região", "idBairro",
                "descricao", "CEP");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.CADASTRO_BASICO);
    }

    private List<CidadeBairroJson> gerarListaCidadeBairro(List<Bairro> listaBairro) {
        final List<CidadeBairroJson> listaCidadeBairroJson = new ArrayList<CidadeBairroJson>();
        for (Bairro bairro : listaBairro) {
            listaCidadeBairroJson.add(new CidadeBairroJson(bairro.getId(), bairro.getCidade().getDescricao() + " - "
                    + bairro.getDescricao()));
        }
        return listaCidadeBairroJson;
    }

    @Post("regiao/inclusao")
    public void inserir(Regiao regiao, List<Integer> listaIdBairroAssociado) {
        try {
            this.regiaoService.inserir(regiao, listaIdBairroAssociado);
            gerarMensagemCadastroSucesso(regiao, "nome");
        } catch (BusinessException e) {
            addAtributo("regiao", regiao);
            try {
                List<CidadeBairroJson> listaCidadeBairro = this.gerarListaCidadeBairro(this.enderecamentoService
                        .pesquisarBairroById(listaIdBairroAssociado));

                popularPicklist(null, listaCidadeBairro);
                gerarListaMensagemErro(e);

            } catch (ControllerException e1) {
                gerarLogErroNavegacao("Região", e1);
            }
        } catch (Exception e) {
            gerarLogErroInclusao("Região", e);
        }
        irTopoPagina();
    }

    @Get("regiao/{idRegiao}")
    public void pesquisar(Integer idRegiao) {
        Regiao regiao = this.regiaoService.pesquisarById(idRegiao);
        addAtributo("regiao", regiao);
        try {
            popularPicklist(null, this.gerarListaCidadeBairro(regiao.getListaBairro()));
        } catch (ControllerException e) {
            gerarLogErroNavegacao("Região", e);
        }
        irTopoPagina();
    }

    @Get("regiao/listagem")
    public void pesquisar(Regiao filtro, Integer paginaSelecionada) {
        PaginacaoWrapper<Regiao> paginacao = this.regiaoService.paginarRegiao(filtro,
                this.calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());

        this.inicializarPaginacao(paginaSelecionada, paginacao, "listaRegiao");
        addAtributo("regiao", filtro);
    }

    @Get("regiao/bairro/listagem")
    public void pesquisarBairroByCEP(String cep) {
        final List<Bairro> listaBairro = this.enderecamentoService.pesquisarBairroByCep(cep);
        final List<Autocomplete> listaResultado = new ArrayList<Autocomplete>();
        for (Bairro bairro : listaBairro) {
            listaResultado.add(new Autocomplete(bairro.getId(), bairro.getCidade().getDescricao() + " - "
                    + bairro.getDescricao()));
        }
        serializarJson(new SerializacaoJson("listaResultado", listaResultado));
    }

    @Get("regiao")
    public void regiaoHome() {
        if (!isElementosAssociadosPreenchidosPicklist()) {
            try {
                popularPicklist(null, null);
            } catch (ControllerException e) {
                gerarLogErroNavegacao("Cliente", e);
            }
        }
    }

    @Post("regiao/remocao")
    public void remover(Integer idRegiao) {
        this.regiaoService.remover(idRegiao);
        try {
            gerarMensagemRemocaoSucesso();
        } catch (ControllerException e) {
            gerarLogErroNavegacao("Região", e);
        }
        irTopoPagina();
    }
}
