package br.com.svr.vendas.login;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.entity.PerfilAcesso;
import br.com.svr.service.entity.Usuario;

@Component
@SessionScoped
public class UsuarioInfo {

    private Integer codigoUsuario;
    private boolean compraPermitida;
    private String email;
    private List<TipoAcesso> listaTipoAcesso;

    private String nome;
    private String nomeCompleto;
    private boolean usuarioLogado = false;
    private boolean vendaPermitida;

    public Integer getCodigoUsuario() {
        return this.codigoUsuario;
    }

    public String getDescricaoLogin() {
        return nomeCompleto + " - " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public void inicializar(Usuario usuario) {
        if (usuario == null) {
            return;
        }

        codigoUsuario = usuario.getId();
        compraPermitida = usuario.isComprador();
        nomeCompleto = usuario.getNomeCompleto();
        nome = usuario.getNome();
        email = usuario.getEmail();
        vendaPermitida = usuario.isVendedor();

        usuarioLogado = true;
        listaTipoAcesso = new ArrayList<TipoAcesso>();

        if (usuario.getListaPerfilAcesso() != null) {
            TipoAcesso tipoAcesso = null;
            for (PerfilAcesso perfil : usuario.getListaPerfilAcesso()) {
                tipoAcesso = TipoAcesso.valueOfBy(perfil.getDescricao());
                if (tipoAcesso != null) {
                    listaTipoAcesso.add(tipoAcesso);
                }
            }
        }
    }

    public boolean isAcessoNaoPermitido(TipoAcesso... tipoAcesso) {
        if (tipoAcesso == null) {
            return false;
        }
        return this.usuarioLogado && !this.listaTipoAcesso.containsAll(Arrays.asList(tipoAcesso));
    }

    public boolean isAcessoPermitido(TipoAcesso... tipoAcesso) {
        if (tipoAcesso == null) {
            return false;
        }

        for (TipoAcesso tipo : tipoAcesso) {
            if (this.usuarioLogado && this.listaTipoAcesso.contains(tipo)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCompraPermitida() {
        return compraPermitida;
    }

    public boolean isLogado() {
        return usuarioLogado;
    }

    public boolean isVendaPermitida() {
        return vendaPermitida;
    }

    public void limpar() {
        this.usuarioLogado = false;
        this.listaTipoAcesso = null;
    }
}
