package in.tech_camp.protospace.entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEntity {
  private Integer id;
  private String name;
  private String email;
  private String password;
  private String profile;
  private String affiliation;
  private String position;

  // DBに保存しない確認用フィールド
  private transient String passwordConfirmation;
}