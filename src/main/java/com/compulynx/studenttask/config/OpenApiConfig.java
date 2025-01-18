package com.compulynx.studenttask.config;


import java.util.List;


import com.compulynx.studenttask.exception.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(name = "bearerToken", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    @Value("${studenttask.openapi.dev-url}")
    private String devUrl;

    @Value("${studenttask.openapi.prod-url}")
    private String prodUrl ;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("zadikiochola@gmail.com");
        contact.setName("Zadiki Hassan");
        contact.setUrl("https://www.students.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info().title("Student Management API").version("1.0").contact(contact).description("This API exposes endpoints to manage students data.").termsOfService("https://www.students.com/terms").license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }


    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        ResolvedSchema errResSchema = ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class));
        Content content = new Content().addMediaType("application/json", new MediaType().schema(errResSchema.schema));
        return openApi -> openApi.getPaths()
                .values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> operation.getResponses()
                        .addApiResponse("400", new ApiResponse().description("Bad Request").content(content))
                        .addApiResponse("403", new ApiResponse().description("Access Denied Request").content(content))
                        .addApiResponse("404", new ApiResponse().description("Resource Not Found").content(content))
                ));
    }
}
