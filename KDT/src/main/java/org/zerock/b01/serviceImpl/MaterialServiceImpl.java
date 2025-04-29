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
        else if(materialRepository.findByMaterialCode(materialDTO.getMCode()) == null){
            log.info("NewHave " + material.getMCode());
        } else {
            log.info("haveOld " + material.getMCode());
        }

        material.setProduct(product);
        log.info("materialDTO = " + materialDTO.getPName());
        materialRepository.save(material);
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

    @Override
    public String[] registerMaterialEasy(List<MaterialDTO> materialDTOS, String uId){
        log.info("innerMaterialRegister" + materialDTOS);

        List<String> errorMessage = new ArrayList<>();
        List<String> materialRegister = new ArrayList<>();
        materialRegister.add("부품 등록이 완료되었습니다.");

        // 사용자 정보는 한 번만 가져오면 됩니다.
        UserBy user = userByRepository.findByUId(uId);

        for (MaterialDTO materialDTO : materialDTOS) {
            log.info(" UUUU " + uId);

            Material material = modelMapper.map(materialDTO, Material.class);
            material.setUserBy(user);

            Optional<Product> optionalProduct = productRepository.findByProductName(materialDTO.getPName());

            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                material.setProduct(product);
            } else {
                errorMessage.add("제품명 '" + materialDTO.getPName() + "'에 해당하는 제품 정보가 존재하지 않습니다.");
                continue;
            }

            // 중복 제품 코드 체크
            if (materialRepository.findByMaterialCode(material.getMCode()).isPresent()) {
                errorMessage.add(material.getMCode() + "는 이미 존재하는 코드입니다.");
            }
            // 중복 제품명 체크
            else if (materialRepository.findByMaterialCode(material.getMName()).isPresent()) {
                errorMessage.add(material.getMName() + "는 이미 등록된 상품명입니다.");
            }
            // 새로운 제품은 저장
            else {
                materialRepository.save(material);
            }
        }

        // 에러 메시지가 있으면 리턴
        if (!errorMessage.isEmpty()) {
            return errorMessage.toArray(new String[0]);
        } else {
            // 모든 제품이 성공적으로 등록되었으면
            return materialRegister.toArray(new String[0]);
        }
    }
}
