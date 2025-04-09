package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Product;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.service.ProductService;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<Product> getProducts() {
        List<Product> products = productRepository.findAll();
        return products;
    }
}
