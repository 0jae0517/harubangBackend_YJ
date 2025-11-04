package com.harubang.harubangBackend.repository;

import com.harubang.harubangBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // 이 인터페이스가 Spring Bean으로 등록될 리포지토리임을 명시 (생략 가능하기도 함)
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<User, Long>
    // -> User 엔티티를 관리하고, User의 기본 키(ID) 타입은 Long이다.

    // Spring Data JPA가 메소드 이름을 보고 쿼리를 자동으로 생성합니다.
    // "email" 필드로 User를 찾는다.
    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email); // 이메일로 사용자를 찾기 위한 메소드 (로그인 시 필요)

    // 이메일 존재 여부 확인 (더 효율적)
    boolean existsByEmail(String email); // 회원가입 시 이메일 중복 체크용
}
