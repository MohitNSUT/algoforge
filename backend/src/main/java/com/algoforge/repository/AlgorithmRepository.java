package com.algoforge.repository;

import com.algoforge.entity.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, UUID> {
    Optional<Algorithm> findBySlug(String slug);
}
