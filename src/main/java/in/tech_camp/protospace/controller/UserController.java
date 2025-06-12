package in.tech_camp.protospace.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.form.UserForm;
import in.tech_camp.protospace.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @GetMapping("/users/sign_up")
  public String showSignUpForm(Model model) {
      model.addAttribute("user", new UserForm());
      return "users/sign_up";
  }

  @GetMapping("/users/sign_in")
  public String showSignInForm() {
      return "users/sign_in";
  }

  @PostMapping("/signup")
  public String registerUser(
      @Validated @ModelAttribute("user") UserForm form,
      BindingResult result,
      RedirectAttributes redirectAttributes,
      HttpServletRequest request // ★追加: HttpServletRequestを引数に追加
  ) {
      // メールアドレスの一意性チェック
      if (userRepository.findByEmail(form.getEmail()) != null) {
          result.rejectValue("email", "error.user", "このメールアドレスは既に使用されています。");
      }

      form.validatePasswordConfirmation(result);

      if (result.hasErrors()) {
          return "users/sign_up";
      }

      UserEntity user = new UserEntity();
      user.setName(form.getName());
      user.setEmail(form.getEmail());
      user.setPassword(passwordEncoder.encode(form.getPassword()));
      user.setProfile(form.getProfile());
      user.setAffiliation(form.getAffiliation());
      user.setPosition(form.getPosition());

      userRepository.insert(user);

      // ★★★ 自動ログインの核心部分（修正） ★★★
      // 1. 認証トークンを作成
      UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword());
      
      // 2. AuthenticationManagerで認証を実行
      Authentication authentication = authenticationManager.authenticate(authToken);
      
      // 3. SecurityContextHolderに認証情報を設定
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // 4. ★ここから追加: セッションにSecurityContextを明示的に保存する
      HttpSession session = request.getSession(true);
      session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
      // ★★★ ここまで追加 ★★★

      redirectAttributes.addFlashAttribute("successMessage", "新規登録が完了しました。");
      return "redirect:/";
  }
}