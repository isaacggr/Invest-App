package com.isaacggr.investapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.isaacggr.investapp.dto.account.AccountResponse;
import com.isaacggr.investapp.dto.account.CreateAccountRequest;
import com.isaacggr.investapp.dto.account.UpdateAccountNameRequest;
import com.isaacggr.investapp.service.AccountService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest req) {
        AccountResponse created = service.create(req);
        return ResponseEntity
                .created(URI.create("/accounts/" + created.id()))
                .body(created);
    }

    // LIST by user
    @GetMapping
    public ResponseEntity<List<AccountResponse>> listByUser(@RequestParam UUID userId) {
        return ResponseEntity.ok(service.listByUser(userId));
    }

    // GET by id
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // UPDATE name
    @PatchMapping("/{id}/name")
    public ResponseEntity<AccountResponse> updateName(
            @PathVariable UUID id,
            @RequestBody UpdateAccountNameRequest req
    ) {
        return ResponseEntity.ok(service.updateName(id, req));
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}