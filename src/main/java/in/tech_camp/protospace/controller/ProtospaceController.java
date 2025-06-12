package in.tech_camp.protospace.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.repository.UserRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class ProtospaceController {

  private final UserRepository userRepository;

  @GetMapping("/")
  public String showIndex(Model model) {
    List<UserEntity> users = userRepository.findAll();
    model.addAttribute("users", users);
    return "users/index"; 
  }
}
