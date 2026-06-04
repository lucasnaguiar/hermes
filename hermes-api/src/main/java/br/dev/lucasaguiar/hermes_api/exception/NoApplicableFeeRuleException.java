package br.dev.lucasaguiar.hermes_api.exception;

public class NoApplicableFeeRuleException extends RuntimeException{
    public NoApplicableFeeRuleException(int days) {
        super("\"Não existe regra de taxa para transferências com 60 dias de antecedência.\"");
    }
}
