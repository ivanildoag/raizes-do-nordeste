package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.application.dto.request.*;
import com.raizesdonordeste.application.dto.response.*;
import com.raizesdonordeste.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller de autenticação — endpoints públicos de registro e login */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Registro e login de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @Operation(summary = "Cadastrar novo usuário", description = "Registra um novo cliente no sistema. Requer consentimento LGPD.")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody CriarUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login com JWT", description = "Autentica o usuário e retorna token JWT para acesso aos endpoints protegidos.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
