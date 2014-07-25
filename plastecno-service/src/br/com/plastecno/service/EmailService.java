package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.mensagem.email.MensagemEmail;

@Local
public interface EmailService {

    void enviar(MensagemEmail mensagemEmail) throws NotificacaoException;
}
