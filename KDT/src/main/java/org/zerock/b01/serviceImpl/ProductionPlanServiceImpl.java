package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.service.ProductionPlanService;

import java.time.format.DateTimeFormatter;

@Log4j2
@Service
public class ProductionPlanServiceImpl implements ProductionPlanService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Override
    public String registerProductionPlan(ProductionPlanDTO productionPlanDTO) {
        ProductionPlan plan = modelMapper.map(productionPlanDTO, ProductionPlan.class);

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
            plan.setPpName(productionPlanDTO.getPpName());
            plan.setPppCode(productionPlanDTO.getPppCode());
            plan.setPpNum(productionPlanDTO.getPpNum());
            plan.setPpStart(productionPlanDTO.getPpStart());
            plan.setPpEnd(productionPlanDTO.getPpEnd());
        }

        plan.setPpState(CurrentStatus.ON_HOLD);
        productionPlanRepository.save(plan);

        return plan.getPpCode();
    }

    private ProductionPlan toEntity(ProductionPlanDTO dto) {
        if (dto == null) return null;

        ProductionPlan plan = new ProductionPlan();
        plan.setPpId(dto.getPpId());
        plan.setPppCode(dto.getPppCode());
        plan.setPpName(dto.getPpName());
        plan.setPpStart(dto.getPpStart());
        plan.setPpEnd(dto.getPpEnd());
        plan.setPpNum(dto.getPpNum());
        plan.setPpState(CurrentStatus.ON_HOLD);
        return plan;
    }

    private String generateProductionPlanCode(ProductionPlanDTO dto) {
        // 제품명에 따른 접두어 설정
        String prefix;
        switch (dto.getPpName()) {
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
        String dateCode = dto.getPpStart().format(formatter);

        // 동일 접두어 코드의 다음 번호
        Long nextSequence = productionPlanRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }
}
