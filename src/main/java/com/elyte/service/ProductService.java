package com.elyte.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.elyte.domain.Product;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.domain.response.ProductResponse;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.repository.ProductRepository;
import com.elyte.utils.UtilityFunctions;
import java.util.Optional;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import com.elyte.domain.request.CreateProductRequest;
import org.springframework.data.domain.Page;

@Service
public class ProductService extends UtilityFunctions {

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<CustomResponseStatus> getAllProducts(Pageable pageable) {
        Page<Product> result = productRepository.findAll(pageable);
        List<ProductResponse> products = new ArrayList<>();
        for (Product product : result.getContent()) {
            products.add(new ProductResponse(product.getPid(), product.getName(),
                    product.getDescription(), product.getCategory(), product.getPrice(), product.getStock_quantity(),
                    product.getReviews(), product.getImage(), product.getDetails()));
        }
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS,
                this.SRC, this.timeNow(),
                Map.of("products", products,
                        "totalElements", result.getTotalElements(),"number",result.getNumber(),
                        "totalPages", result.getTotalPages(), "numberOfElements", result.getNumberOfElements(), "size",
                        result.getSize(), "first", result.isFirst(), "last", result.isLast(),
                        "empty", result.isEmpty()));
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public ResponseEntity<CustomResponseStatus> findProductsByPrice(double price, Pageable pageable) {
        List<Product> productsByprice = productRepository.findAllByPrice(price, pageable);
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS,
                this.SRC, this.timeNow(), productsByprice);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public ResponseEntity<CustomResponseStatus> createOneProduct(CreateProductRequest product)
            throws DataIntegrityViolationException {
        boolean prodExist = productRepository.existsByName(product.getName());
        if (!prodExist) {
            Product newProduct = new Product();
            newProduct.setCategory(product.getCategory());
            newProduct.setDetails(product.getDetails());
            newProduct.setImage(product.getImage());
            newProduct.setName(product.getName());
            newProduct.setPrice(product.getPrice());
            newProduct.setDescription(product.getDescription());
            newProduct.setStock_quantity(product.getStock_quantity());
            newProduct.setProductDiscount("1.0");
            productRepository.save(newProduct);
            HttpHeaders responseHeaders = new HttpHeaders();
            URI newUserUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{pid}")
                    .buildAndExpand(newProduct.getPid()).toUri();
            responseHeaders.setLocation(newUserUri);
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.CREATED.value(),
                    this.I200_MSG,
                    this.SUCCESS,
                    this.SRC, this.timeNow(), newProduct.getPid());
            return new ResponseEntity<>(resp, responseHeaders, HttpStatus.CREATED);
        }

        throw new DataIntegrityViolationException("A PRODUCT WITH THE NAME : " + product.getName() + " EXIST ALREADY");
    }

    public ResponseEntity<CustomResponseStatus> ProductById(String pid) throws ResourceNotFoundException {
        Optional<Product> prod = productRepository.findById(pid);
        if (prod.isEmpty()) {
            throw new ResourceNotFoundException("Product with id :" + pid + " not found!");
        }
        Product product = prod.get();
        ProductResponse productResponse = new ProductResponse(product.getPid(), product.getName(),
                product.getDescription(), product.getCategory(), product.getPrice(), product.getStock_quantity(),
                product.getReviews(), product.getImage(), product.getDetails());
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS,
                this.SRC, this.timeNow(), productResponse);
        return new ResponseEntity<>(resp, HttpStatus.OK);

    }

    public ResponseEntity<CustomResponseStatus> deleteProduct(String pid) throws ResourceNotFoundException {

        Optional<Product> product = productRepository.findById(pid);

        if (product.isPresent()) {

            productRepository.deleteById(pid);
            CustomResponseStatus status = new CustomResponseStatus(HttpStatus.NO_CONTENT.value(),
                    this.I200_MSG,
                    this.SUCCESS,
                    this.SRC, this.timeNow(), null);
            return new ResponseEntity<>(status, HttpStatus.OK);

        }
        throw new ResourceNotFoundException("Product with id :" + pid + " not found!");
    }

    public ResponseEntity<CustomResponseStatus> createMany(List<CreateProductRequest> createProducts)
            throws NullPointerException {

        if (!createProducts.isEmpty()) {

            List<String> productsPids = new ArrayList<>();

            for (CreateProductRequest productRequest : createProducts) {

                boolean prodExist = productRepository.existsByName(productRequest.getName());

                if (prodExist)
                    continue;

                Product newProduct = new Product();
                newProduct.setCategory(productRequest.getCategory());
                newProduct.setDetails(productRequest.getDetails());
                newProduct.setImage(productRequest.getImage());
                newProduct.setName(productRequest.getName());
                newProduct.setPrice(productRequest.getPrice());
                newProduct.setDescription(productRequest.getDescription());
                newProduct.setStock_quantity(productRequest.getStock_quantity());
                productRepository.save(newProduct);
                productsPids.add(newProduct.getPid());

            }

            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                    this.SUCCESS,
                    this.SRC, this.timeNow(), productsPids);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        }

        throw new NullPointerException("EMPTY LIST OF INPUTS");
    }

    public ResponseEntity<CustomResponseStatus> updateProduct(Product product, String pid)
            throws ResourceNotFoundException {
        Optional<Product> productData = productRepository.findById(pid);
        if (productData.isEmpty()) {
            throw new ResourceNotFoundException("Product with id :" + pid + " not found!");
        }
        product = productRepository.save(product);
        CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS,
                this.SRC, this.timeNow(), product);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

}
