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
    public String[] registerProductsEasy(List<ProductDTO> productDTOs, String uId) {

        log.info("innerProductRegister" + productDTOs);

        List<String> errorMessage = new ArrayList<>();
        List<String> productRegister = new ArrayList<>();
        productRegister.add("상품 등록이 완료되었습니다.");

        // 사용자 정보는 한 번만 가져오면 됩니다.
        UserBy user = userByRepository.findByUId(uId);

        for (ProductDTO productDTO : productDTOs) {
            log.info(" UUUU " + uId);

            Product product = modelMapper.map(productDTO, Product.class);
            product.setUserBy(user);

            // 중복 제품 코드 체크
            if (productRepository.findByProductId(product.getPCode()).isPresent()) {
                errorMessage.add(product.getPCode() + "는 이미 존재하는 코드입니다.");
            }
            // 중복 제품명 체크
            else if (productRepository.findByProductName(product.getPName()).isPresent()) {
                errorMessage.add(product.getPName() + "는 이미 등록된 상품명입니다.");
            }
            // 새로운 제품은 저장
            else {
                productRepository.save(product);
            }
        }

        // 에러 메시지가 있으면 리턴
        if (!errorMessage.isEmpty()) {
            return errorMessage.toArray(new String[0]);
        } else {
            // 모든 제품이 성공적으로 등록되었으면
            return productRegister.toArray(new String[0]);
        }
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
