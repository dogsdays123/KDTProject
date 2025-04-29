package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.allDTO.PlanListAllDTO;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.ProductionPlanService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ProductionPlanServiceImpl implements ProductionPlanService {

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserByRepository userByRepository;

    @Override
    public ProductionPlan findProductionPlan(ProductionPlanDTO productionPlanDTO){
        ProductionPlan productionPlan = modelMapper.map(productionPlanDTO, ProductionPlan.class);
        return productionPlan;
    }

    //생산계획 등록
    @Override
    public String registerProductionPlan(ProductionPlanDTO productionPlanDTO, String uId) {

        ProductionPlan plan = modelMapper.map(productionPlanDTO, ProductionPlan.class);
        Product product = productionPlanRepository.findByProduct(productionPlanDTO.getPName());

        if(product == null){
            product = new Product();
            product.setPName(productionPlanDTO.getPName());
            product.setPCode(productionPlanDTO.getPppCode());
            product.setUserBy(userByRepository.findByUId(uId));
            productRepository.save(product);
        }

        plan.setUserBy(userByRepository.findByUId(uId));
        log.info("&&&& " + uId);

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
            plan.setPName(productionPlanDTO.getPName());
            plan.setPpNum(productionPlanDTO.getPpNum());
            plan.setPpStart(productionPlanDTO.getPpStart());
            plan.setPpEnd(productionPlanDTO.getPpEnd());
        }

        plan.setProduct(product);
        plan.setPpState(CurrentStatus.ON_HOLD);
        log.info("****" + plan.getPpCode());
        productionPlanRepository.save(plan);

        return plan.getPpCode();
    }

    public List<ProductionPlan> getPlans(){
        return productionPlanRepository.findByPlans();
    }

    public String generateProductionPlanCode(ProductionPlanDTO dto) {
        List<Product> products = productRepository.findByProducts();

        // 제품명에 따른 접두어 설정
        String prefix = "";

        for(Product product : products){
            if(dto.getPName().equals(product.getPName())){
                prefix = product.getPName();
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

    @Override
    public void modifyProductionPlan(ProductionPlanDTO productionPlanDTO, String uName){
        Optional<ProductionPlan> result = productionPlanRepository.findByProductionPlanCode(productionPlanDTO.getPpCode());
        ProductionPlan productionPlan = result.orElseThrow();
        productionPlan.change(productionPlanDTO.getPpNum(), productionPlanDTO.getPpStart(), productionPlanDTO.getPpEnd());
        productionPlanRepository.save(productionPlan);
    }

    @Override
    public void removeProductionPlan(List<String> ppCodes){
        if (ppCodes == null || ppCodes.isEmpty()) {
            throw new IllegalArgumentException("삭제할 생산 계획 코드가 없습니다.");
        }

        for (String ppCode : ppCodes) {
            productionPlanRepository.deleteById(ppCode); // 개별적으로 삭제
        }
    }

    @Override
    public PageResponseDTO<ProductionPlanDTO> list(PageRequestDTO pageRequestDTO){
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        LocalDate ppStart = pageRequestDTO.getPpStart();
        LocalDate ppEnd = pageRequestDTO.getPpEnd();
        String ppCode = pageRequestDTO.getPpCode();
        String pName = pageRequestDTO.getPName();
        String ppState = pageRequestDTO.getPpState();
        Pageable pageable = pageRequestDTO.getPageable();

        Page<PlanListAllDTO> result = productionPlanRepository.planSearchWithAll(types, keyword, ppCode, pName, ppState, ppStart, ppEnd, pageable);

        List<ProductionPlanDTO> dtoList = result.getContent().stream().map(productionPlan ->modelMapper.map(productionPlan, ProductionPlanDTO.class)).collect(Collectors.toList());

        return PageResponseDTO.<ProductionPlanDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
}
