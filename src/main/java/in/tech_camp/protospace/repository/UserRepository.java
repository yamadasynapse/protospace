package in.tech_camp.protospace.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace.entity.UserEntity;

@Mapper
public interface UserRepository {

  @Select("SELECT * FROM users")
  List<UserEntity> findAll();

  @Insert("""
    INSERT INTO users (name, email, password, profile, affiliation, position)
    VALUES (#{name}, #{email}, #{password}, #{profile}, #{affiliation}, #{position})
  """)
  void insert(UserEntity user);
}
