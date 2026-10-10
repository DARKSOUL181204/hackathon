package com.defense.logistics.request.repository;
import com.defense.logistics.request.model.RequestStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RequestStatusHistoryRepository extends JpaRepository<RequestStatusHistory, Long> {}
