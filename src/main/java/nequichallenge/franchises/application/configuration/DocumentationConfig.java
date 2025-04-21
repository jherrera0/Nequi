package nequichallenge.franchises.application.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentationConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Franchise API")
                        .version("1.0")
                        .description("API para la gestión de franquicias"));
    }

    @Bean
    public GroupedOpenApi franchiseApi() {
        return GroupedOpenApi.builder()
                .group("franchise")
                .pathsToMatch("/franchise/**")
                .build();
    }

    @Bean
    public GroupedOpenApi branchApi() {
        return GroupedOpenApi.builder()
                .group("branch")
                .pathsToMatch("/branch/**")
                .build();
    }
    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("product")
                .pathsToMatch("/product/**")
                .build();
    }
}
