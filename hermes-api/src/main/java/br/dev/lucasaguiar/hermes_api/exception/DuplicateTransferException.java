package br.dev.lucasaguiar.hermes_api.exception;

public class DuplicateTransferException extends RuntimeException {
    public DuplicateTransferException() {
        super("\"A transfer with the same details is already scheduled for this date\"");
    }
}
