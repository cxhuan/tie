package com.chelaile.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
@ConfigurationProperties(prefix = "env")
public class EnvConfig {
    private boolean verifyCode;

    public boolean isVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(boolean verifyCode) {
        this.verifyCode = verifyCode;
    }
}
