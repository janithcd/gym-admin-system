package lk.janith.gymadmin.dto;

import lk.janith.gymadmin.entity.AdminRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserDTO {

    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private AdminRole role;
    private boolean enabled;
}