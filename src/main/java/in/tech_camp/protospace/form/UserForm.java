package in.tech_camp.protospace.form;

import org.springframework.validation.BindingResult;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserForm {

    @NotBlank(message = "ユーザー名は必須です。")
    private String name;

    @NotBlank(message = "メールアドレスは必須です。")
    @Email(message = "メールアドレスの形式が正しくありません。")
    private String email;

    @NotBlank(message = "パスワードは必須です。")
    @Size(min = 6, message = "パスワードは6文字以上で入力してください。")
    private String password;

    @NotBlank(message = "パスワード（確認）は必須です。")
    private String passwordConfirmation;

    @NotBlank(message = "プロフィールは必須です。") // ★追加
    private String profile;

    @NotBlank(message = "所属は必須です。") // ★追加
    private String affiliation;

    @NotBlank(message = "役職は必須です。") // ★追加
    private String position;

    public void validatePasswordConfirmation(BindingResult result) {
        if (!password.equals(passwordConfirmation)) {
            result.rejectValue("passwordConfirmation", "error.user", "パスワードとパスワード（確認）が一致しません。");
        }
    }
}