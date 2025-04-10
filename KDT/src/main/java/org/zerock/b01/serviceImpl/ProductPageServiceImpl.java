package org.zerock.b01.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ProductListAllDTO;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.service.ProductPageService;

import java.time.LocalDate;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ProductPageServiceImpl implements ProductPageService {

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductListAllDTO> listWithAll(PageRequestDTO pageRequestDTO){
        String [] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("pId");
        String pCode = pageRequestDTO.getPCode();
        String pName = pageRequestDTO.getPName();
        LocalDate startDate = pageRequestDTO.getStartDate();
        LocalDate endDate = pageRequestDTO.getStartDate();

        Page<ProductListAllDTO> result = productRepository.searchWithAll(types, keyword, pCode, pName, startDate, endDate, pageable);

        return PageResponseDTO.<ProductListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }
}
