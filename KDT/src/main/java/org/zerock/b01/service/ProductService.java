package org.zerock.b01.service;

import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    List<Product> getProducts();
    String[] registerProducts(List<ProductDTO> productDTOs, String uName);
    void registerProductsEasy(List<ProductDTO> productDTOs, String uName);
}
