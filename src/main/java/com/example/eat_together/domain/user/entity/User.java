package com.example.eat_together.domain.user.entity;

import com.example.eat_together.global.entity.BaseTimeEntity;
import com.example.eat_together.domain.user.dto.request.SignupRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Setter
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public User(SignupRequestDto request, String password) {
        this.loginId = request.getLoginId();
        this.password = password;
        this.email = request.getEmail();
        this.nickname = request.getNickname();
    }

    public void changePassword(String password){
        this.password = password;
    }
}