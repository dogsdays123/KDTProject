package org.zerock.b01.service;

import org.zerock.b01.domain.Bom;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductionPlanDTO;

import java.util.List;
import java.util.Map;

public interface BomService {
    List<BomDTO> getBoms();
    Map<String, String[]> registerBOM(List<BomDTO> bomDTOs, String uId);
    Map<String, String[]> checkBOM(List<BomDTO> bomDTOs);
    void modifyBOM(BomDTO bomDTO, Long bId);
    void removeBOM(List<Long> bIds);
}
