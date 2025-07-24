package com.example.eat_together.domain.user.entity;

import com.example.eat_together.domain.chat.entity.ChatGroup;
import com.example.eat_together.domain.chat.entity.ChatMessage;
import com.example.eat_together.domain.chat.entity.ChatRoomUser;
import com.example.eat_together.domain.user.dto.request.SignupRequestDto;
import com.example.eat_together.domain.user.dto.request.UpdateUserInfoRequestDto;
import com.example.eat_together.global.entity.BaseTimeEntity;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "name")
    private String name;

    @Setter
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ChatGroup> chatGroupList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ChatRoomUser> chatRoomUserList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ChatMessage> chatMessageList = new ArrayList<>();

    public User(SignupRequestDto request, String password) {
        this.loginId = request.getLoginId();
        this.password = password;
        this.email = request.getEmail();
        this.nickname = request.getNickname();
        this.name = request.getName();
    }

    public User(String loginId, String name, String password, String email,String nickname) {
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
    }

    public static User createAuth(String loginId, String name, String password, String email, String nickname) {
        return new User(loginId,name,password,email,nickname);
    }

    public void updateProfile(UpdateUserInfoRequestDto request) {

        // 닉네임이 요청에 포함되어 있다면(null이 아니라면) 업데이트
        if (request.getNickname() != null) {
            this.nickname = request.getNickname();
        }

        // 이메일이 요청에 포함되어 있다면(null이 아니라면) 업데이트
        if (request.getEmail() != null) {
            this.email = request.getEmail();
        }

        // 이름이 요청에 포함되어 있다면(null이 아니라면) 업데이트
        if (request.getName() != null) {
            this.name = request.getName();
        }
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void changeRoleByAdmin() {
        this.role = UserRole.ADMIN;
    }

    public void deleteUser() {
        if (this.role == UserRole.ADMIN) {
            throw new CustomException(ErrorCode.ADMIN_ACCOUNT_CANNOT_BE_DELETED);
        }
        this.isDeleted = true;
    }
}