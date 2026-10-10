package com.defense.logistics.resource.repository;

import com.defense.logistics.resource.model.AccessRequest;
import com.defense.logistics.resource.model.UserAccount;
import com.defense.logistics.resource.model.enums.AccessRequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    boolean existsByUserAccountAndStatus(UserAccount userAccount, AccessRequestStatus status);
    List<AccessRequest> findAllByOrderBySubmittedAtDesc();
    List<AccessRequest> findAllByStatusOrderBySubmittedAtDesc(AccessRequestStatus status);
    List<AccessRequest> findAllByUserAccountOrderBySubmittedAtDesc(UserAccount userAccount);
}
