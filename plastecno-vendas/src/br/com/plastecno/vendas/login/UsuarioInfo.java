package br.com.plastecno.vendas.login;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.Usuario;

@Component
@SessionScoped
public class UsuarioInfo {

    private Integer codigoUsuario;
    private boolean usuarioLogado = false;
    private List<TipoAcesso> listaTipoAcesso;
    private boolean vendaPermitida;
    private String nomeCompleto;

    public void inicializar(Usuario usuario) {
        if (usuario == null) {
            return;
        }

        this.codigoUsuario = usuario.getId();
        this.vendaPermitida = usuario.isVendedorAtivo();
        this.nomeCompleto = usuario.getNomeCompleto();
        this.usuarioLogado = true;
        this.listaTipoAcesso = new ArrayList<TipoAcesso>();

        if (usuario.getListaPerfilAcesso() != null) {
            TipoAcesso tipoAcesso = null;
            for (PerfilAcesso perfil : usuario.getListaPerfilAcesso()) {
                tipoAcesso = TipoAcesso.valueOfBy(perfil.getDescricao());
                if (tipoAcesso != null) {
                    this.listaTipoAcesso.add(tipoAcesso);
                }
            }
        }

    }

    public boolean isVendaPermitida() {
        return vendaPermitida;
    }

    public boolean isLogado() {
        return usuarioLogado;
    }

    public void limpar() {
        this.usuarioLogado = false;
        this.listaTipoAcesso = null;
    }

    public Integer getCodigoUsuario() {
        return this.codigoUsuario;
    }

    public String getDescricaoLogin() {
        return nomeCompleto + " - " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
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

    public boolean isAcessoNaoPermitido(TipoAcesso... tipoAcesso) {
        if (tipoAcesso == null) {
            return false;
        }
        return this.usuarioLogado && !this.listaTipoAcesso.containsAll(Arrays.asList(tipoAcesso));
    }
}
