package com.elyte.domain.response;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.elyte.domain.User;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class GetUserResponse implements Serializable{

    private static final long serialVersionUID = -101879091924046844L;

    private Status status;

    private User user;

}
