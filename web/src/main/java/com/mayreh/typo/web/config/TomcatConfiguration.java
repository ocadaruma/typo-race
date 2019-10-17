package com.mayreh.typo.web.config;

import org.apache.coyote.http2.Http2Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.getMimeMappings().add("html", "text/html;charset=utf-8");
        factory.getMimeMappings().add("css", "text/css;charset=utf-8");
        factory.getMimeMappings().add("js", "application/javascript;charset=utf-8");

        factory.addConnectorCustomizers(connector -> {
            connector.addUpgradeProtocol(new Http2Protocol());
        });
    }
}
