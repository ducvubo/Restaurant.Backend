package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.infrastructure.persistence.entity.UserManagementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaMapper extends JpaRepository<UserManagementJpaEntity, UUID>, JpaSpecificationExecutor<UserManagementJpaEntity> {
    @Query("SELECT u FROM UserManagementJpaEntity u WHERE u.username = :username")
    Optional<UserManagementJpaEntity> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserManagementJpaEntity u WHERE u.email = :email")
    Optional<UserManagementJpaEntity> findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) > 0 FROM UserManagementJpaEntity u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM UserManagementJpaEntity u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
