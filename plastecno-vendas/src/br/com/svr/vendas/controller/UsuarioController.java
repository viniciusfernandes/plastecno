package br.com.svr.vendas.controller;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.ContatoService;
import br.com.svr.service.PerfilAcessoService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.entity.ContatoUsuario;
import br.com.svr.service.entity.LogradouroUsuario;
import br.com.svr.service.entity.PerfilAcesso;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;
import br.com.svr.util.StringUtils;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.controller.exception.ControllerException;
import br.com.svr.vendas.json.SerializacaoJson;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class UsuarioController extends AbstractController {
    @Servico
    private ContatoService contatoService;
    @Servico
    private PerfilAcessoService perfilAcessoService;
    @Servico
    private UsuarioService usuarioService;

    public UsuarioController(final Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Usuario do sistema");
        this.inicializarPicklist("Perfis de Acesso", "Perfis Cadastrados", "Perfis Associados", "id", "descricao",
                false);
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.ADMINISTRACAO);
    }

    @Post("usuario/desativacao")
    public void destivar(Integer idUsuario) {
        try {
            this.usuarioService.desabilitar(idUsuario);
            gerarMensagemSucesso("Usuario desabilitado com sucesso.");
        } catch (BusinessException e) {
            addAtributo("listaMensagemErro", e.getListaMensagem());
        } catch (Exception e) {
            gerarLogErroInclusao("Usuario", e);
        }
        this.irPaginaHome();
    }

    @Post("usuario/inclusao")
    public void inserir(Double salario, Double comissao, Usuario usuario, List<ContatoUsuario> listaContato,
            LogradouroUsuario logradouro, List<Integer> listaIdPerfilAssociado, boolean isAlteracaoSenha) {
        try {

            if (usuario.getId() == null) {
                isAlteracaoSenha = true;
            }

            if (temElementos(listaContato)) {
                usuario.addContato(listaContato);
            }

            if (logradouro != null && StringUtils.isNotEmpty(logradouro.getCep())) {
                usuario.setLogradouro(logradouro);
            }

            if (temElementos(listaIdPerfilAssociado)) {
                for (Integer idPerfil : listaIdPerfilAssociado) {
                    usuario.addPerfilAcesso(new PerfilAcesso(idPerfil));
                }
            }

            usuarioService.inserir(usuario, isAlteracaoSenha);
            gerarMensagemCadastroSucesso(usuario, "nome");
        } catch (BusinessException e) {
            inserirMascaraDocumentos(usuario);
            addAtributo("usuario", usuario);
            addAtributo("logradouro", logradouro);
            addAtributo("isAlteracaoSenha", isAlteracaoSenha);
            addAtributo("tipoLogradouroSelecionado", logradouro != null ? logradouro.getTipoLogradouro() : null);
            addAtributo("listaContato", listaContato);

            try {
                popularPicklist(perfilAcessoService.pesquisarComplementaresById(listaIdPerfilAssociado),
                        perfilAcessoService.pesquisarById(listaIdPerfilAssociado));
                this.gerarListaMensagemErro(e);
            } catch (ControllerException e1) {
                gerarLogErroNavegacao("Usuario", e1);
            }
        } catch (Exception e) {
            gerarLogErroInclusao("Usuario", e);
        }
        irTopoPagina();
    }

    private void inserirMascaraDocumentos(Usuario usuario) {
        usuario.setCpf(formatarCPF(usuario.getCpf()));
    }

    @Get("usuario/listagem")
    public void pesquisar(Usuario filtro, Integer paginaSelecionada) {
        final PaginacaoWrapper<Usuario> paginacao = this.usuarioService.paginarUsuario(filtro, false, false,
                this.calcularIndiceRegistroInicial(paginaSelecionada), getNumerRegistrosPorPagina());

        for (Usuario usuario : paginacao.getLista()) {
            inserirMascaraDocumentos(usuario);
        }

        this.inicializarPaginacao(paginaSelecionada, paginacao, "listaUsuario");
        addAtributo("usuario", filtro);
    }

    @Get("usuario/edicao")
    public void pesquisarUsuarioById(Integer id) {
        Usuario usuario = this.usuarioService.pesquisarById(id);
        if (usuario == null) {
            this.gerarListaMensagemErro("Usuário não existe no sistema");
        } else {
            LogradouroUsuario logradouro = this.usuarioService.pesquisarLogradouro(usuario.getId());
            usuario.setLogradouro(logradouro);

            usuario.setSenha(null);
            inserirMascaraDocumentos(usuario);
            addAtributo("tipoLogradouroSelecionado", logradouro != null ? logradouro.getTipoLogradouro() : null);
            addAtributo("usuario", usuario);
            addAtributo("logradouro", logradouro);
            addAtributo("listaContato", this.usuarioService.pesquisarContatos(id));

            try {
                popularPicklist(usuarioService.pesquisarPerfisNaoAssociados(id),
                        usuarioService.pesquisarPerfisAssociados(id));
            } catch (ControllerException e) {
                gerarLogErroNavegacao("Usuario", e);
            }
        }
        irTopoPagina();
    }

    @Get("usuario/listagem/nome")
    public void pesquisarUsuarioByNome(String nome) {
        List<Autocomplete> lista = new ArrayList<Autocomplete>();
        List<Usuario> listaUsuario = usuarioService.pesquisarByNome(nome);
        for (Usuario usuario : listaUsuario) {
            lista.add(new Autocomplete(usuario.getId(), usuario.getNome()));
        }
        serializarJson(new SerializacaoJson("lista", lista));
    }

    @Post("usuario/contato/remocao/{idContato}")
    public void removerContato(Integer idContato) {
        this.contatoService.remover(idContato);
        irTopoPagina();
    }

    @Post("usuario/remocaologradouro")
    public void removerLogradouro(Integer idLogradouro) {
        usuarioService.removerLogradouro(idLogradouro);
        gerarMensagemSucesso("O logradouro do usuário foi removido com sucesso.");
        irTopoPagina();
    }

    @Get("usuario")
    public void usuarioHome() {
        inicializarComboTipoLogradouro();

        if (!isElementosNaoAssociadosPreenchidosPicklist()) {

            try {
                popularPicklist(this.perfilAcessoService.pesquisar(), null);
            } catch (ControllerException e) {
                gerarLogErroNavegacao("Usuario", e);
            }
        }
    }
}
