package com.isaacggr.investapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.isaacggr.investapp.dto.user.CreateUserRequest;
import com.isaacggr.investapp.dto.user.UpdateUserNameRequest;
import com.isaacggr.investapp.dto.user.UserResponse;
import com.isaacggr.investapp.service.UserService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
        UserResponse created = service.create(req);
        return ResponseEntity
                .created(URI.create("/users/" + created.id()))
                .body(created);
    }

    // READ
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // UPDATE (nome)
    @PatchMapping("/{id}/name")
    public ResponseEntity<UserResponse> updateName(
            @PathVariable UUID id,
            @RequestBody UpdateUserNameRequest req
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