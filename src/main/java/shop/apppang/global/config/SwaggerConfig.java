package shop.apppang.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Set;

@Configuration
public class SwaggerConfig {

    // SecurityConfig의 permitAll 목록과 반드시 동기화할 것 (컨트롤러 클래스명#메서드명)
    private static final Set<String> PUBLIC_OPERATIONS = Set.of(
            "ProductController#getProducts",
            "ProductController#getProduct",
            "ReviewController#getReviews",
            "CategoryController#getCategories",
            "BannerController#getBanners",
            "SearchController#getSuggestions",
            "ChatController#chat",
            "AuthController#signup",
            "AuthController#login",
            "AuthController#checkEmail",
            "AuthController#findEmail",
            "AuthController#verifyPasswordReset",
            "AuthController#resetPassword"
    );

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

    // permitAll API는 자물쇠(인증 필요 표시)를 제거. springdoc이 만든 Operation 모델을 직접 수정하므로
    // @Operation(security = {})와 달리 확실하게 반영된다 (해당 애너테이션 방식은 springdoc에서 알려진 미동작 이슈가 있음)
    @Bean
    public OperationCustomizer publicApiSecurityCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            String key = handlerMethod.getBeanType().getSimpleName() + "#" + handlerMethod.getMethod().getName();
            if (PUBLIC_OPERATIONS.contains(key)) {
                operation.setSecurity(List.of());
            }
            return operation;
        };
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