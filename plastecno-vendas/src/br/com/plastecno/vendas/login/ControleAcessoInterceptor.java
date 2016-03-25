package br.com.plastecno.vendas.login;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.SessionScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.plastecno.vendas.controller.LoginController;
import br.com.plastecno.vendas.controller.anotacao.Login;

/*
 * Consideramos como escopo de sessao pois em qualquer momento que fosse
 * disparado um ajax os recursos serao interceptados podem impactar na
 * performance.
 */
@SessionScoped
@Intercepts
public class ControleAcessoInterceptor implements Interceptor {

    private Result result;
    private UsuarioInfo usuarioInfo;
    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final boolean auditoriaHabilidata;

    public ControleAcessoInterceptor(Result result, UsuarioInfo usuarioInfo, HttpServletRequest request) {
        this.result = result;
        this.usuarioInfo = usuarioInfo;
        auditoriaHabilidata = Boolean.parseBoolean(request.getServletContext().getInitParameter("auditoriaHabilidata"));
    }

    @Override
    public boolean accepts(ResourceMethod metodo) {
        return !metodo.getMethod().isAnnotationPresent(Login.class);
    }

    @Override
    public void intercept(InterceptorStack stack, ResourceMethod metodo, Object resourceInstance) {
        /*
         * Caso o usuario nao esteja logado no sistema, vamos direciona-lo a
         * tela de login. Apos configurarmos o redirecionamento, chamamos o
         * stack.next para dar sequencia na executar do metodo.
         */
        if (!usuarioInfo.isLogado()) {
            this.result.forwardTo(LoginController.class).redirecionarLogin();
        }

        if (auditoriaHabilidata) {
            Get get = null;
            Post post = null;
            Path path = null;
            String recurso = null;
            if ((get = metodo.getMethod().getAnnotation(Get.class)) != null) {
                recurso = get.value()[0];
            } else if ((post = metodo.getMethod().getAnnotation(Post.class)) != null) {
                recurso = post.value()[0];
            } else if ((path = metodo.getMethod().getAnnotation(Path.class)) != null) {
                recurso = path.value()[0];
            }
            logger.log(Level.INFO, "Usuario " + usuarioInfo.getDescricaoLogin() + ". Acessando o recurso: " + recurso);
        }

        try {
            stack.next(metodo, resourceInstance);
        } catch (InterceptionException e) {
            String mensagem = null;
            if (usuarioInfo.isLogado()) {
                mensagem = "Falha no redirecionamento do usuario \"" + usuarioInfo.getDescricaoLogin() + "\""
                        + " que requisitou o metodo " + metodo.getMethod().getName();
            } else {
                mensagem = "Tentativa de acesso por usuario nao autenticado ou timeoute de sessao";
            }
            logger.log(Level.WARNING, mensagem);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Falha na interceptacao do metodo \"" + metodo.getMethod().getName()
                    + "\" efetuado pelo usuario: " + usuarioInfo.getDescricaoLogin());
            throw e;
        }
    }

}
