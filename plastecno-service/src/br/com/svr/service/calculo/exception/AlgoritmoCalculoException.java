package br.com.svr.service.calculo.exception;

import br.com.svr.service.exception.BusinessException;

public class AlgoritmoCalculoException extends BusinessException {
    private static final long serialVersionUID = 6849011967918990916L;

    public AlgoritmoCalculoException() {
    }

    public AlgoritmoCalculoException(String mensagem) {
        super(mensagem);
    }

    public AlgoritmoCalculoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
