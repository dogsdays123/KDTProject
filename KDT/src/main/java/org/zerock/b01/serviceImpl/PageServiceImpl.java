package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.CurrentStatus;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.ProductionPlanRepository;
import org.zerock.b01.service.PageService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PageServiceImpl implements PageService {

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private ProductionPlanRepository productionPlanRepository;

    @Override
    public PageResponseDTO<PlanListAllDTO> planListWithAll(PageRequestDTO pageRequestDTO){
        log.info(">>>> 요청된 페이지 번호: {}", pageRequestDTO.getPage());
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String ppCode = pageRequestDTO.getPpCode();
        String pCode = pageRequestDTO.getPCode();
        String ppNum = pageRequestDTO.getPpNum();
        String pName = pageRequestDTO.getPName();
        String uName = pageRequestDTO.getUName();
        String ppState = pageRequestDTO.getPpState();
        LocalDate ppStart = pageRequestDTO.getPpStart();
        LocalDate ppEnd = pageRequestDTO.getPpEnd();

        Pageable pageable = pageRequestDTO.getPageable("ppId");

        Page<PlanListAllDTO> result = productionPlanRepository
                .planSearchWithAll(types, keyword, ppCode, pCode, ppNum, pName,
                        uName, ppState, ppStart, ppEnd, pageable);

        return PageResponseDTO.<PlanListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<ProductListAllDTO> productListWithAll(PageRequestDTO pageRequestDTO){
        log.info(">>>> 요청된 페이지 번호: {}", pageRequestDTO.getPpStart());
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String pCode = pageRequestDTO.getPCode();
        String pName = pageRequestDTO.getPName();
        String uName = pageRequestDTO.getUName();
        LocalDate regDate = pageRequestDTO.getPReg();

        Pageable pageable = pageRequestDTO.getPageable("pId");

        Page<ProductListAllDTO> result = productRepository.productSearchWithAll(types, keyword, pCode, pName, uName, regDate, pageable);

        return PageResponseDTO.<ProductListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<UserByAllDTO> userByWithAll(PageRequestDTO pageRequestDTO){
        log.info(">>>> user 페이지 번호: {}", pageRequestDTO.getPage());
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String uName = pageRequestDTO.getUName();
        String userJob = pageRequestDTO.getUserJob();
        String userRank = pageRequestDTO.getUserRank();
        LocalDateTime regDate = pageRequestDTO.getURegDate();
        String status = pageRequestDTO.getStatus();
        String uId = pageRequestDTO.getUId();

        Pageable pageable = pageRequestDTO.getPageable("uId");

        Page<UserByAllDTO> result = productRepository.userBySearchWithAll(types, keyword, uName, userJob, userRank, regDate, status, uId, pageable);

        return PageResponseDTO.<UserByAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<SupplierAllDTO> supplierWithAll(PageRequestDTO pageRequestDTO){
        log.info(">>>> supplier 페이지 번호: {}", pageRequestDTO.getPage());
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        String sName = pageRequestDTO.getUName();
        String sRegNum = pageRequestDTO.getUserJob();
        String sBusinessType = pageRequestDTO.getUserRank();
        LocalDateTime sRegDate = pageRequestDTO.getSRegDate();
        String sStatus = "";
        String uId = pageRequestDTO.getUId();
        if(pageRequestDTO.getSStatus() == null){
            sStatus = "대기중";
        } else {
            sStatus = "완료";
        }

        Pageable pageable = pageRequestDTO.getPageable("uId");

        Page<SupplierAllDTO> result = productRepository.supplierSearchWithAll(types, keyword, sName, sRegNum, sBusinessType, sRegDate, sStatus, pageable);

        return PageResponseDTO.<SupplierAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }
}
