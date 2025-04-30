package org.zerock.b01.serviceImpl;

import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.UserBy;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductDTO;
import org.zerock.b01.repository.MaterialRepository;
import org.zerock.b01.repository.ProductRepository;
import org.zerock.b01.repository.UserByRepository;
import org.zerock.b01.service.MaterialService;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.*;


@Log4j2
@Service
public class MaterialServiceImpl implements MaterialService {
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private UserByRepository userByRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Material> getMaterials() {
        return materialRepository.findAll();
    }

    @Override
    public List<Material> getMaterialByPName(String pCode){
        return materialRepository.findByProductCode(pCode);
    }

    @Override
    public List<String> getComponentTypesByProductCode(String pCode) {
        return materialRepository.findComponentTypesByProductCode(pCode);
    }

    @Override
    public List<Material> getMaterialByComponentType(String componentType) {
        return materialRepository.findByComponentType(componentType);
    }

    @Override
    public void registerMaterial(MaterialDTO materialDTO, String uId){
        Material material = modelMapper.map(materialDTO, Material.class);
        Product product = materialRepository.findByProduct(materialDTO.getPName());

        material.setUserBy(userByRepository.findByUId(uId));
        log.info("&&&& " + uId);

        if(materialDTO.getMCode() == null){
            log.info("NewNoHave " + material.getMCode());
            String productionPlanCode = generateProductionPlanCode(materialDTO);
            material.set(productionPlanCode);
        }
        else if(materialRepository.findByMaterialCode(materialDTO.getMCode()).isEmpty()){
            log.info("NewHave " + material.getMCode());
        } else {
            log.info("haveOld " + material.getMCode());
        }

        material.setProduct(product);
        log.info("materialDTO = " + materialDTO.getPName());
        materialRepository.save(material);
    }

    @Override
    public Map<String, String[]> registerMaterialEasy(List<MaterialDTO> materialDTOs, String uId){
        List<String> duplicatedCodes = new ArrayList<>();
        List<String> errorCheck = new ArrayList<>();

        UserBy user = userByRepository.findByUId(uId);

        //돌아라돌아라
        for (MaterialDTO materialDTO : materialDTOs) {
            //만약 엑셀에 들어온 제품이 등록되지 않은 제품이라면 error로 저장
            if(productRepository.findByProductNameObj(materialDTO.getPName()) == null){
                errorCheck.add(materialDTO.getPName());
                continue;
            }
            //그게 아니면 정상영업합니다.
            Material material = modelMapper.map(materialDTO, Material.class);
            material.setProduct(productRepository.findByProductNameObj(materialDTO.getPName()));
            material.setUserBy(userByRepository.findByUId(uId));

            String mCode = material.getMCode();

            boolean isDuplicated = false;

            if (materialRepository.findByMaterialCode(mCode).isPresent()) {
                isDuplicated = true;
            }

            if (isDuplicated) {
                duplicatedCodes.add(mCode);
            } else {
                materialRepository.save(material);
            }
        }

        Map<String, String[]> result = new HashMap<>();
        result.put("errorCheck", errorCheck.toArray(new String[0]));
        result.put("mCodes", duplicatedCodes.toArray(new String[0]));

        return result;
    }

    @Override
    public Map<String, String[]> materialCheck(List<MaterialDTO> materialDTOs) {
        List<String> duplicatedCodes = new ArrayList<>();
        List<String> errorCheck = new ArrayList<>();

        //돌아라돌아라
        for (MaterialDTO materialDTO : materialDTOs) {
            //만약 엑셀에 들어온 제품이 등록되지 않은 제품이라면 error로 저장
            if(productRepository.findByProductNameObj(materialDTO.getPName()) == null){
                errorCheck.add(materialDTO.getPName());
                continue;
            }
            //그게 아니면 정상영업합니다.
            Material material = modelMapper.map(materialDTO, Material.class);
            material.setProduct(productRepository.findByProductNameObj(materialDTO.getPName()));
            String mCode = material.getMCode();

            boolean isDuplicated = false;

            if (materialRepository.findByMaterialCode(mCode).isPresent()) {
                isDuplicated = true;
            }

            if (isDuplicated) {
                duplicatedCodes.add(mCode);
            }
        }

        Map<String, String[]> result = new HashMap<>();
        result.put("errorCheck", errorCheck.toArray(new String[0]));
        result.put("mCodes", duplicatedCodes.toArray(new String[0]));

        return result;
    }

    public String generateProductionPlanCode(MaterialDTO dto) {
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
        String dateCode = dto.getRegDate().format(formatter);

        // 동일 접두어 코드의 다음 번호
        Long nextSequence = materialRepository.countByPrefix(prefix) + 1;

        // 코드 생성
        return String.format("%s%s%03d", prefix, dateCode, nextSequence);
    }

    @Override
    public void modifyMaterial(MaterialDTO materialDTO, String uName){
        Optional<Material> result = materialRepository.findByMaterialCode(materialDTO.getMCode());
        Material material = result.orElseThrow();
        material.change(materialDTO.getMComponentType(), materialDTO.getMType(), materialDTO.getMName(), materialDTO.getMMinNum(), materialDTO.getMUnitPrice(),
                materialDTO.getMDepth(), materialDTO.getMHeight(), materialDTO.getMWidth(),
                materialDTO.getMWeight(), materialDTO.getMLeadTime());

        materialRepository.save(material);
    }

    @Override
    public void removeMaterial(List<String> pCodes){
        if (pCodes == null || pCodes.isEmpty()) {
            throw new IllegalArgumentException("삭제할 생산 계획 코드가 없습니다.");
        }

        for (String pCode : pCodes) {
            materialRepository.deleteById(pCode); // 개별적으로 삭제
        }
    }

}
