package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.ProductDTO;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.ProductService;
import org.zerock.b01.service.UserByService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private UserByService userByService;
    @Autowired
    private UserByRepository userByRepository;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public String[] registerProducts(List<ProductDTO> productDTOs, String uId){

        List<String> errorMessage = new ArrayList<>();

        List<String> productRegister = new ArrayList<>();
        productRegister.add("제품 등록이 완료되었습니다.");

        for(ProductDTO productDTO : productDTOs){
            Product product = modelMapper.map(productDTO, Product.class);
            product.setUserBy(userByRepository.findByUId(uId));

            //이미 존재한 코드
            if(productRepository.findByProductId(product.getPCode()).isPresent()){
                errorMessage.add(product.getPCode() + "이미 존재하는 코드입니다.");
                return errorMessage.toArray(new String[errorMessage.size()]);
            }

            //이미 존재한 제품
            if(productRepository.findByProductName(product.getPName()).isPresent()){
                errorMessage.add(product.getPName());
            }
            //존재하지 않은 제품
            else {
                productRepository.save(product);
            }
        }

        if(errorMessage.size() > 0){
            errorMessage.add(" 제품은 이미 등록된 제품입니다.");
            return errorMessage.toArray(new String[errorMessage.size()]);
        } else {
            return productRegister.toArray(new String[productRegister.size()]);
        }
    }

    @Override
    public void registerProductsEasy(List<ProductDTO> productDTOs, String uId){

        log.info("innerProductRegister" + productDTOs);

        for(ProductDTO productDTO : productDTOs){

            log.info(" UUUU " + uId);

            Product product = modelMapper.map(productDTO, Product.class);
            product.setUserBy(userByRepository.findByUId(uId));

            log.info(" UUUU " + userByRepository.findByUId(productDTO.getUName()));

            if(productRepository.findByProductName(product.getPName()).isPresent()){

            } else {
                productRepository.save(product);
            }
        }
    }
}
