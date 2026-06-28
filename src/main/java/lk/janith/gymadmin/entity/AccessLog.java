package lk.janith.gymadmin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "access_logs")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nullable because sometimes an unknown member code may be entered
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "entered_member_code")
    private String enteredMemberCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_status", nullable = false)
    private AccessStatus accessStatus;

    @Column(nullable = false)
    private String reason;

    @Column(name = "access_time")
    private LocalDateTime accessTime;

    @PrePersist
    public void beforeSave() {
        if (accessTime == null) {
            accessTime = LocalDateTime.now();
        }
    }
}