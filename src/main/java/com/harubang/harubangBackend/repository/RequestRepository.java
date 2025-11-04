package com.harubang.harubangBackend.repository;

import com.harubang.harubangBackend.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    // JpaRepository<Request, Long>
    // -> Request 엔티티를 관리하고, Request의 ID 타입은 Long이다.

    // 특정 고객(Customer)의 ID(PK)로 모든 신청서를 찾는다. (마이페이지 신청 내역)
    List<Request> findByCustomerId(Long customerId);
}
