package br.dev.lucasaguiar.hermes_api.dto.response;

import br.dev.lucasaguiar.hermes_api.domain.AppliedFeeSnapshot;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TransferSimulateResponse {
    private String sourceAccount;
    private String targetAccount;
    private BigDecimal transferAmount;
    private LocalDate transferDate;
    private LocalDate schedulingDate;
    private BigDecimal feeAmount;
    private BigDecimal feeRate;
    private BigDecimal feeFixedAmount;
    private Integer feeDaysRangeFrom;
    private Integer feeDaysRangeTo;

    public static TransferSimulateResponse from(
        String sourceAccount,
        String targetAccount,
        BigDecimal transferAmount,
        LocalDate transferDate,
        AppliedFeeSnapshot fee
    ) {
        TransferSimulateResponse response = new TransferSimulateResponse();
        response.sourceAccount = sourceAccount;
        response.targetAccount = targetAccount;
        response.transferAmount = transferAmount;
        response.transferDate = transferDate;
        response.schedulingDate = LocalDate.now();
        response.feeAmount = fee.getFeeAmount();
        response.feeRate = fee.getFeeRate();
        response.feeFixedAmount = fee.getFeeFixedAmount();
        response.feeDaysRangeFrom = fee.getFeeDaysRangeFrom();
        response.feeDaysRangeTo = fee.getFeeDaysRangeTo();
        return response;
    }
}
