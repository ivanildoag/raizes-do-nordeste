-- Migration: Criar tabelas do programa de fidelidade
CREATE TABLE fidelidade (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuarios(id),
    pontos_acumulados INTEGER NOT NULL DEFAULT 0,
    pontos_resgatados INTEGER NOT NULL DEFAULT 0,
    saldo INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE historico_fidelidade (
    id BIGSERIAL PRIMARY KEY,
    fidelidade_id BIGINT NOT NULL REFERENCES fidelidade(id),
    pedido_id BIGINT REFERENCES pedidos(id),
    pontos INTEGER NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    descricao VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_historico_fidelidade ON historico_fidelidade(fidelidade_id);
