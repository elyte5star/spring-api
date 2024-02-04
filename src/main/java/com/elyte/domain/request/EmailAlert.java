package com.elyte.domain.request;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import com.elyte.domain.enums.EmailType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailAlert  implements Serializable{
    
    @Serial
    private static final long serialVersionUID = 1234567L;

    @Email(message = "invalid email address")
    private String recipientEmail;

    @NotBlank(message = "username is required")
    private String recipientUsername;

    @NotBlank(message = "subject is required")
    private String subject;

    private EmailType emailType;
    
    private Map<String,Object> data;
    
}
