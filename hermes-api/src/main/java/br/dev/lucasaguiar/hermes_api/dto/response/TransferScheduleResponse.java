package br.dev.lucasaguiar.hermes_api.dto.response;

import br.dev.lucasaguiar.hermes_api.domain.AppliedFeeSnapshot;
import br.dev.lucasaguiar.hermes_api.domain.TransferSchedule;
import br.dev.lucasaguiar.hermes_api.domain.TransferStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class TransferScheduleResponse {
    private UUID id;
    private String sourceAccount;
    private String targetAccount;
    private BigDecimal transferAmount;
    private LocalDate transferDate;
    private LocalDate schedulingDate;
    private TransferStatus status;
    private BigDecimal feeAmount;
    private BigDecimal feeRate;
    private BigDecimal feeFixedAmount;
    private Integer feeDaysRangeFrom;
    private Integer feeDaysRangeTo;

    public static TransferScheduleResponse from(TransferSchedule transferSchedule) {
        TransferScheduleResponse response = new TransferScheduleResponse();
        AppliedFeeSnapshot appliedFee = transferSchedule.getAppliedFee();

        response.id = transferSchedule.getId();
        response.sourceAccount = transferSchedule.getSourceAccount().getAccountNumber();
        response.targetAccount = transferSchedule.getTargetAccount().getAccountNumber();
        response.transferAmount = transferSchedule.getTransferAmount();
        response.transferDate = transferSchedule.getTransferDate();
        response.schedulingDate = transferSchedule.getSchedulingDate();
        response.status = transferSchedule.getStatus();
        response.feeAmount = appliedFee.getFeeAmount();
        response.feeRate = appliedFee.getFeeRate();
        response.feeFixedAmount = appliedFee.getFeeFixedAmount();
        response.feeDaysRangeFrom = appliedFee.getFeeDaysRangeFrom();
        response.feeDaysRangeTo = appliedFee.getFeeDaysRangeTo();

        return response;
    }
}
