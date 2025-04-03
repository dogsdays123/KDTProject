package org.zerock.b01.serviceImpl.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.test.ProductionPlan;
import org.zerock.b01.dto.test.ProductionPlanDTO;
import org.zerock.b01.repository.test.ProductionPlanRepository;
import org.zerock.b01.service.test.ProductionPlanService;

import java.time.format.DateTimeFormatter;

@Service
public class ProductionPlanServiceImpl implements ProductionPlanService {

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Override
    public String registerProductionPlan(ProductionPlanDTO productionPlanDTO) {
        ProductionPlan plan = toEntity(productionPlanDTO);

        //만약 생산계획 코드가 없는 경우(새로 입력된 것인 경우)
        if (productionPlanDTO.getProductionPlanCode() == null || productionPlanRepository.findByProductionPlanCode(productionPlanDTO.getProductionPlanCode()) == null) {
            // 생산 계획 코드 생성 로직 추가
            String productionPlanCode = generateProductionPlanCode(productionPlanDTO);
            plan.setProductionPlanCode(productionPlanCode);
        } else { //생산 계획 코드가 있는 경우 덮어쓰기한다.
            plan = productionPlanRepository.findByProductionPlanCode(productionPlanDTO.getProductionPlanCode());
            plan.setProductName(productionPlanDTO.getProductName());
            plan.setProductCode(productionPlanDTO.getProductCode());
            plan.setProductionQuantity(productionPlanDTO.getProductionQuantity());
            plan.setProductionStartDate(productionPlanDTO.getProductionStartDate());
            plan.setProductionEndDate(productionPlanDTO.getProductionEndDate());
        }
        productionPlanRepository.save(plan);

        return plan.getProductionPlanCode();
    }

    private ProductionPlan toEntity(ProductionPlanDTO dto) {
        if (dto == null) return null;

        ProductionPlan plan = new ProductionPlan();
        plan.setPlanId(dto.getPlanId());
        plan.setProductCode(dto.getProductCode());
        plan.setProductName(dto.getProductName());
        plan.setProductionStartDate(dto.getProductionStartDate());
        plan.setProductionEndDate(dto.getProductionEndDate());
        plan.setProductionQuantity(dto.getProductionQuantity());
        plan.setStatus(CurrentStatus.ON_HOLD);
        return plan;
    }

    private String generateProductionPlanCode(ProductionPlanDTO dto) {
        // 제품명에 따른 접두어 설정
        String prefix;
        switch (dto.getProductName()) {
            case "전기자전거A":
                prefix = "PDPBA";
                break;
            case "전기자전거B":
                prefix = "PDPBB";
                break;
            case "전동킥보드":
                prefix = "PDPBK";
                break;
            default:
                prefix = "PDPUN";
        }

        // 날짜 포맷 (예: 20231120)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMM");
        String dateCode = dto.getProductionStartDate().format(formatter);

        // 동일 접두어 코드의 다음 번호
        Long nextSequence = productionPlanRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }
}
