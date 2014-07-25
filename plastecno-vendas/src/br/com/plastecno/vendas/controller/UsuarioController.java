package br.com.plastecno.vendas.controller;

import java.util.List;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.plastecno.service.ContatoService;
import br.com.plastecno.service.PerfilAcessoService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.ContatoUsuario;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.vendas.controller.anotacao.Servico;
import br.com.plastecno.vendas.controller.exception.ControllerException;
import br.com.plastecno.vendas.login.UsuarioInfo;

@Resource
public class UsuarioController extends AbstractController {
    @Servico
    private UsuarioService usuarioService;
    @Servico
    private PerfilAcessoService perfilAcessoService;
    @Servico
    private ContatoService contatoService;

    public UsuarioController(final Result result, UsuarioInfo usuarioInfo) {
        super(result, usuarioInfo);
        this.setNomeTela("Usuario do sistema");
        this.inicializarPicklist("Perfis de Acesso", "Perfis Cadastrados", "Perfis Associados", "id", "descricao",
                false);
        this.verificarPermissaoAcesso("acessoCadastroBasicoPermitido", TipoAcesso.ADMINISTRACAO);
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

    public void pesquisarVendedorByNome() {

    }
    
    @Post("usuario/contato/remocao/{idContato}")
    public void removerContato(Integer idContato) {
        this.contatoService.remover(idContato);
        irTopoPagina();
    }

    @Post("usuario/inclusao")
    public void inserir(Double salario, Double comissao, Usuario usuario, List<ContatoUsuario> listaContato,
            Logradouro logradouro, List<Integer> listaIdPerfilAssociado, boolean isAlteracaoSenha) {
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
                    usuario.addPerfilAcesso(this.perfilAcessoService.pesquisarById(idPerfil));
                }
            }

            this.usuarioService.inserir(usuario, isAlteracaoSenha);
            this.gerarMensagemCadastroSucesso(usuario, "nome");
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
    public void editar(Integer id) {
        Usuario usuario = this.usuarioService.pesquisarById(id);
        Logradouro logradouro = this.usuarioService.pesquisarLogradouro(usuario.getId());
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

        irTopoPagina();
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

    private void inserirMascaraDocumentos(Usuario usuario) {
        usuario.setCpf(formatarCPF(usuario.getCpf()));
    }
}
