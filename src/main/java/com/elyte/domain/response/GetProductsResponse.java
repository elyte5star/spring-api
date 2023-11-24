package com.elyte.domain.response;
import com.elyte.domain.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@AllArgsConstructor(staticName = "build")
public class GetProductsResponse {

    private Status status;

    private Iterable<Product> products;
    
}
