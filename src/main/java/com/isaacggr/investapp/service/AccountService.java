package com.isaacggr.investapp.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.isaacggr.investapp.dto.account.AccountResponse;
import com.isaacggr.investapp.dto.account.CreateAccountRequest;
import com.isaacggr.investapp.dto.account.UpdateAccountNameRequest;
import com.isaacggr.investapp.entity.Account;
import com.isaacggr.investapp.entity.User;
import com.isaacggr.investapp.exception.BusinessRuleException;
import com.isaacggr.investapp.exception.ResourceNotFoundException;
import com.isaacggr.investapp.repository.AccountRepository;
import com.isaacggr.investapp.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    // CREATE
    @Transactional
    public AccountResponse create(CreateAccountRequest req) {
        if (req == null) throw new IllegalArgumentException("Body não pode ser nulo");

        UUID userId = req.userId();
        String name = req.name();

        if (userId == null) throw new IllegalArgumentException("userId é obrigatório");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!user.isActive()) {
            throw new BusinessRuleException("Usuário está inativo");
        }

        String normalizedName = normalizeName(name);

        if (accountRepository.existsByUser_IdAndName(userId, normalizedName)) {
            throw new BusinessRuleException("Já existe uma carteira com esse nome para este usuário");
        }

        Account account = new Account(user, normalizedName);
        accountRepository.save(account);

        return toResponse(account);
    }

    // READ - listar carteiras do usuário (somente não deletadas)
    public List<AccountResponse> listByUser(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId é obrigatório");

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        return accountRepository.findAllByUser_IdAndDeletedFalse(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ - por id (soft delete => 404)
    public AccountResponse getById(UUID accountId) {
        if (accountId == null) throw new IllegalArgumentException("accountId é obrigatório");

        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        if (!acc.isActive()) {
            throw new ResourceNotFoundException("Carteira não encontrada");
        }

        return toResponse(acc);
    }

    // UPDATE name (soft delete => 404)
    @Transactional
    public AccountResponse updateName(UUID accountId, UpdateAccountNameRequest req) {
        if (accountId == null) throw new IllegalArgumentException("accountId é obrigatório");
        if (req == null) throw new IllegalArgumentException("Body não pode ser nulo");

        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        if (!acc.isActive()) {
            throw new ResourceNotFoundException("Carteira não encontrada");
        }

        String newName = normalizeName(req.name());

        UUID userId = acc.getUser().getId();

        // impede duplicar nome em outra conta do mesmo usuário
        if (accountRepository.existsByUser_IdAndNameAndIdNot(userId, newName, acc.getId())) {
            throw new BusinessRuleException("Já existe uma carteira com esse nome para este usuário");
        }

        acc.changeName(newName);
        accountRepository.save(acc);

        return toResponse(acc);
    }

    // DELETE (soft delete => 404 se já deletada)
    @Transactional
    public void delete(UUID accountId) {
        if (accountId == null) throw new IllegalArgumentException("accountId é obrigatório");

        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        if (!acc.isActive()) {
            throw new ResourceNotFoundException("Carteira não encontrada");
        }

        acc.markAsDeleted();
        accountRepository.save(acc);
    }

    // ======================
    // HELPERS
    // ======================
    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome da carteira é obrigatório");
        }
        return name.trim();
    }

    private AccountResponse toResponse(Account acc) {
        return new AccountResponse(
                acc.getId(),
                acc.getUserId(),
                acc.getName(),
                acc.isActive()
        );
    }
}