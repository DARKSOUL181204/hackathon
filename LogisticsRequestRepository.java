package com.defense.logistics.request.repository;
import com.defense.logistics.request.model.LogisticsRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface LogisticsRequestRepository extends JpaRepository<LogisticsRequest, Long> {
    Optional<LogisticsRequest> findByRequestNumber(String requestNumber);
}
