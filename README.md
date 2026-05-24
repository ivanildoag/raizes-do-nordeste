# 🌿 Raízes do Nordeste - API Backend

Sistema backend completo para rede de lanchonetes **Raízes do Nordeste**, com suporte multicanal (APP, TOTEM, BALCÃO, PICKUP, WEB).

## 📋 Funcionalidades

- ✅ **Autenticação JWT** com registro e login
- ✅ **Gerenciamento de usuários** com perfis (ADMIN, GERENTE, CLIENTE)
- ✅ **Gerenciamento de unidades** da rede
- ✅ **Cardápio de produtos** com categorias nordestinas
- ✅ **Controle de estoque** por unidade com auditoria de movimentações
- ✅ **Pedidos multicanal** com validação de estoque e transições de status
- ✅ **Pagamento mock** (70% aprovado, 20% recusado, 10% timeout)
- ✅ **Programa de fidelidade** (1 ponto por R$1 gasto)
- ✅ **Documentação Swagger/OpenAPI** interativa
- ✅ **Tratamento padronizado de erros** em JSON
- ✅ **Conformidade LGPD** (CPF mascarado, consentimento obrigatório)
- ✅ **Testes automatizados** (unitários e integração)

## 🛠️ Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 3.3.5 |
| Spring Security | JWT (jjwt) |
| Spring Data JPA | Hibernate |
| PostgreSQL | 16 |
| Flyway | Migrações |
| Docker + Docker Compose | Containerização |
| Swagger/OpenAPI | 3.0 |
| JUnit 5 + Mockito | Testes |
| Testcontainers | Testes de integração |
| Lombok | Redução de boilerplate |

## 🏗️ Arquitetura

O projeto segue **Clean Architecture** com separação clara de camadas:

```
src/main/java/com/raizesdonordeste/
├── domain/           # Entidades, enums, exceções, repositórios (ports)
├── application/      # DTOs, mappers, serviços (casos de uso)
├── infrastructure/   # Segurança, config, gateways (adapters)
└── api/              # Controllers, exception handler (adapters)
```

## 🚀 Como Executar

### Com Docker Compose (recomendado)

```bash
# 1. Copie o arquivo de variáveis de ambiente
cp .env.example .env

# 2. Inicie com Docker Compose
docker-compose up --build
```

A API estará disponível em: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Sem Docker (desenvolvimento local)

Requisitos: Java 21, Maven, PostgreSQL rodando localmente.

```bash
# 1. Configure o PostgreSQL (criar banco 'raizesdonordeste')

# 2. Compile e execute
./mvnw spring-boot:run
```

## 📚 Documentação da API

Acesse o **Swagger UI** em: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Endpoints Principais

| Método | Endpoint | Descrição | Acesso |
|---|---|---|---|
| POST | `/api/v1/auth/registro` | Registrar usuário | Público |
| POST | `/api/v1/auth/login` | Login (JWT) | Público |
| GET | `/api/v1/produtos` | Listar cardápio | Público |
| GET | `/api/v1/unidades` | Listar unidades | Público |
| POST | `/api/v1/pedidos` | Criar pedido | Autenticado |
| PATCH | `/api/v1/pedidos/{id}/status` | Atualizar status | ADMIN/GERENTE |
| POST | `/api/v1/pagamentos/processar` | Processar pagamento | Autenticado |
| GET | `/api/v1/fidelidade/saldo` | Consultar pontos | Autenticado |
| POST | `/api/v1/estoque/entrada` | Entrada de estoque | ADMIN/GERENTE |

### Usuários de Teste

| Email | Senha | Perfil |
|---|---|---|
| admin@raizesdonordeste.com | Admin@123 | ADMIN |
| gerente@raizesdonordeste.com | Admin@123 | GERENTE |
| cliente@raizesdonordeste.com | Admin@123 | CLIENTE |

## 🧪 Testes

```bash
# Executar todos os testes
./mvnw test

# Testes com cobertura
./mvnw verify
```

### Cobertura de Testes

- **AuthService**: Registro, login, validações LGPD, credenciais inválidas
- **PedidoService**: Criação, estoque, transições de status, cancelamento
- **PagamentoService**: Aprovação, recusa, validação de status
- **EstoqueService**: Entrada, saída, estoque insuficiente
- **Pedido Entity**: Máquina de estados de transição de status
- **UsuarioMapper**: Mascaramento de CPF (LGPD)
- **AuthController**: Integração HTTP com MockMvc

## 📊 Modelo de Dados

```
usuarios ─┬── pedidos ─── itens_pedido
           │     │
           │     ├── pagamentos
           │     │
           │     └── historico_fidelidade
           │
           ├── fidelidade
           │
           └── movimentacoes_estoque
                    │
unidades ──┼── estoque ──── produtos
```

## 📝 Licença

Projeto acadêmico - Uso educacional.
