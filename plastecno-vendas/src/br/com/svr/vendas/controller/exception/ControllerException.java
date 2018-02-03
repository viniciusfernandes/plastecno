package br.com.svr.vendas.controller.exception;

public class ControllerException extends Exception {

    private static final long serialVersionUID = 7502705212889022410L;

    public ControllerException(String mensagem) {
        super(mensagem);
    }
}
