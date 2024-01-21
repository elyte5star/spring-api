package com.elyte.domain.request;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;




@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cart implements Serializable{
    private static final long serialVersionUID = 1234567L;

    private List<CreateProductRequest> itemsList;

    private int itemsQuantity;
    
}
