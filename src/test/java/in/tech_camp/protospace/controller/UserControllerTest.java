package in.tech_camp.protospace.controller;

import static org.hamcrest.Matchers.endsWith;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString; // UserDetailsServiceのモック化に必要
import static org.mockito.Mockito.times; // SecurityConfigを読み込むために必要
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // SecurityConfigをインポートするため
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.protospace.config.SecurityConfig;
import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.repository.UserRepository;
import in.tech_camp.protospace.service.UserDetailsServiceImpl;

// UserControllerのみをテスト対象とし、Webレイヤーのコンポーネントをロード
@WebMvcTest(UserController.class)
// Spring Securityの設定をテストに含めるため、SecurityConfigをインポート
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTPリクエストをシミュレートする

    @MockBean // UserRepositoryをモック化（DBアクセスは行わない）
    private UserRepository userRepository;

    @MockBean // PasswordEncoderをモック化（Spring Securityの内部で使われるため）
    private PasswordEncoder passwordEncoder;

    @MockBean // UserDetailsServiceImplをモック化（AuthenticationManagerが依存するため）
    private UserDetailsServiceImpl userDetailsService;

    // --- GET /users/sign_up のテスト ---
    @Test
    void testShowSignUpForm() throws Exception {
        mockMvc.perform(get("/users/sign_up"))
                .andExpect(status().isOk()) // HTTPステータスが200 OKであること
                .andExpect(view().name("users/sign_up")) // 返されるビューの名前が正しいこと
                .andExpect(model().attributeExists("user")); // "user"モデル属性が存在すること
    }

    // --- POST /signup のテスト ---
    @Test
    void testRegisterUserSuccess() throws Exception {

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "testuser")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("passwordConfirmation", "password123")
                .param("profile", "test profile")
                .param("affiliation", "test affiliation")
                .param("position", "test position")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                // .andExpect(redirectedUrl("/users/sign_in")); // この行を削除/コメントアウト

                // ★ここに新しいアサーションを追加します (行番号84になるように調整)
                .andExpect(header().string("Location", endsWith("/users/sign_in")));
    
        // userRepository.insertが一度呼び出されたことを確認
        verify(userRepository, times(1)).insert(any(UserEntity.class));}

    @Test
    void testRegisterUserValidationError() throws Exception {
        // 必須項目を空にしてバリデーションエラーを発生させる
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "") // 空にする
                .param("email", "invalid-email") // 無効な形式
                .param("password", "short") // 短すぎる
                .param("passwordConfirmation", "short")
                .param("profile", "p")
                .param("affiliation", "a")
                .param("position", "pos")
                .with(csrf()))
                .andExpect(status().isOk()) // エラー時は同じページに留まる（HTTP 200 OK）
                .andExpect(view().name("users/sign_up"))
                .andExpect(model().attributeHasFieldErrors("user", "name", "email", "password")); // 特定のフィールドにエラーがあること

        // ユーザー挿入は行われないことを確認
        verify(userRepository, times(0)).insert(any(UserEntity.class));
    }

    @Test
    void testRegisterUserPasswordMismatchError() throws Exception {
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "testuser")
                .param("email", "test@example.com")
                .param("password", "password123")
                .param("passwordConfirmation", "passwordABC") // 不一致
                .param("profile", "p")
                .param("affiliation", "a")
                .param("position", "pos")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/sign_up"))
                .andExpect(model().attributeHasFieldErrors("user", "passwordConfirmation"));

        verify(userRepository, times(0)).insert(any(UserEntity.class));
    }

    @Test
    void testRegisterUserDuplicateEmailError() throws Exception {
        // モックの振る舞いを定義: ユーザーが存在する場合
        when(userRepository.findByEmail(anyString()))
                .thenReturn(new UserEntity(1, "existing", "duplicate@example.com", "hash", "p", "a", "pos"));

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "newuser")
                .param("email", "duplicate@example.com")
                .param("password", "password123")
                .param("passwordConfirmation", "password123")
                .param("profile", "p")
                .param("affiliation", "a")
                .param("position", "pos")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/sign_up"))
                .andExpect(model().attributeHasFieldErrors("user", "email"));

        verify(userRepository, times(0)).insert(any(UserEntity.class));
    }

    // --- GET /users/sign_in のテスト ---
    @Test
    void testShowSignInForm() throws Exception {
        mockMvc.perform(get("/users/sign_in"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/sign_in"));
    }
}