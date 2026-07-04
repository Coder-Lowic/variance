package com.lowic.ai.repository;

import com.lowic.ai.entity.DocumentCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentCacheRepository extends JpaRepository<DocumentCache, String> {
}
