package com.raizesdonordeste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Raízes do Nordeste.
 * 
 * Sistema backend para rede de lanchonetes com suporte multicanal
 * (APP, TOTEM, BALCÃO, PICKUP, WEB).
 * 
 * Funcionalidades principais:
 * - Autenticação e autorização com JWT
 * - Gerenciamento de unidades e produtos
 * - Controle de estoque por unidade
 * - Criação e gerenciamento de pedidos multicanal
 * - Integração mock de pagamento
 * - Programa de fidelidade
 */
@SpringBootApplication
public class RaizesDoNordesteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaizesDoNordesteApplication.class, args);
    }
}
