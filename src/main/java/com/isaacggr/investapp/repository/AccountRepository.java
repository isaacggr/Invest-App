package com.isaacggr.investapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.isaacggr.investapp.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    // Lista somente contas não deletadas (soft delete)
    List<Account> findAllByUser_IdAndDeletedFalse(UUID userId);

    // Para criação: impede duplicar nome por usuário (também pode filtrar deleted=false se quiser liberar reuso)
    boolean existsByUser_IdAndName(UUID userId, String name);

    // Para update: impede duplicar nome ignorando a própria conta
    boolean existsByUser_IdAndNameAndIdNot(UUID userId, String name, UUID id);
}