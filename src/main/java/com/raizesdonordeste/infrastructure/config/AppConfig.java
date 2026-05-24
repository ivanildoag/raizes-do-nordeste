package com.raizesdonordeste.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuração geral da aplicação Raízes do Nordeste.
 *
 * Esta classe centraliza beans de infraestrutura que são compartilhados
 * por toda a aplicação, como o ObjectMapper para serialização JSON.
 *
 * Ao definir o ObjectMapper como @Primary, garantimos que todas as partes
 * da aplicação (controllers, serviços, filtros) usem a mesma configuração
 * de serialização, evitando inconsistências no formato JSON.
 */
@Configuration
public class AppConfig {

    /**
     * Bean do ObjectMapper configurado para toda a aplicação.
     *
     * Configurações aplicadas:
     * - JavaTimeModule: suporte a tipos do java.time (LocalDate, LocalDateTime, etc.)
     * - Datas como texto ISO-8601 (não como timestamps numéricos)
     * - Propriedades nulas não são incluídas no JSON de saída
     * - Propriedades desconhecidas no JSON de entrada são ignoradas (tolerância)
     *
     * A anotação @Primary garante que este bean seja preferido quando houver
     * múltiplos ObjectMapper no contexto (ex: o padrão do Spring Boot).
     *
     * @return ObjectMapper configurado e pronto para uso
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registra o módulo para suporte a tipos de data/hora do Java 8+
        // Sem este módulo, LocalDate, LocalDateTime, etc. causariam erros de serialização
        mapper.registerModule(new JavaTimeModule());

        // Serializa datas como strings ISO-8601 (ex: "2026-05-21T21:00:00")
        // em vez de arrays numéricos (ex: [2026, 5, 21, 21, 0, 0])
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Não inclui propriedades com valor null no JSON de saída
        // Reduz o tamanho das respostas e evita informação desnecessária
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Ignora propriedades desconhecidas no JSON de entrada
        // Permite evolução da API sem quebrar clientes antigos
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }
}
