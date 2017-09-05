package br.com.plastecno.service.impl;

import java.io.IOException;
import java.util.HashSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

import br.com.plastecno.service.AutenticacaoService;
import br.com.plastecno.service.ConfiguracaoSistemaService;
import br.com.plastecno.service.EmailService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.mensagem.email.AnexoEmail;
import br.com.plastecno.service.mensagem.email.MensagemEmail;
import br.com.plastecno.util.StringUtils;

@Stateless
public class EmailServiceImpl implements EmailService {

	@EJB
	private AutenticacaoService autenticacaoService;

	@EJB
	private ConfiguracaoSistemaService configuracaoSistemaService;

	@EJB
	private UsuarioService usuarioService;

	public void enviar(MensagemEmail mensagemEmail) throws NotificacaoException {

		try {
			final String REMETENTE = mensagemEmail.getRemetente();
			final String DESTINATARIO = mensagemEmail.getDestinatario();

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
			email.addTo(gerarDestinatarios(DESTINATARIO));
			email.setMsg(mensagemEmail.getConteudo());
			gerarAnexo(mensagemEmail, email);

			// email.send();

		} catch (Exception e) {
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
				if (anexo == null) {
					continue;
				}
				email.attach(new ByteArrayDataSource(anexo.getConteudo(), anexo.getTipoAnexo()), anexo.getNome(),
						anexo.getDescricao());
			}
		}
	}

	private String[] gerarDestinatarios(String destinatarios) {
		String[] dest = destinatarios.split(";");
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
