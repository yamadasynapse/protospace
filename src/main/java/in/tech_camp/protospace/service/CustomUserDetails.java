package in.tech_camp.protospace.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import in.tech_camp.protospace.entity.UserEntity;

public class CustomUserDetails extends User {
    private String userName; // ユーザー名 (UserEntityのnameフィールド)

    public CustomUserDetails(UserEntity userEntity, Collection<? extends GrantedAuthority> authorities) {
        // Spring SecurityのUserクラスのコンストラクタには、ユーザー名（認証ID）、ハッシュ化されたパスワード、権限を渡す
        super(userEntity.getEmail(), userEntity.getPassword(), authorities);
        this.userName = userEntity.getName(); // カスタムで追加するユーザー名
    }

    public String getUserName() {
        return userName;
    }
}