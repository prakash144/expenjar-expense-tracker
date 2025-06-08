package com.example.user_service.repository;

import com.example.user_service.entities.UserInfo;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface UserRepository extends CrudRepository<UserInfo, String>  {

    Optional<UserInfo> findByUserId(String userId);
}
