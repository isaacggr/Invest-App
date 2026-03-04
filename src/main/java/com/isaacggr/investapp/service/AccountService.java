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

    @Transactional
    public AccountResponse create(CreateAccountRequest req) {
        if (req == null) throw new IllegalArgumentException("Body não pode ser nulo");

        UUID userId = req.userId();
        if (userId == null) throw new IllegalArgumentException("userId é obrigatório");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!user.isActive()) {
            throw new BusinessRuleException("Usuário está inativo");
        }

        String normalizedName = normalizeName(req.name());

        if (accountRepository.existsByUser_IdAndName(userId, normalizedName)) {
            throw new BusinessRuleException("Já existe uma carteira com esse nome para este usuário");
        }

        Account account = new Account(user, normalizedName);
        accountRepository.save(account);

        return new AccountResponse(
                account.getId(),
                userId,               
                account.getName(),
                account.isActive()
        );
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<AccountResponse> listByUser(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId é obrigatório");

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        return accountRepository.findAllByUser_IdAndDeletedFalse(userId)
                .stream()
                .map(acc -> toResponse(acc, userId)) 
                .toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public AccountResponse getById(UUID accountId) {
        if (accountId == null) throw new IllegalArgumentException("accountId é obrigatório");

        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        if (!acc.isActive()) {
            throw new ResourceNotFoundException("Carteira não encontrada");
        }

        UUID userId = acc.getUser().getId();

        return toResponse(acc, userId);
    }

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
        UUID userId = acc.getUser().getId(); // ✅ transação aberta

        if (accountRepository.existsByUser_IdAndNameAndIdNot(userId, newName, acc.getId())) {
            throw new BusinessRuleException("Já existe uma carteira com esse nome para este usuário");
        }

        acc.changeName(newName);

        return toResponse(acc, userId);
    }

    @Transactional
    public void delete(UUID accountId) {
        if (accountId == null) throw new IllegalArgumentException("accountId é obrigatório");

        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        if (!acc.isActive()) {
            throw new ResourceNotFoundException("Carteira não encontrada");
        }

        acc.markAsDeleted();
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome da carteira é obrigatório");
        }
        return name.trim();
    }

    private AccountResponse toResponse(Account acc, UUID userId) {
        return new AccountResponse(
                acc.getId(),
                userId,
                acc.getName(),
                acc.isActive()
        );
    }
}