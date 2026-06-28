package lk.janith.gymadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ReportSummary {

    private long totalRecords;
    private long totalPayments;
    private BigDecimal totalIncome;

    private long accessGranted;
    private long accessDenied;
}