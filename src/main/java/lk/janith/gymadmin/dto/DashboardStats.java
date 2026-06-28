package lk.janith.gymadmin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DashboardStats {

    private long totalMembers;
    private long activeMembers;
    private long expiredMembers;
    private long suspendedMembers;

    private long maleMembers;
    private long femaleMembers;
    private long otherGenderMembers;

    private long totalPlans;
    private long activePlans;

    private long todayPayments;
    private BigDecimal todayIncome;
    private BigDecimal monthlyIncome;

    private long todayCheckIns;
    private long todayAccessGranted;
    private long todayAccessDenied;

    private List<String> last7DaysLabels;
    private List<BigDecimal> last7DaysIncome;
    private List<Long> last7DaysCheckIns;
}