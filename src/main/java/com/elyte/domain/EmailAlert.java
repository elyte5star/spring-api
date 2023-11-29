package com.elyte.domain;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class EmailAlert {
    
    @Email(message = "invalid email address")
    private String recipientEmail;

    @NotBlank(message = "username is required")
    private String recipientUsername;

    @NotBlank(message = "message is required")
    private String mailBody;

    @NotBlank(message = "subject is required")
    private String subject;
    
}
