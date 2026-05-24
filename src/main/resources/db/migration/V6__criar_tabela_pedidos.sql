-- Migration: Criar tabela de pedidos
-- Multicanalidade: campo canal_pedido é obrigatório
CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES usuarios(id),
    unidade_id BIGINT NOT NULL REFERENCES unidades(id),
    canal_pedido VARCHAR(10) NOT NULL,
    status VARCHAR(25) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    total DECIMAL(10,2) NOT NULL,
    observacao VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_pedidos_cliente ON pedidos(cliente_id);
CREATE INDEX idx_pedidos_unidade ON pedidos(unidade_id);
CREATE INDEX idx_pedidos_canal ON pedidos(canal_pedido);
CREATE INDEX idx_pedidos_status ON pedidos(status);
