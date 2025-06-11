package in.tech_camp.protospace.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    private Integer id;
    private String name;
    private String email;
    private String password; // ★ハッシュ化されたパスワードが格納される
    private String profile; // ★追加
    private String affiliation; // ★追加
    private String position; // ★追加
}