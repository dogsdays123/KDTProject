package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.ProductDTO;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.service.ProductService;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<Product> getProducts() {
        List<Product> products = productRepository.findAll();
        return products;
    }

    @Override
    public String[] registerProducts(List<ProductDTO> productDTOs){

        log.info("innerProductRegister" + productDTOs);

        List<String> productNames = new ArrayList<>();

        List<String> productRegister = new ArrayList<>();
        productRegister.add("제품 등록이 완료되었습니다.");

        for(ProductDTO productDTO : productDTOs){
            Product product = modelMapper.map(productDTO, Product.class);

            //이미 존재한 제품
            if(productRepository.findByProductName(product.getPName()).isPresent()){
                productNames.add(product.getPName());
            }
            //존재하지 않은 제품
            else {
                productRepository.save(product);
            }
        }

        if(productNames.size() > 0){
            productNames.add(" 제품은 이미 등록된 제품입니다.");
            return productNames.toArray(new String[productNames.size()]);
        } else {
            return productRegister.toArray(new String[productRegister.size()]);
        }
    }

    @Override
    public void registerProductsEasy(List<ProductDTO> productDTOs){

        log.info("innerProductRegister" + productDTOs);

        for(ProductDTO productDTO : productDTOs){

            Product product = modelMapper.map(productDTO, Product.class);

            if(productRepository.findByProductName(product.getPName()).isPresent()){

            } else {
                productRepository.save(product);
            }
        }
    }
}
