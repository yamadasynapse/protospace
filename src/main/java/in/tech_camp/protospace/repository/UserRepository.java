package in.tech_camp.protospace.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace.entity.UserEntity; // ★追加: Listを使用するため

@Mapper
public interface UserRepository {

    @Insert("INSERT INTO users (name, email, password, profile, affiliation, position) VALUES (#{name}, #{email}, #{password}, #{profile}, #{affiliation}, #{position})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserEntity user);

    @Select("SELECT id, name, email, password, profile, affiliation, position FROM users WHERE email = #{email}")
    UserEntity findByEmail(String email);

    // ★ここを追加: すべてのユーザーを取得するメソッド
    @Select("SELECT id, name, email, password, profile, affiliation, position FROM users")
    List<UserEntity> findAll();
}