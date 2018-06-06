package br.com.svr.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.ClienteService;
import br.com.svr.service.ContatoService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.constante.TipoApresentacaoIPI;
import br.com.svr.service.constante.TipoLogradouro;
import br.com.svr.service.entity.ComentarioRepresentada;
import br.com.svr.service.entity.ContatoRepresentada;
import br.com.svr.service.entity.LogradouroRepresentada;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.util.NumeroUtils;
import br.com.svr.util.StringUtils;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.json.SerializacaoJson;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class RepresentadaController extends AbstractController {
    @Servico
    private ClienteService clienteService;

    @Servico
    private ContatoService contatoService;

    @Servico
    private RepresentadaService representadaService;

    public RepresentadaController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Representada");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.ADMINISTRACAO,
                TipoAcesso.CADASTRO_BASICO);
    }

    @Post("representada/desativacao")
    public void desativar(Integer idRepresentada) {
        this.representadaService.desativar(idRepresentada);
        irTopoPagina();
        this.gerarMensagemSucesso("Representada desativada com sucesso");
    }

    private void formataAliquotas(Representada representada) {
        representada.setComissao(NumeroUtils.gerarPercentual(representada.getComissao(), 2));
        representada.setAliquotaICMS(NumeroUtils.gerarPercentualInteiro(representada.getAliquotaICMS()));
    }

    private String formatarComentarios(Integer idRepresentada) {
        List<ComentarioRepresentada> listaComentario = representadaService
                .pesquisarComentarioByIdRepresentada(idRepresentada);
        StringBuilder concat = new StringBuilder();
        for (ComentarioRepresentada comentario : listaComentario) {
            concat.append("\n");
            concat.append(StringUtils.formatarData(comentario.getDataInclusao()));
            concat.append(" - ");
            concat.append(comentario.getNomeUsuario());
            concat.append(" ");
            concat.append(comentario.getSobrenomeUsuario());
            concat.append(" - ");
            concat.append(comentario.getConteudo());
            concat.append("\n");
        }
        return concat.toString();
    }

    @Post("representada/inclusao")
    public void inserir(Representada representada, LogradouroRepresentada logradouro,
            List<ContatoRepresentada> listaContato) {
        try {

            if (hasAtributo(logradouro)) {
                logradouro.setTipoLogradouro(TipoLogradouro.COMERCIAL);
                representada.setLogradouro(logradouro);
            }

            if (temElementos(listaContato)) {
                representada.addContato(listaContato);
            }

            representada.setComissao(NumeroUtils.gerarAliquota(representada.getComissao(), 3));
            representada.setAliquotaICMS(NumeroUtils.gerarAliquota(representada.getAliquotaICMS()));

            representadaService.inserir(representada);
            gerarMensagemCadastroSucesso(representada, "nomeFantasia");
        } catch (BusinessException e) {
            formataAliquotas(representada);
            addAtributo("representada", representada);
            addAtributo("logradouro", representada.getLogradouro());
            addAtributo("listaContato", listaContato);
            gerarListaMensagemErro(e);
        } catch (Exception e) {
            gerarLogErroInclusao("Representada", e);
        }
        irTopoPagina();
    }

    @Post("representada/inclusao/comentario")
    public void inserirComentario(Integer idRepresentada, String comentario) {

        if (idRepresentada == null) {
            gerarListaMensagemErro("Para inserir um comentário é necessário escolher uma representada/fornecedor.");
            irTopoPagina();
        } else {
            try {
                representadaService.inserirComentario(getCodigoUsuario(), idRepresentada, comentario);
                String nomeFantasia = representadaService.pesquisarNomeFantasiaById(idRepresentada);
                gerarMensagemSucesso("Comentário sobre o representada/fornecedor \"" + nomeFantasia
                        + "\" inserido com sucesso.");
            } catch (BusinessException e) {
                gerarListaMensagemErro(e);
                addAtributo("comentario", comentario);
            }
            redirecTo(this.getClass()).pesquisarRepresentadaById(idRepresentada);
        }
    }

    @Get("representada/listagem")
    public void pesquisar(Representada filtro, Integer paginaSelecionada) {
        filtro.setCnpj(removerMascaraDocumento(filtro.getCnpj()));
        this.paginarPesquisa(paginaSelecionada, representadaService.pesquisarTotalRegistros(filtro, null));

        List<Representada> lista = representadaService.pesquisarBy(filtro, null,
                calcularIndiceRegistroInicial(paginaSelecionada), getNumeroRegistrosPorPagina());

        for (Representada representada : lista) {
            formatarDocumento(representada);
            formataAliquotas(representada);
        }

        addAtributo("representada", filtro);
        addAtributo("listaRepresentada", lista);
    }

    @Get("representada/fornecedor/listagem")
    public void pesquisarFornecedorByNomeFantasia(String nomeFantasia) {
        List<Representada> listaFornecedor = representadaService.pesquisarFornecedorAtivoByNomeFantasia(nomeFantasia);
        List<Autocomplete> lista = new ArrayList<Autocomplete>(50);
        for (Representada f : listaFornecedor) {
            lista.add(new Autocomplete(f.getId(), f.getNomeFantasia()));
        }
        serializarJson(new SerializacaoJson("lista", lista));

    }

    @Get("representada/edicao")
    public void pesquisarRepresentadaById(Integer id) {
        Representada representada = representadaService.pesquisarById(id);

        formataAliquotas(representada);

        addAtributo("representada", representada);
        addAtributo("listaContato", representadaService.pesquisarContato(id));
        addAtributo("logradouro", representadaService.pesquisarLogradorouro(id));
        addAtributo("tipoApresentacaoIPISelecionada", representada.getTipoApresentacaoIPI());
        addAtributo("comentarios", formatarComentarios(id));

        irTopoPagina();
    }

    @Post("representada/contato/remocao/{idContato}")
    public void removerContato(Integer idContato) {
        this.contatoService.remover(idContato);
        irTopoPagina();
    }

    @Get("representada")
    public void representadaHome() {
        addAtributo("listaTipoApresentacaoIPI", TipoApresentacaoIPI.values());
    }
}
