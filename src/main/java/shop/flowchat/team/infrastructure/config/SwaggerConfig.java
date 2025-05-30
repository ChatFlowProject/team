package shop.flowchat.team.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class SwaggerConfig {
    @Value("${tag.version}")
    private String version;
    @Value("${chatflow.http-url}")
    private String httpUrl;
    @Value("${chatflow.https-url}")
    private String httpsUrl;
    private static final String SECURITY_SCHEME_NAME = "authorization";

    @Bean
    public OpenAPI openAPI() {
        List<Server> servers = new ArrayList<>();
        servers.add(0, new Server().url(httpUrl));
        servers.add(1, new Server().url(httpsUrl));

        return new OpenAPI()
                .servers(servers)
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Chatflow Team Service REST API Specifications")
                .description("Chatflow Team Service Specification")
                .version(version);
    }
}