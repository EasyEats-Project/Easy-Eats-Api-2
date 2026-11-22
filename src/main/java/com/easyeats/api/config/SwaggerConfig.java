package com.easyeats.api.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class SwaggerConfig {

    private static final String DESCRICAO = """
            API do **Easy Eats**, sistema de gestão de restaurante: cardápio, estoque, comandas, caixa e vendas.

            **Como usar**
            1. Em **Autenticação**, execute `POST /auth/login` com usuário e senha e copie o token devolvido.
            2. Clique em **Authorize**, no topo da página, cole o token e confirme.
            3. As demais rotas passam a enviar o token automaticamente.

            **Fluxo do atendimento**
            abrir o caixa, lançar a comanda de uma mesa (a venda da mesa abre sozinha), enviar para preparo,
            registrar os pagamentos e finalizar a venda, que libera a mesa e lança o dinheiro no caixa.

            **Erros**
            as respostas de erro trazem o campo `erro` com a mensagem.
            """;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Easy Eats")
                        .version("v1.0.0")
                        .description(DESCRICAO))
                // A ordem das seções na tela segue o fluxo do sistema.
                .tags(List.of(
                        new Tag().name("Autenticação"),
                        new Tag().name("Mesas"),
                        new Tag().name("Categorias"),
                        new Tag().name("Produtos"),
                        new Tag().name("Ingredientes"),
                        new Tag().name("Ficha técnica"),
                        new Tag().name("Preços"),
                        new Tag().name("Estoque"),
                        new Tag().name("Pedidos (comandas)"),
                        new Tag().name("Itens do pedido"),
                        new Tag().name("Caixa"),
                        new Tag().name("Vendas")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira seu token JWT")));
    }
}