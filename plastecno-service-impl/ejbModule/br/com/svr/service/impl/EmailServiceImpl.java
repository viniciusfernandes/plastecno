package br.com.svr.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

import br.com.svr.service.AutenticacaoService;
import br.com.svr.service.ConfiguracaoSistemaService;
import br.com.svr.service.EmailService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.constante.ParametroConfiguracaoSistema;
import br.com.svr.service.exception.NotificacaoException;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.mensagem.email.MensagemEmail;
import br.com.svr.util.StringUtils;

@Stateless
public class EmailServiceImpl implements EmailService {

	@EJB
	private AutenticacaoService autenticacaoService;

	@EJB
	private ConfiguracaoSistemaService configuracaoSistemaService;

	@EJB
	private UsuarioService usuarioService;
	private Logger log = Logger.getLogger(this.getClass().getName());

	public void enviar(MensagemEmail mensagemEmail) throws NotificacaoException {

		try {
			final String REMETENTE = mensagemEmail.getRemetente();
			final String DESTINATARIO = mensagemEmail.getDestinatario();
			final String DESTINATARIO_CC = mensagemEmail.getDestinatarioCc();
			final String SENHA = autenticacaoService.decriptografar(usuarioService.pesquisarSenhaByEmail(REMETENTE));

			MultiPartEmail email = new HtmlEmail();
			email.setHostName(configuracaoSistemaService.pesquisar(ParametroConfiguracaoSistema.NOME_SERVIDOR_SMTP));
			email.setSmtpPort(Integer.parseInt(configuracaoSistemaService
					.pesquisar(ParametroConfiguracaoSistema.PORTA_SERVIDOR_SMTP)));
			email.setAuthenticator(new DefaultAuthenticator(REMETENTE, SENHA));
			email.setSSLOnConnect(Boolean.valueOf(configuracaoSistemaService
					.pesquisar(ParametroConfiguracaoSistema.SSL_HABILITADO_PARA_SMTP)));
			email.setSubject(mensagemEmail.getTitulo());
			if (StringUtils.isEmpty(REMETENTE)) {
				throw new NotificacaoException("Endereco de email do remetente eh obrigatorio");
			}

			if (StringUtils.isEmpty(DESTINATARIO)) {
				throw new NotificacaoException("Endereco de email para envio eh obrigatorio");
			}

			if (StringUtils.isEmpty(mensagemEmail.getConteudo())) {
				throw new NotificacaoException("Conteudo do email eh obrigatorio");
			}

			email.setFrom(REMETENTE);
			email.addTo(gerarEmails(DESTINATARIO));
			email.setMsg(mensagemEmail.getConteudo());
			final String[] cc = gerarEmails(DESTINATARIO_CC);
			if (cc != null) {
				email.addCc(cc);
			}
			gerarAnexo(mensagemEmail, email);
			// email.setTLS(true);
			// email.send();

		} catch (Exception e) {
			log.log(Level.SEVERE, "Falha na configuracao do envio de email.", e);
			StringBuilder mensagem = new StringBuilder();
			mensagem.append("Falha no envio de email de ");
			mensagem.append(mensagemEmail.getRemetente());
			mensagem.append(" para ");
			mensagem.append(mensagemEmail.getDestinatario());
			throw new NotificacaoException(mensagem.toString(), e);
		}
	}

	private void gerarAnexo(MensagemEmail mensagemEmail, MultiPartEmail email) throws EmailException, IOException {
		if (mensagemEmail.contemAnexo()) {
			for (AnexoEmail anexo : mensagemEmail.getListaAnexo()) {
				email.attach(new ByteArrayDataSource(anexo.getConteudo(), anexo.getTipoAnexo()), anexo.getNome(),
						anexo.getDescricao());
			}
		}
	}

	private String[] gerarEmails(String emails) {
		if (emails == null || emails.isEmpty()) {
			return null;
		}
		String[] dest = emails.split(";");
		if (dest.length == 1) {
			return dest;
		}
		// Aqui estamos utilizando o Set para remover os emails repetivos dos
		// destinatarios
		HashSet<String> s = new HashSet<String>();
		for (String e : dest) {
			e = e.trim();
			if (e.isEmpty()) {
				continue;
			}
			s.add(e.trim());
		}
		return s.toArray(new String[] {});
	}
}
