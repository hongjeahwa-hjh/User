package com.example.user.entity;

import com.example.user.dto.LoginUserResponseDTO;
import com.example.user.dto.SignupUserDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity                     // JPA Repository Object를 통해 데이터를 저장할 오브젝트임을 명시
@Table(name = "users")      // 테이블 지정
@Getter
@Setter
@NoArgsConstructor          // 파라미터가 없는 생성자
@AllArgsConstructor         // 모든 필드를 입력으로 받는 생성자
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "nick_name", nullable = false, length = 100)
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.ROLE_USER;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", updatable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp default current_timestamp on update current_timestamp")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Role {
        ROLE_ADMIN,
        ROLE_USER
    }

    /**
     * SignupUserDTO 객체를 User 엔티티로 변환하는 정적 메서드.
     *
     * <p>회원가입 요청에서 전달된 DTO 데이터를 기반으로
     * User 엔티티 객체를 생성하고 필드를 매핑한다.
     * 변환된 User 객체는 DB 저장 또는 인증 관련 로직에서 사용될 수 있다.</p>
     *
     * @param dto 회원가입 정보를 담고 있는 SignupUserDTO
     * @return dto 정보를 기반으로 생성된 User 엔티티 객체
     */
    public static User fromDTO(SignupUserDTO dto){
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setNickName(dto.getNick_name());

        return user;
    }

    /**
     * User 엔티티를 LoginUserResponseDTO로 변환하는 정적 메서드.
     *
     * <p>로그인된 사용자 정보를 클라이언트에 전달하기 위해
     * User 엔티티의 주요 데이터(id, email, nickName)를
     * LoginUserResponseDTO로 매핑하여 반환한다.</p>
     *
     * @param user 로그인된 사용자 정보를 담고 있는 User 엔티티
     * @return User 엔티티 정보를 기반으로 생성된 LoginUserResponseDTO 객체
     */
    public static LoginUserResponseDTO toLoginUserResponse(User user) {
        LoginUserResponseDTO dto = new LoginUserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNick_name(user.getNickName());
        return dto;
    }
}

