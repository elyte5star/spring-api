package com.elyte.domain.request;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;





@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModifyEntityRequest  implements Serializable{

    @Serial
    private static final long serialVersionUID = 1234567L;

    private String email;

    private String telephone;

    private String streetAddress;

    private String city;

    private String state;

    private String zip;

  
}
