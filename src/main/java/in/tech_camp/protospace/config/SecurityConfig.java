// src/main/java/in/tech_camp/protospace/config/SecurityConfig.java (またはあなたのプロジェクトの適切なパッケージ名)
package in.tech_camp.protospace.config; // ★このパッケージ名が、実際のファイルの場所と一致しているか確認

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain; // このインポートも忘れずに
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // このインポートも忘れずに

import in.tech_camp.protospace.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    // パスワードエンコーダーをBeanとして登録 (BCryptを使用)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // ★BCryptPasswordEncoderが返されていること
    }

    // 認証マネージャーのBean定義
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService); // ★UserDetailsServiceが正しく設定されていること
        authenticationProvider.setPasswordEncoder(passwordEncoder); // ★上で定義したPasswordEncoderが使われていること
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/assets/**",
                    "/images/**",
                    "/users/sign_up",
                    "/signup",
                    "/users/sign_in",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/users/sign_in")
                .loginProcessingUrl("/users/sign_in")
                .defaultSuccessUrl("/", true)
                .failureUrl("/users/sign_in?error")
                .usernameParameter("email")
                .passwordParameter("password")
            )
           .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                // .logoutSuccessUrl("/", true) // 修正前
                .logoutSuccessUrl("/") // ★修正後: `true` を削除
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/signup"));

        return http.build();
    }
}