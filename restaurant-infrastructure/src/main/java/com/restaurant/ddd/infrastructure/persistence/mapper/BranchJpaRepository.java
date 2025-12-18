package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.infrastructure.persistence.entity.BranchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchJpaRepository extends JpaRepository<BranchJpaEntity, UUID>, JpaSpecificationExecutor<BranchJpaEntity> {
    
    @Query("SELECT b FROM BranchJpaEntity b WHERE b.status = :status")
    List<BranchJpaEntity> findByStatus(@Param("status") DataStatus status);
    
    @Query("SELECT b FROM BranchJpaEntity b WHERE b.code = :code")
    Optional<BranchJpaEntity> findByCode(@Param("code") String code);
    
    @Query("SELECT b FROM BranchJpaEntity b WHERE b.email = :email")
    Optional<BranchJpaEntity> findByEmail(@Param("email") String email);
    
    @Query("SELECT b FROM BranchJpaEntity b WHERE b.phone = :phone")
    Optional<BranchJpaEntity> findByPhone(@Param("phone") String phone);
    
    @Query("SELECT COUNT(b) > 0 FROM BranchJpaEntity b WHERE b.code = :code")
    boolean existsByCode(@Param("code") String code);
    
    @Query("SELECT COUNT(b) > 0 FROM BranchJpaEntity b WHERE b.email = :email")
    boolean existsByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(b) > 0 FROM BranchJpaEntity b WHERE b.phone = :phone")
    boolean existsByPhone(@Param("phone") String phone);
}
