package com.mayreh.typo.web.tool.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app")
public class AppProperties {
    private String outputDir;
}
