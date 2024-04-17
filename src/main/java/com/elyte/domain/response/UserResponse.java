package com.elyte.domain.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; 



@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {
    

    private  String createdAt;

    private String userid;

    private String lastModifiedAt;

    private String username;

    private String email;

    private boolean admin;

    private boolean enabled;

    private boolean accountNonLocked;

    private boolean isUsing2FA;

    private String telephone;

}
