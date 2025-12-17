package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.infrastructure.persistence.entity.UserPolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserPolicyJpaRepository extends JpaRepository<UserPolicyJpaEntity, UUID> {
    @Query("SELECT up FROM UserPolicyJpaEntity up WHERE up.userId = :userId")
    List<UserPolicyJpaEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT up.policyId FROM UserPolicyJpaEntity up WHERE up.userId = :userId")
    List<UUID> findPolicyIdsByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserPolicyJpaEntity up WHERE up.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
