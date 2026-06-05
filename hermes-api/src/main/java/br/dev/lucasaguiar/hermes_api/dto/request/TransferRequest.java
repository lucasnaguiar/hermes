package br.dev.lucasaguiar.hermes_api.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TransferRequest {
    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "Account number must be exactly 10 digits")
    private String sourceAccount;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "Account number must be exactly 10 digits")
    private String targetAccount;

    @NotNull
    @DecimalMin(value = "0.01", message = "Transfer amount must be greater than zero")
    private BigDecimal transferAmount;

    @NotNull
    @Future(message = "Transfer date must be in the future")
    private LocalDate transferDate;
}

