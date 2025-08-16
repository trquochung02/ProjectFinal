package com.team3.repositories;

import com.team3.entities.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    List<User> findByRole(String role);

    User findByEmail(String email);

    User findByUsername(String username);

    boolean existsByEmail(String email);

}
