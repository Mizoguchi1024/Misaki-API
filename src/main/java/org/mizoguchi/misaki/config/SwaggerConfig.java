package org.mizoguchi.misaki.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Misaki")
                        .description("基于 MCP 的人工智能助理")
                        .version("1.0.0 Dev")
                        .contact(new Contact()
                                .name("Mizoguchi")
                                .email("kiyomizu_wx@qq.com")
                                .url("https://github.com/Mizoguchi1024"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                );
    }
}
