package in.tech_camp.protospace.controller;

// import in.tech_camp.protospace.entity.UserEntity; // 不要になる可能性あり
// import in.tech_camp.protospace.repository.UserRepository; // 不要になる可能性あり
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// import java.util.List; // 不要になる可能性あり

@Controller
// @RequiredArgsConstructor // UserRepositoryが不要になるなら、不要になる可能性あり
public class ProtospaceController {

    // private final UserRepository userRepository; // ★削除する

    @GetMapping("/")
    public String index(Model model) {
        // ★以下の2行を削除します
        // List<UserEntity> users = userRepository.findAll();
        // model.addAttribute("users", users);

        return "users/index";
    }
}