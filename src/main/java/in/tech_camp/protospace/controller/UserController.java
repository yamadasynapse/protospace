package in.tech_camp.protospace.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // ★追加

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.form.UserForm;
import in.tech_camp.protospace.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder; // ★追加: パスワードハッシュ化用
  private final AuthenticationManager authenticationManager; // ★追加: 自動ログイン用

  @GetMapping("/users/sign_up")
  public String showSignUpForm(Model model) {
      model.addAttribute("user", new UserForm());
      return "users/sign_up";
  }

  @PostMapping("/signup")
  public String registerUser(
      @Validated @ModelAttribute("user") UserForm form,
      BindingResult result,
      RedirectAttributes redirectAttributes // ★追加: リダイレクト時にメッセージを渡すため
  ) {
      // メールアドレスの一意性チェック
      if (userRepository.findByEmail(form.getEmail()) != null) {
          result.rejectValue("email", "error.user", "このメールアドレスは既に使用されています。");
      }

      form.validatePasswordConfirmation(result); // パスワードと確認用パスワードの一致チェック

      if (result.hasErrors()) {
          return "users/sign_up"; // エラー時は新規登録ページに留まる
      }

      UserEntity user = new UserEntity();
      user.setName(form.getName());
      user.setEmail(form.getEmail());
      user.setPassword(passwordEncoder.encode(form.getPassword())); // ★パスワードをハッシュ化して保存
      user.setProfile(form.getProfile());
      user.setAffiliation(form.getAffiliation());
      user.setPosition(form.getPosition());

      userRepository.insert(user);

      // ★新規登録後に自動ログインさせる処理
      // 認証トークンを作成 (生のパスワードを使用。AuthenticationManagerがUserDetailsServiceとPasswordEncoderを使い認証する)
      UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword());
      // 認証マネージャーで認証を実行
      Authentication authentication = authenticationManager.authenticate(authToken);
      // SecurityContextに認証情報を設定し、ログイン状態にする
      SecurityContextHolder.getContext().setAuthentication(authentication);

      redirectAttributes.addFlashAttribute("successMessage", "新規登録が完了しました。"); // 成功メッセージ
      return "redirect:/"; // 新規登録成功後、トップページへリダイレクト
  }

  // ログインフォームを表示するためのメソッド
  // Spring Securityが/users/sign_inへのPOSTリクエストを処理するため、このControllerでログイン処理は不要
  @GetMapping("/users/sign_in")
  public String showSignInForm() {
      return "users/sign_in"; // users/sign_in.html を表示
  }

  // その他のユーザー関連のメソッドがあればここに記述
}