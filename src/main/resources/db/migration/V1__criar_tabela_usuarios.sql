-- Migration: Criar tabela de usuários
-- LGPD: Esta tabela armazena dados pessoais. O campo senha_hash usa BCrypt.
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    telefone VARCHAR(20),
    perfil VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    consentimento_lgpd BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_perfil ON usuarios(perfil);
