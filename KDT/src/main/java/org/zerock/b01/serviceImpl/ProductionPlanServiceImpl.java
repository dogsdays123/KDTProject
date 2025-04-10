package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.service.ProductionPlanService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Log4j2
@Service
public class ProductionPlanServiceImpl implements ProductionPlanService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductionPlan findProductionPlan(ProductionPlanDTO productionPlanDTO){
        ProductionPlan productionPlan = modelMapper.map(productionPlanDTO, ProductionPlan.class);
        return productionPlan;
    }

    //생산계획 등록
    @Override
    public String registerProductionPlan(ProductionPlanDTO productionPlanDTO) {

        ProductionPlan plan = modelMapper.map(productionPlanDTO, ProductionPlan.class);
        Product product = productionPlanRepository.findByProduct(productionPlanDTO.getPName());
        log.info("****" + plan.getPpId());

        //만약 생산계획 코드가 없고, 새로 입력된 경우
        if (productionPlanDTO.getPpCode() == null) {
            log.info("NoHaveNew" + plan.getPpCode());
            // 생산 계획 코드 생성 로직 추가
            String productionPlanCode = generateProductionPlanCode(productionPlanDTO);
            plan.set(productionPlanCode);
        }
        //생산계획 코드가 있지만, 새로 입력된 경우
        else if(productionPlanRepository.findByProductionPlanCode(productionPlanDTO.getPpCode()) == null){

            log.info("haveNew" + plan.getPpCode());

        } else { //생산 계획 코드가 있는 경우 덮어쓰기한다.
            log.info("haveOld" + plan.getPpCode());
            plan = productionPlanRepository.findByProductionPlanCode(productionPlanDTO.getPpCode());
            plan.setPName(productionPlanDTO.getPName());
            plan.setPpNum(productionPlanDTO.getPpNum());
            plan.setPpStart(productionPlanDTO.getPpStart());
            plan.setPpEnd(productionPlanDTO.getPpEnd());
        }

        plan.setProduct(product);
        plan.setPpState(CurrentStatus.ON_HOLD);
        log.info("****" + plan.getPpId());
        productionPlanRepository.save(plan);

        return plan.getPpCode();
    }

    private String generateProductionPlanCode(ProductionPlanDTO dto) {
        List<Product> products = productRepository.findByProducts();

        // 제품명에 따른 접두어 설정
        String prefix = "";

        for(Product product : products){
            if(dto.getPName().equals(product.getPName())){
                prefix = product.getPCode();
            } else{
                prefix = "DEFAULT";
            }
        }

        // 날짜 포맷 (예: 20231120)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMM");
        String dateCode = dto.getPpStart().format(formatter);

        // 동일 접두어 코드의 다음 번호
        Long nextSequence = productionPlanRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }
}
