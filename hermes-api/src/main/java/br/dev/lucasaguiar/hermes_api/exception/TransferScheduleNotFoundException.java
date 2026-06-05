package br.dev.lucasaguiar.hermes_api.exception;

import java.util.UUID;

public class TransferScheduleNotFoundException extends RuntimeException {
    public TransferScheduleNotFoundException(UUID id) {
        super("Transfer schedule not found: " + id);
    }
}
