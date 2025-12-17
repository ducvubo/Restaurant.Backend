package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Unit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Unit
 */
public interface UnitRepository {
    Unit save(Unit unit);
    Optional<Unit> findById(UUID id);
    Optional<Unit> findByCode(String code);
    List<Unit> findAll();
    List<Unit> findByStatus(DataStatus status);
    List<Unit> findBaseUnits(); // Find units where baseUnitId is null
    void deleteById(UUID id);
    boolean existsByCode(String code);
}
