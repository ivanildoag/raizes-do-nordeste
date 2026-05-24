-- Migration: Criar tabela de estoque por unidade
-- Regra: cada unidade possui estoque independente por produto
CREATE TABLE estoque (
    id BIGSERIAL PRIMARY KEY,
    unidade_id BIGINT NOT NULL REFERENCES unidades(id),
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    quantidade INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT uk_estoque_unidade_produto UNIQUE (unidade_id, produto_id)
);

CREATE INDEX idx_estoque_unidade ON estoque(unidade_id);
