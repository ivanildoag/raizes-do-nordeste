-- Seed: Dados iniciais para desenvolvimento e testes
-- Senhas: hash BCrypt de 'Senha@123'

-- Usuários iniciais (LGPD: dados fictícios para desenvolvimento)
INSERT INTO usuarios (nome, email, senha_hash, cpf, telefone, perfil, ativo, consentimento_lgpd) VALUES
('Ivanildo Administrador', 'admin@raizesdonordeste.com', '$2a$10$TpPBE/cyUbyo1/2Qqp82hec7fR54fUiU6yV21gWZCmoHOLaVlduv.', '000.000.000-00', '(85) 99999-0001', 'ADMIN', true, true),
('Paulo Gerente', 'gerente@raizesdonordeste.com', '$2a$10$TpPBE/cyUbyo1/2Qqp82hec7fR54fUiU6yV21gWZCmoHOLaVlduv.', '111.111.111-11', '(85) 99999-0002', 'GERENTE', true, true),
('flavio Cliente', 'flavio@raizesdonordeste.com', '$2a$10$TpPBE/cyUbyo1/2Qqp82hec7fR54fUiU6yV21gWZCmoHOLaVlduv.', '222.222.222-22', '(85) 99999-0003', 'CLIENTE', true, true),
('Monica Cliente', 'monica@raizesdonordeste.com', '$2a$10$TpPBE/cyUbyo1/2Qqp82hec7fR54fUiU6yV21gWZCmoHOLaVlduv.', '333.333.333-33', '(85) 99999-0004', 'CLIENTE', true, true),
('Paula Cliente', 'paula@raizesdonordeste.com', '$2a$10$TpPBE/cyUbyo1/2Qqp82hec7fR54fUiU6yV21gWZCmoHOLaVlduv.', '444.444.444-44', '(85) 99999-0005', 'CLIENTE', true, true),
('Jose Cliente', 'jose@raizesdonordeste.com', '$2a$10$TpPBE/cyUbyo1/2Qqp82hec7fR54fUiU6yV21gWZCmoHOLaVlduv.', '555.555.555-55', '(85) 99999-0006', 'CLIENTE', true, true);

-- Unidades da rede
INSERT INTO unidades (nome, endereco, cidade, estado, telefone, ativa) VALUES
('Raízes Paraíba Centro', 'Rua das Trincheiras, 50, Centro', 'João Pessoa', 'PB', '(83) 3333-0001', true),
('Raízes Brasília Asa Sul', 'CLS 201 Bloco A, Loja 10', 'Brasília', 'DF', '(61) 3333-0002', true),
('Raízes Taguatinga Centro', 'C 1 Bloco B, Loja 5', 'Taguatinga', 'DF', '(61) 3333-0003', true),
('Raízes Ceilândia Centro', 'CNM 1 Bloco C, Loja 2', 'Ceilândia', 'DF', '(61) 3333-0004', true);

-- Produtos do cardápio nordestino
INSERT INTO produtos (nome, descricao, categoria, preco, ativo) VALUES
('Camarão Internacional', 'Delicioso prato de camarão com arroz cremoso e queijo gratinado', 'PRATOS', 69.90, true),
('Moqueca de Peixe', 'Tradicional moqueca de peixe com leite de coco e azeite de dendê', 'PRATOS', 59.90, true),
('Moqueca de Camarão', 'Moqueca de camarão fresco com pimentões e coentro', 'PRATOS', 79.90, true),
('Carne de Sol com Queijo Coalho', 'Porção de carne de sol acebolada servida com cubos de queijo coalho assado', 'PRATOS', 49.90, true),
('Camarão Raizes Brasil', 'Camarão empanado crocante servido com molho especial da casa', 'PRATOS', 65.90, true),
('Espaguete Coco Raizes', 'Espaguete artesanal ao molho branco com toque de leite de coco e frutos do mar', 'PRATOS', 54.90, true);

-- Estoque inicial: 50 unidades de cada produto em cada unidade
INSERT INTO estoque (unidade_id, produto_id, quantidade) VALUES
(1, 1, 50), (1, 2, 50), (1, 3, 50), (1, 4, 50), (1, 5, 50), (1, 6, 50),
(2, 1, 50), (2, 2, 50), (2, 3, 50), (2, 4, 50), (2, 5, 50), (2, 6, 50),
(3, 1, 50), (3, 2, 50), (3, 3, 50), (3, 4, 50), (3, 5, 50), (3, 6, 50),
(4, 1, 50), (4, 2, 50), (4, 3, 50), (4, 4, 50), (4, 5, 50), (4, 6, 50);

-- Programa de fidelidade para usuários existentes
INSERT INTO fidelidade (usuario_id, pontos_acumulados, pontos_resgatados, saldo) VALUES
(1, 0, 0, 0),
(2, 0, 0, 0),
(3, 0, 0, 0),
(4, 0, 0, 0),
(5, 0, 0, 0),
(6, 0, 0, 0);
