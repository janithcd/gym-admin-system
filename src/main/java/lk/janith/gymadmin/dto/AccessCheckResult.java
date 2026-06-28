package lk.janith.gymadmin.dto;

import lk.janith.gymadmin.entity.AccessStatus;
import lk.janith.gymadmin.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AccessCheckResult {

    private Member member;
    private String enteredMemberCode;
    private AccessStatus accessStatus;
    private String reason;
    private LocalDateTime accessTime;
}