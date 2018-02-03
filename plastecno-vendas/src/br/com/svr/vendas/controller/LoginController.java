package br.com.svr.vendas.controller;

import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.svr.service.AutenticacaoService;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.AutenticacaoException;
import br.com.svr.vendas.controller.anotacao.Login;
import br.com.svr.vendas.controller.anotacao.Servico;
import br.com.svr.vendas.login.UsuarioInfo;

@Resource
public class LoginController extends AbstractController {

    @Servico
    private AutenticacaoService autenticacaoService;
    private HttpSession sessao;
    // Variavel necessaria para efetuar a inicializacao das infos do usuario
    // autenticado.
    private UsuarioInfo usuarioInfo;

    public LoginController(Result result, UsuarioInfo usuarioInfo, HttpSession httpSession) {
        super(result, usuarioInfo);
        this.usuarioInfo = usuarioInfo;
        this.sessao = httpSession;
    }

    private void inicializarUsuarioInfo(Usuario usuario) {
        this.usuarioInfo.inicializar(usuario);
    }

    @Login
    @Post("login/entrar")
    public void logar(String email, String senha) {
        try {
            Usuario usuario = autenticacaoService.autenticar(email, senha);

            if (usuario == null) {
                gerarListaMensagemAlerta("Falha na autenticação. Usuario/Senha inválido(s)");
                irPaginaHome();
            } else if (!usuario.isAtivo()) {
                gerarListaMensagemAlerta("Falha na autenticação. Usuario desativado.");
                irPaginaHome();
            } else {
                inicializarUsuarioInfo(usuario);
                redirecTo(MenuController.class).menuHome();
            }

        } catch (AutenticacaoException e) {
            this.gerarListaMensagemErro(e.getListaMensagem());
            forwardTo(ErroController.class).erroHome();
        }
    }

    @Login
    @Get("login")
    public void loginHome() {
    }

    /*
     * Esse metodo foi implementado para o redirecionamento para a tela de login
     * caso o usuario tenha a sessao expirada, pois, ao clicarmos em qualquer
     * link do menu, o conteudo da tela de login era inserida no iframe da tela
     * principal, ja que todos os itens do menu, ao serem clicados, redirecionam
     * o conteudo da resposta do servidor para o iframe, sendo assim, tivemos
     * que criar uma instrucao para que a tela de menu aponte para a URL da tela
     * de login, sendo que esse redirecionamento sera efetuado do lado do
     * cliente atraves de um metodo em javascrpt.
     */
    @Get("login/redirecionar")
    public void redirecionarLogin() {
    }

    @Login
    @Get("/login/sair")
    public void sair() {
        this.sessao.invalidate();
        redirecTo(this.getClass()).redirecionarLogin();
    }
}
