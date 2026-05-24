package com.raizesdonordeste.domain.enums;

/**
 * Canais de venda da rede Raízes do Nordeste.
 * Suporte multicanal conforme requisito de multicanalidade:
 * o pedido deve registrar obrigatoriamente o canal de origem.
 */
public enum CanalPedido {
    APP,
    TOTEM,
    BALCAO,
    PICKUP,
    WEB
}
