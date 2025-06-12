package in.tech_camp.protospace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import in.tech_camp.protospace.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // Spring Securityを有効化
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    // パスワードエンコーダーをBeanとして登録 (BCryptを使用)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring Security 6以降の推奨設定方法: SecurityFilterChainをBeanとして定義
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 認証なしでアクセス許可するパス
                .requestMatchers(
                    "/",               // トップページ
                    "/assets/**",      // CSS/JS/画像などの静的リソース
                    "/images/**",      // 画像ファイル
                    "/users/sign_up",  // 新規登録フォーム表示
                    "/signup",         // 新規登録POST処理
                    "/users/sign_in",  // ログインフォーム表示
                    "/error"           // エラーページ
                ).permitAll() // これらのパスは認証なしでアクセス可能
                // その他の全てのリクエストは認証が必要
                .anyRequest().authenticated()
            )
            // フォーム認証の設定
            .formLogin(login -> login
                .loginPage("/users/sign_in")       // ログインページのURL (GETリクエスト)
                .loginProcessingUrl("/users/sign_in")  // ログインフォームのPOST送信先URL (Spring Securityが処理)
                .defaultSuccessUrl("/", true)    // ログイン成功時のリダイレクト先 (常に"/"へ)
                .failureUrl("/users/sign_in?error") // ログイン失敗時のリダイレクト先
                .usernameParameter("email")      // ログインフォームのユーザー名フィールドのname属性
                .passwordParameter("password")   // ログインフォームのパスワードフィールドのname属性
            )
            // ログアウトの設定
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST")) // POSTリクエストで/logoutをトリガー
                .logoutSuccessUrl("/users/sign_in?logout") // ログアウト成功時のリダイレクト先
                .invalidateHttpSession(true) // HTTPセッションを無効化
                .deleteCookies("JSESSIONID") // クッキーを削除
            )
            // CSRF保護の設定 (新規登録は手動認証のため一時的に無視)
            // 本来は新規登録フォームにもThymeleafでCSRFトークンを埋め込むべき
            .csrf(csrf -> csrf.ignoringRequestMatchers("/signup"));

        return http.build();
    }

    // 認証マネージャーのBean定義
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
}