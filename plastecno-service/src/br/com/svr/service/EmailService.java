package br.com.svr.service;

import javax.ejb.Local;

import br.com.svr.service.exception.NotificacaoException;
import br.com.svr.service.mensagem.email.MensagemEmail;

@Local
public interface EmailService {

    void enviar(MensagemEmail mensagemEmail) throws NotificacaoException;
}
