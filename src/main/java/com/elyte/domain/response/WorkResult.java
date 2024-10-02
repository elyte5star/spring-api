package com.elyte.domain.response;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WorkResult implements Serializable{

    private static final long serialVersionUID = 1234567L;

    private String tid;

    private boolean success;

    private String result;
    
}
