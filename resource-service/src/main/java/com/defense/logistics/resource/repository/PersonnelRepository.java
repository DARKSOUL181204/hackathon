package com.defense.logistics.resource.repository;
import com.defense.logistics.resource.model.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    Optional<Personnel> findByServiceNumber(String serviceNumber);
    Optional<Personnel> findByServiceNumberIgnoreCase(String serviceNumber);
}
