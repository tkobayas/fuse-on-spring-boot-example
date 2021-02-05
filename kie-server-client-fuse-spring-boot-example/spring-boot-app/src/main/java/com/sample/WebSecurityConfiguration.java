package com.sample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * WebSecurityConfiguration
 */
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Value("${camel.component.servlet.mapping.context-path}")
  private String contextPath;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().cors().configurationSource(corsConfigurationSource());
  }

  private CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cors = new CorsConfiguration();
    cors.addAllowedHeader(CorsConfiguration.ALL);
    cors.addAllowedOrigin(CorsConfiguration.ALL);
    cors.addAllowedMethod(CorsConfiguration.ALL);
    cors.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource corsSrc = new UrlBasedCorsConfigurationSource();
    corsSrc.registerCorsConfiguration(contextPath, cors);

    return corsSrc;
  }

}