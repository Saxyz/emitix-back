package com.unimag.emitix.repository;

import com.unimag.emitix.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, UUID> {
    Optional<Buyer> findByNit(String nit);
    boolean existsByNit(String nit);
}
