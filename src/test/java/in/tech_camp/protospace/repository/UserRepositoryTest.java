package in.tech_camp.protospace.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest; // 重複キーエラー用
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;

import in.tech_camp.protospace.entity.UserEntity; 
@MybatisTest // MyBatisのテストを有効化
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 実際のDBではなく、テスト用DBを使用
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // 各テストの前に実行される。テスト用のユーザーを挿入するなど、共通の前処理
    @BeforeEach
    void setUp() {
        // テストごとにDBをクリーンアップしたい場合（H2はインメモリなので基本的に不要だが、念のため）
        // または、各テストメソッドでデータを挿入する
    }

    @Test
    void testInsertAndFindByEmail() {
        // GIVEN: テストデータ
        UserEntity user = new UserEntity(null, "testuser", "test@example.com", "password_hash", "profile", "affiliation", "position");

        // WHEN: ユーザーを挿入
        userRepository.insert(user);

        // THEN: IDが割り当てられていることを確認
        assertNotNull(user.getId());

        // WHEN: メールアドレスでユーザーを検索
        UserEntity foundUser = userRepository.findByEmail("test@example.com");

        // THEN: ユーザーが見つかり、データが一致することを確認
        assertNotNull(foundUser);
        assertThat(foundUser.getName()).isEqualTo("testuser");
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
        // パスワードはハッシュ化されるので、ここでそのまま比較はしない。
        // リポジトリテストでは、パスワードが格納されていることのみを確認。
    }

    @Test
    void testFindByEmailNotFound() {
        // WHEN: 存在しないメールアドレスで検索
        UserEntity foundUser = userRepository.findByEmail("nonexistent@example.com");

        // THEN: nullが返されることを確認
        assertThat(foundUser).isNull();
    }

    @Test
    void testInsertDuplicateEmail() {
        // GIVEN: 最初のユーザーを挿入
        UserEntity user1 = new UserEntity(null, "user1", "duplicate@example.com", "hash1", "p1", "a1", "pos1");
        userRepository.insert(user1);

        // GIVEN: 同じメールアドレスで別のユーザーを作成
        UserEntity user2 = new UserEntity(null, "user2", "duplicate@example.com", "hash2", "p2", "a2", "pos2");

        // WHEN & THEN: 重複メールアドレスの挿入がDuplicateKeyExceptionをスローすることを確認
        assertThrows(DuplicateKeyException.class, () -> {
            userRepository.insert(user2);
        });
    }

    @Test
    void testFindAll() {
        // GIVEN: 複数のユーザーを挿入
        userRepository.insert(new UserEntity(null, "userA", "a@example.com", "hashA", "pA", "aA", "posA"));
        userRepository.insert(new UserEntity(null, "userB", "b@example.com", "hashB", "pB", "aB", "posB"));

        // WHEN: 全てのユーザーを検索
        List<UserEntity> users = userRepository.findAll();

        // THEN: 挿入したユーザー数が含まれていることを確認（テスト環境全体で考える必要がある）
        // H2インメモリDBなので、このテストケース実行時に挿入されたものだけをカウントできる
        assertThat(users).hasSize(2); // または、特定の名前のユーザーが存在することを確認
        assertThat(users).extracting(UserEntity::getEmail)
                         .containsExactlyInAnyOrder("a@example.com", "b@example.com");
    }
}