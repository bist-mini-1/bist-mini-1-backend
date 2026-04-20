package com.bist.mini.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger(SpringDoc) 설정 클래스
 * API 문서 제목, 설명 및 Bearer(JWT) 인증 버튼 설정을 담당합니다.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "bearerAuth";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);
        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(new Info()
                        .title("BIST Mini Project API")
                        .description("BIST 미니 프로젝트 1기 백엔드 API 명세서입니다.")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
