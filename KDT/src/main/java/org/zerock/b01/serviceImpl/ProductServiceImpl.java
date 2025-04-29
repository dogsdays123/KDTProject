package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.ProductDTO;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.ProductService;
import org.zerock.b01.service.UserByService;

import java.util.*;

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
        productRegister.add("상품 등록이 완료되었습니다.");

        for(ProductDTO productDTO : productDTOs){
            Product product = modelMapper.map(productDTO, Product.class);
            product.setUserBy(userByRepository.findByUId(uId));

            //이미 존재한 코드
            if(productRepository.findByProductId(product.getPCode()).isPresent()){
                errorMessage.add(product.getPCode() + "는 이미 존재하는 코드입니다.");
                return errorMessage.toArray(new String[errorMessage.size()]);
            }

            //이미 존재한 제품
            if(productRepository.findByProductName(product.getPName()).isPresent()){
                productRegister.add("이미 등록된 상품입니다.");
                errorMessage.add(product.getPName());
            }
            //존재하지 않은 제품
            else {
                productRepository.save(product);
            }
        }

        if(errorMessage.size() > 0){
            errorMessage.add(" 상품은 이미 등록된 상품입니다.");
            return errorMessage.toArray(new String[errorMessage.size()]);
        } else {
            return productRegister.toArray(new String[productRegister.size()]);
        }
    }

    @Override
    public Map<String, String[]> registerProductsEasy(List<ProductDTO> productDTOs, String uId) {

        log.info("innerProductRegister" + productDTOs);

        List<String> duplicatedCodes = new ArrayList<>();
        List<String> duplicatedNames = new ArrayList<>();

        UserBy user = userByRepository.findByUId(uId);

        for (ProductDTO productDTO : productDTOs) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setUserBy(user);

            String pCode = product.getPCode();
            String pName = product.getPName();

            boolean isDuplicated = false;

            if (productRepository.findByProductId(pCode).isPresent()) {
                isDuplicated = true;
            } else if (productRepository.findByProductName(pName).isPresent()) {
                isDuplicated = true;
            }

            if (isDuplicated) {
                duplicatedCodes.add(pCode);
                duplicatedNames.add(pName);
            } else {
                productRepository.save(product);
            }
        }

        Map<String, String[]> result = new HashMap<>();
        result.put("pCodes", duplicatedCodes.toArray(new String[0]));
        result.put("pNames", duplicatedNames.toArray(new String[0]));

        return result;
    }

    @Override
    public Map<String, String[]> ProductCheck(List<ProductDTO> productDTOs) {

        List<String> duplicatedCodes = new ArrayList<>();
        List<String> duplicatedNames = new ArrayList<>();

        for (ProductDTO productDTO : productDTOs) {
            Product product = modelMapper.map(productDTO, Product.class);

            String pCode = product.getPCode();
            String pName = product.getPName();

            boolean isDuplicated = false;

            if (productRepository.findByProductId(pCode).isPresent()) {
                isDuplicated = true;
            } else if (productRepository.findByProductName(pName).isPresent()) {
                isDuplicated = true;
            }

            if (isDuplicated) {
                duplicatedCodes.add(pCode);
                duplicatedNames.add(pName);
            }
        }

        Map<String, String[]> result = new HashMap<>();
        result.put("pCodes", duplicatedCodes.toArray(new String[0]));
        result.put("pNames", duplicatedNames.toArray(new String[0]));

        return result;
    }

    @Override
    public void modifyProduct(ProductDTO productDTO, String uName){
        Optional<Product> result = productRepository.findByProductId(productDTO.getPCode());
        Product product = result.orElseThrow();
        product.change(productDTO.getPName());
        productRepository.save(product);
    }

    @Override
    public void removeProduct(List<String> pCodes){
        if (pCodes == null || pCodes.isEmpty()) {
            throw new IllegalArgumentException("삭제할 생산 계획 코드가 없습니다.");
        }

        for (String pCode : pCodes) {
            productRepository.deleteById(pCode); // 개별적으로 삭제
        }
    }
}
