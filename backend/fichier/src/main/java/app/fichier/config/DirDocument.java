package app.fichier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix="document.upload")
@Getter
@Setter
public class DirDocument {
    private String dir;
}
