package com.raizesdonordeste.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI.
 * 
 * Configura a documentação interativa da API com:
 * - Informações gerais do projeto
 * - Esquema de autenticação JWT Bearer para testar endpoints protegidos
 * - Acesso via: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Raízes do Nordeste API")
                        .description("Sistema backend para rede de lanchonetes Raízes do Nordeste. "
                                + "Suporte multicanal (APP, TOTEM, BALCÃO, PICKUP, WEB) com autenticação JWT, "
                                + "gestão de produtos, estoque, pedidos, pagamento mock e programa de fidelidade.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Raízes do Nordeste")
                                .email("contato@raizesdonordeste.com")))
                // Configuração de segurança JWT no Swagger
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT obtido no endpoint /api/v1/auth/login")));
    }
}
