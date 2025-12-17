package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.MaterialCategory;
import com.restaurant.ddd.domain.respository.MaterialCategoryRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.MaterialCategoryJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.MaterialCategoryDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.MaterialCategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MaterialCategoryRepositoryImpl implements MaterialCategoryRepository {

    private final MaterialCategoryJpaRepository jpaRepository;
    private final MaterialCategoryDataAccessMapper mapper;

    @Override
    public MaterialCategory save(MaterialCategory category) {
        MaterialCategoryJpaEntity entity = mapper.toEntity(category);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<MaterialCategory> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<MaterialCategory> findByCode(String code) {
        return jpaRepository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<MaterialCategory> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public List<MaterialCategory> find(int page, int size, String keyword) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1, size);
        if (keyword == null || keyword.isEmpty()) {
            return mapper.toDomainList(jpaRepository.findAll(pageable).getContent());
        }
        return mapper.toDomainList(jpaRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(keyword, keyword, pageable).getContent());
    }

    @Override
    public long count(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return jpaRepository.count();
        }
        // Need a count query or just use the page total request if optimized, 
        // but JpaRepository doesn't natively expose count by name/code without a query.
        // For simplicity, let's trust the Page object in the service, OR adding a count method.
        // Actually, to implement `count(keyword)`, I need a JPA method for it. 
        // Let's rely on the JpaRepository's count method matching the search.
        // For now, I'll temporarily return 0 or implement a proper count method in JPA interface.
        return countByKeyword(keyword);
    }

    private long countByKeyword(String keyword) {
         // This needs a matching count method in JpaRepository
         // Let's assume I will add it in the next step or use findAll to count (inefficient but works for small data)
         // Better: Let's add countByNameContainingIgnoreCaseOrCodeContainingIgnoreCase to JpaRepository
         return jpaRepository.countByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
