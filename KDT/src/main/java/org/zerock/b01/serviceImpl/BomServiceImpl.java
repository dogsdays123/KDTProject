package org.zerock.b01.serviceImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductionPlanDTO;
import org.zerock.b01.repository.BomRepository;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.BomService;

import java.util.*;

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
            bomDTO.setPName(bom.getProduct().getPName());
            bomDTO.setMCode(bom.getMaterial().getMCode());
            bomDTO.setMName(bom.getMaterial().getMName());
            bomDTO.setMComponentType(bom.getMaterial().getMComponentType());
            bomDTO.setBRequireNum(bom.getBRequireNum());

            bomDTOList.add(bomDTO);
        }

        return bomDTOList;
    }

    @Override
    public Map<String, String[]> registerBOM(List<BomDTO> bomDTOs, String uId){
        List<String> errorCheck = new ArrayList<>();

        UserBy user = userByRepository.findByUId(uId);

        //돌아라돌아라
        for (BomDTO bomDTO : bomDTOs) {
            Bom bom = modelMapper.map(bomDTO, Bom.class);
            bom.setUserBy(user);

            //만약 엑셀에 들어온 제품이 등록되지 않은 제품이라면 error로 저장
            if (bomRepository.findByProductByPName(bomDTO.getPName()) == null) {
                errorCheck.add(bomDTO.getPName());
                continue;
            } else{
                bom.setProduct(productRepository.findByProductNameObj(bomDTO.getPName()));
            }

            Material m = materialRepository.findByMaterialCode(bomDTO.getMCode()).orElse(null);

            if (m == null) {
                errorCheck.add(bomDTO.getMName());
            } else {
                bom.setMaterial(m);
                bomRepository.save(bom);
            }
        }

        Map<String, String[]> result = new HashMap<>();
        result.put("errorCheck", errorCheck.toArray(new String[0]));

        return result;
    }

    @Override
    public Map<String, String[]> checkBOM(List<BomDTO> bomDTOs){
        List<String> pNames = new ArrayList<>();
        List<String> mCodes = new ArrayList<>();

        //돌아라돌아라
        for (BomDTO bomDTO : bomDTOs) {

            if(bomRepository.findByProductByPName(bomDTO.getPName()) == null){
                pNames.add(bomDTO.getPName());
                continue;
            }

            if(materialRepository.findByMaterialCode(bomDTO.getMCode()).orElse(null) == null){
                mCodes.add(bomDTO.getMCode());
            }
        }

        Map<String, String[]> result = new HashMap<>();
        result.put("pNames", pNames.toArray(new String[0]));
        result.put("mCodes", mCodes.toArray(new String[0]));

        return result;
    }

    @Override
    public void modifyBOM(BomDTO bomDTO, Long bId){
        Optional<Bom> result = bomRepository.findById(bId);
        Bom bom = result.orElseThrow();

        Optional<Material> materialOptional = materialRepository.findById(bomDTO.getMCode());
        Material material = materialOptional.orElseThrow(() -> new EntityNotFoundException("Material not found"));
        bom.change(bomDTO.getBRequireNum(), material);
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
