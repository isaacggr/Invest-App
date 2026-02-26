package com.isaacggr.investapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isaacggr.investapp.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);
}