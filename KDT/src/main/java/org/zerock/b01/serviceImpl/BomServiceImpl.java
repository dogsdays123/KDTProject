package org.zerock.b01.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Bom;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.ProductionPlan;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.BomRepository;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.BomService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class BomServiceImpl implements BomService {
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private BomRepository bomRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private UserByRepository userByRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<BomDTO> getBoms() {
        List<Bom> bomList = bomRepository.findAll();

        List<BomDTO> bomDTOList = new ArrayList<>();
        for (Bom bom : bomList) {
            BomDTO bomDTO = new BomDTO();
            bomDTO.setPCode(bom.getProduct().getPCode());
            bomDTO.setPName(bom.getProduct().getPName());
            bomDTO.setMCode(bom.getMaterial().getMCode());
            bomDTO.setMName(bom.getMaterial().getMName());
            bomDTO.setBComponentType(bom.getBComponentType());
            bomDTO.setBRequireNum(bom.getBRequireNum());

            bomDTOList.add(bomDTO);
        }

        return bomDTOList;
    }

    @Override
    public void registerBOM(BomDTO bomDTO){
        Bom bom = modelMapper.map(bomDTO, Bom.class);

        Product product = productRepository.findByProductId(bomDTO.getPCode()).orElseThrow(() -> new RuntimeException("Product not found"));
        Material material = materialRepository.findByMaterialCode(bomDTO.getMCode()).orElseThrow(() -> new RuntimeException("Material not found"));


        bom.setProduct(product);
        bom.setMaterial(material);
        bomRepository.save(bom);
    }

    @Override
    public void modifyBOM(BomDTO bomDTO, Long bId){
        Optional<Bom> result = bomRepository.findById(bId);
        Bom bom = result.orElseThrow();

        Optional<Material> materialOptional = materialRepository.findById(bomDTO.getMCode());
        Material material = materialOptional.orElseThrow(() -> new EntityNotFoundException("Material not found"));
        bom.change(bomDTO.getBComponentType(), bomDTO.getBRequireNum(), material);
        bomRepository.save(bom);
    }

    @Override
    public void removeBOM(List<Long> bIds){
        if (bIds == null || bIds.isEmpty()) {
            throw new IllegalArgumentException("삭제할 BOM이 없습니다.");
        }

        for (Long bId : bIds) {
            bomRepository.deleteById(bId);// 개별적으로 삭제
        }
    }
}
