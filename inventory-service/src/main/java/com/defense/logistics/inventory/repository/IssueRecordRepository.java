package com.defense.logistics.inventory.repository;
import com.defense.logistics.inventory.model.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {}
