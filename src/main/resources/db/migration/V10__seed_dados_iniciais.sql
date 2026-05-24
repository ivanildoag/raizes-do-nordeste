-- Seed: Dados iniciais para desenvolvimento e testes
-- Senhas: hash BCrypt de 'Senha@123'

-- Usuários iniciais (LGPD: dados fictícios para desenvolvimento)
INSERT INTO usuarios (nome, email, senha_hash, cpf, telefone, perfil, ativo, consentimento_lgpd) VALUES
('Administrador', 'admin@raizesdonordeste.com', '$2a$10$TpPBE/cyUbyo1/2Qqp82hec7fR54fUiU6yV21gWZCmoHOLaVlduv.', '000.000.000-00', '(85) 99999-0001', 'ADMIN', true, true),
('Gerente Fortaleza', 'gerente@raizesdonordeste.com', '$2a$10$TpPBE/cyUbyo1/2Qqp82hec7fR54fUiU6yV21gWZCmoHOLaVlduv.', '111.111.111-11', '(85) 99999-0002', 'GERENTE', true, true),
('Maria Cliente', 'cliente@raizesdonordeste.com', '$2a$10$TpPBE/cyUbyo1/2Qqp82hec7fR54fUiU6yV21gWZCmoHOLaVlduv.', '222.222.222-22', '(85) 99999-0003', 'CLIENTE', true, true);

-- Unidades da rede
INSERT INTO unidades (nome, endereco, cidade, estado, telefone, ativa) VALUES
('Raízes Fortaleza Centro', 'Rua do Comércio, 100, Centro', 'Fortaleza', 'CE', '(85) 3333-0001', true),
('Raízes Recife Boa Viagem', 'Av. Boa Viagem, 2000', 'Recife', 'PE', '(81) 3333-0002', true),
('Raízes Salvador Pelourinho', 'Largo do Pelourinho, 50', 'Salvador', 'BA', '(71) 3333-0003', true);

-- Produtos do cardápio nordestino
INSERT INTO produtos (nome, descricao, categoria, preco, ativo) VALUES
('Acarajé', 'Bolinho de feijão-fradinho frito no azeite de dendê, recheado com vatapá e camarão seco', 'SALGADOS', 15.90, true),
('Tapioca de Carne Seca', 'Tapioca recheada com carne seca desfiada, queijo coalho e manteiga de garrafa', 'TAPIOCAS', 18.50, true),
('Baião de Dois', 'Arroz com feijão-de-corda, queijo coalho, manteiga de garrafa e nata', 'PRATOS', 29.90, true),
('Carne de Sol com Macaxeira', 'Carne de sol acebolada com macaxeira frita e manteiga de garrafa', 'PRATOS', 39.90, true),
('Cuscuz com Ovo', 'Cuscuz de milho com ovo frito, manteiga e queijo coalho', 'PRATOS', 14.90, true),
('Cartola', 'Banana frita com queijo coalho derretido, canela e açúcar', 'SOBREMESAS', 16.90, true),
('Bolo de Rolo', 'Fatia de bolo de rolo pernambucano com goiabada', 'SOBREMESAS', 12.90, true),
('Suco de Cajá', 'Suco natural de cajá (400ml)', 'BEBIDAS', 9.90, true),
('Refrigerante Guaraná Jesus', 'Guaraná Jesus lata 350ml', 'BEBIDAS', 7.90, true),
('Água de Coco', 'Água de coco natural gelada (500ml)', 'BEBIDAS', 8.90, true);

-- Estoque inicial: 50 unidades de cada produto em cada unidade
INSERT INTO estoque (unidade_id, produto_id, quantidade) VALUES
(1, 1, 50), (1, 2, 50), (1, 3, 50), (1, 4, 50), (1, 5, 50),
(1, 6, 50), (1, 7, 50), (1, 8, 50), (1, 9, 50), (1, 10, 50),
(2, 1, 50), (2, 2, 50), (2, 3, 50), (2, 4, 50), (2, 5, 50),
(2, 6, 50), (2, 7, 50), (2, 8, 50), (2, 9, 50), (2, 10, 50),
(3, 1, 50), (3, 2, 50), (3, 3, 50), (3, 4, 50), (3, 5, 50),
(3, 6, 50), (3, 7, 50), (3, 8, 50), (3, 9, 50), (3, 10, 50);

-- Programa de fidelidade para usuários existentes
INSERT INTO fidelidade (usuario_id, pontos_acumulados, pontos_resgatados, saldo) VALUES
(1, 0, 0, 0),
(2, 0, 0, 0),
(3, 0, 0, 0);
