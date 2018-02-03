package br.com.svr.service.wrapper.exception;

public class AgrupamentoException extends Exception {
    private static final long serialVersionUID = 7767112366295005389L;

    public AgrupamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
