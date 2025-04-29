package org.zerock.b01.service;

import org.zerock.b01.domain.Bom;
import org.zerock.b01.dto.BomDTO;
import org.zerock.b01.dto.MaterialDTO;
import org.zerock.b01.dto.ProductionPlanDTO;

import java.util.List;

public interface BomService {
    List<BomDTO> getBoms();
    void registerBOM(BomDTO bomDTO);
    void modifyBOM(BomDTO bomDTO, Long bId);
    void removeBOM(List<Long> bIds);
}
