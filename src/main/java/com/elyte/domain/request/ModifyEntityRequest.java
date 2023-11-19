package com.elyte.domain.request;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;




@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class ModifyEntityRequest  implements Serializable{

    private static final long serialVersionUID = 1234567L;

    private String username=null;

    private String password=null;

    private String email=null;

    private String telephone=null;


    
}
