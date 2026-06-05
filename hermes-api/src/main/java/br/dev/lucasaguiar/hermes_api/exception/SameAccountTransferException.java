package br.dev.lucasaguiar.hermes_api.exception;

public class SameAccountTransferException extends RuntimeException {
    public SameAccountTransferException() {
        super("Source and target accounts must be different");
    }
}
