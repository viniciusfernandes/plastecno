package br.com.plastecno.vendas.controller;

import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ContatoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.entity.ComentarioRepresentada;
import br.com.plastecno.service.entity.ContatoRepresentada;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class RepresentadaController extends AbstractController {
    @Servico
    private RepresentadaService representadaService;

    @Servico
    private ContatoService contatoService;

    @Servico
    private ClienteService clienteService;

    public RepresentadaController(Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Representada");
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.CADASTRO_BASICO);
    }

    @Post("representada/desativacao")
    public void desativar(Integer idRepresentada) {
        this.representadaService.desativar(idRepresentada);
        irTopoPagina();
        this.gerarMensagemSucesso("Representada desativada com sucesso");
    }

    private void formataDocumentos(Representada representada) {
        representada.setCnpj(this.formatarCNPJ(representada.getCnpj()));
        representada.setInscricaoEstadual(this.formatarInscricaoEstadual(representada.getInscricaoEstadual()));
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
    public void inserir(Representada representada, Logradouro logradouro, List<ContatoRepresentada> listaContato) {
        try {

            if (hasAtributo(logradouro)) {
                logradouro.setTipoLogradouro(TipoLogradouro.COMERCIAL);
                representada.setLogradouro(logradouro);
            }

            if (temElementos(listaContato)) {
                representada.addContato(listaContato);
            }

            representada.setComissao(NumeroUtils.gerarAliquota(representada.getComissao()));

            this.representadaService.inserir(representada);
            this.gerarMensagemCadastroSucesso(representada, "nomeFantasia");
        } catch (BusinessException e) {
            this.formataDocumentos(representada);
            addAtributo("representada", representada);
            addAtributo("logradouro", representada.getLogradouro());
            addAtributo("listaContato", listaContato);
            this.gerarListaMensagemErro(e);
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
                this.gerarMensagemSucesso("Comentário sobre o representada/fornecedor \"" + nomeFantasia
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
        filtro.setCnpj(this.removerMascaraDocumento(filtro.getCnpj()));
        this.paginarPesquisa(paginaSelecionada, this.representadaService.pesquisarTotalRegistros(filtro, null));

        List<Representada> lista = this.representadaService.pesquisarBy(filtro, null,
                this.calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());

        for (Representada representada : lista) {
            this.formataDocumentos(representada);
        }

        addAtributo("representada", filtro);
        addAtributo("listaRepresentada", lista);
    }

    @Get("representada/edicao")
    public void pesquisarRepresentadaById(Integer id) {
        Representada representada = this.representadaService.pesquisarById(id);
        this.formataDocumentos(representada);

        addAtributo("representada", representada);
        addAtributo("listaContato", this.representadaService.pesquisarContato(id));
        addAtributo("logradouro", this.representadaService.pesquisarLogradorouro(id));
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
