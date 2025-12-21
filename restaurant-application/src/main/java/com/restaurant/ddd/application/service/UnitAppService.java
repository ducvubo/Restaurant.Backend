package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.unit.*;
import java.util.List;
import java.util.UUID;
/**
 * Application Service interface for Unit
 */
public interface UnitAppService {
    UnitDTO createUnit(CreateUnitRequest request);
    UnitDTO getUnitById(UUID id);
    List<UnitDTO> getAllUnits();
    UnitListResponseNew getList(UnitListRequestNew request);
    UnitDTO updateUnit(UUID id, UpdateUnitRequest request);
    UnitDTO activateUnit(UUID id);
    UnitDTO deactivateUnit(UUID id);
}
