package org.zerock.b01.service;

import org.zerock.b01.dto.MaterialDTO;

public interface MaterialService {
    void registerMaterial(MaterialDTO materialDTO, String uId);
}
