package com.unimag.emitix.repository;

import com.unimag.emitix.entity.ActivityLog;
import com.unimag.emitix.entity.enums.EntityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    // entity es un enum → comparación exacta; username/action siguen con LIKE
    @Query("SELECT a FROM ActivityLog a WHERE " +
           "(CAST(:username AS string) IS NULL OR LOWER(a.username) LIKE CAST(:username AS string)) AND " +
           "(CAST(:action AS string) IS NULL OR LOWER(a.action) LIKE CAST(:action AS string)) AND " +
           "(CAST(:entity AS string) IS NULL OR a.entity = :entity) AND " +
           "(CAST(:from AS LocalDateTime) IS NULL OR a.createdAt >= :from) AND " +
           "(CAST(:to AS LocalDateTime) IS NULL OR a.createdAt <= :to) " +
           "ORDER BY a.createdAt DESC")
    Page<ActivityLog> findByFilters(
            @Param("username") String username,
            @Param("action") String action,
            @Param("entity") EntityType entity,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}
