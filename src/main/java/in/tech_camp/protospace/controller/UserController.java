package in.tech_camp.protospace.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("/users/sign_up")
  public String showSignUpForm(Model model) {
      model.addAttribute("user", new UserEntity());
      return "users/sign_up";
  }

  @PostMapping("/signup")
  public String registerUser(@ModelAttribute("user") UserEntity user) {
      System.out.println("★ 登録内容確認:");
      System.out.println(user);
      userRepository.insert(user);  // DBに保存
      return "redirect:/";
  }

}