package org.zerock.b01.service;

import org.zerock.b01.domain.Material;
import org.zerock.b01.domain.Product;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductDTO;

import java.util.List;

public interface MaterialService {
    List<Material> getMaterials();
    void registerMaterial(MaterialDTO materialDTO, String uId);
    void modifyMaterial(MaterialDTO materialDTO, String uName);
    void removeMaterial(List<String> mCodes);
    String[] registerMaterialEasy(List<MaterialDTO> materialDTOS, String uId);
}
