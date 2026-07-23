package shop.apppang.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String schemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    // 모든 API에 공통으로 500 응답 문서를 자동으로 추가
    @Bean
    public GlobalOpenApiCustomizer globalErrorResponseCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    Schema<?> errorSchema = new Schema<>()
                            .type("object")
                            .addProperty("error", new StringSchema().example("서버 오류가 발생했습니다."));

                    ApiResponse serverError = new ApiResponse()
                            .description("서버 내부 오류")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(errorSchema)));

                    operation.getResponses().addApiResponse("500", serverError);
                })
        );
    }
}