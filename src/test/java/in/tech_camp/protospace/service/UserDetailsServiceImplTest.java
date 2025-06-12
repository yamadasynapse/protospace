package in.tech_camp.protospace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import in.tech_camp.protospace.entity.UserEntity;
import in.tech_camp.protospace.repository.UserRepository;

@ExtendWith(MockitoExtension.class) // Mockitoを有効化
class UserDetailsServiceImplTest {

    @Mock // UserRepositoryをモック化
    private UserRepository userRepository;

    @InjectMocks // モックをUserDetailsServiceImplに注入
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadUserByUsernameSuccess() {
        // GIVEN: userRepositoryがユーザーを返すようにモックを設定
        UserEntity userEntity = new UserEntity(1, "testuser", "test@example.com", "encoded_password", "p", "a", "pos");
        when(userRepository.findByEmail("test@example.com")).thenReturn(userEntity);

        // WHEN: UserDetailsServiceでユーザーをロード
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // THEN: UserDetailsが正しくロードされ、情報が一致することを確認
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("encoded_password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");

        // CustomUserDetailsの場合、追加フィールドも確認
        assertThat(((CustomUserDetails) userDetails).getUserName()).isEqualTo("testuser");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        // GIVEN: userRepositoryがnullを返すようにモックを設定
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // WHEN & THEN: UsernameNotFoundExceptionがスローされることを確認
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }
}