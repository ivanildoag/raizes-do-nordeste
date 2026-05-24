-- Migration: Criar tabela de movimentações de estoque (auditoria)
CREATE TABLE movimentacoes_estoque (
    id BIGSERIAL PRIMARY KEY,
    estoque_id BIGINT NOT NULL REFERENCES estoque(id),
    tipo VARCHAR(10) NOT NULL,
    quantidade INTEGER NOT NULL,
    motivo VARCHAR(255),
    usuario_id BIGINT REFERENCES usuarios(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movimentacoes_estoque_id ON movimentacoes_estoque(estoque_id);
