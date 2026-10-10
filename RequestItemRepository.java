package com.defense.logistics.request.repository;
import com.defense.logistics.request.model.RequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {
    List<RequestItem> findByLogisticsRequestId(Long requestId);
}
