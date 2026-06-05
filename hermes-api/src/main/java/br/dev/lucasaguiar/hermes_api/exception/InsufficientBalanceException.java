package br.dev.lucasaguiar.hermes_api.exception;

public class InsufficientBalanceException extends  RuntimeException{
    public InsufficientBalanceException() {
        super("\"Insufficient balance\"");
    }
}
