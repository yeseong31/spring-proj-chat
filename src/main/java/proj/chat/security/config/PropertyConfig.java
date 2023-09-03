package proj.chat.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:env.oauth.properties"),
        @PropertySource("classpath:env.local.properties")
})
public class PropertyConfig {

}
