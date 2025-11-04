package com.harubang.harubangBackend.repository;

import com.harubang.harubangBackend.entity.Property;
import com.harubang.harubangBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    // JpaRepository<Property, Long>
    // -> Property 엔티티를 관리하고, Property의 ID 타입은 Long이다.

    // 특정 공인중개사(User 객체)가 등록한 모든 매물을 찾는다.
    List<Property> findByAgent(User agent);

    // 또는, 특정 공인중개사의 ID(PK)로 모든 매물을 찾는다. (이 방식이 더 유연할 수 있음)
    List<Property> findByAgentId(Long agentId);
}
