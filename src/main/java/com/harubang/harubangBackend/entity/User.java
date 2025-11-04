package com.harubang.harubangBackend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제한
@Table(name = "users") // 테이블 이름 명시 (선택 사항)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // 컬럼명 지정
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // 실제로는 암호화해서 저장해야 함

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 저장
    @Column(nullable = false)
    private Role role; // 사용자 역할 (CUSTOMER, AGENT, ADMIN)

    // 중개사일 경우 추가 정보 (선택적)
    private String registrationNumber; // 중개등록번호
    private String officeName; // 상호명
    private String officeAddress; // 사무소 주소

    // 사용자가 작성한 신청서 목록 (User가 Request를 여러 개 가질 수 있음 - OneToMany)
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Request> requests = new ArrayList<>();

    // 중개사가 등록한 매물 목록 (User가 Property를 여러 개 가질 수 있음 - OneToMany)
    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Property> properties = new ArrayList<>();

    // 중개사가 제안한 목록 (User가 Proposal을 여러 개 가질 수 있음 - OneToMany)
    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Proposal> proposals = new ArrayList<>();


    // 생성자 (Builder 패턴 사용)
    @Builder
    public User(String email, String password, String name, String phone, Role role, String registrationNumber, String officeName, String officeAddress) {
        this.email = email;
        this.password = password; // 생성 시점에 암호화 필요
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.registrationNumber = registrationNumber;
        this.officeName = officeName;
        this.officeAddress = officeAddress;
    }

    // 비밀번호 업데이트 메소드 (암호화 로직 포함 필요)
    public void updatePassword(String newPassword) {
        this.password = newPassword; // 암호화 로직 추가
    }

    // 사용자 정보 업데이트 메소드 (필요에 따라 추가)
    public void updateUserInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    // 중개사 정보 업데이트 메소드 (필요에 따라 추가)
    public void updateAgentInfo(String officeName, String officeAddress) {
        if (this.role == Role.AGENT) {
            this.officeName = officeName;
            this.officeAddress = officeAddress;
        }
        // 역할이 AGENT가 아닐 경우 예외 처리 등 추가 가능
    }
}
