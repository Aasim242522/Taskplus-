package com.taskmonitor.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Production DataSource configuration.
 * Automatically translates Render/Neon database URLs (postgres:// or postgresql://)
 * into standard JDBC format (jdbc:postgresql://) and extracts credentials if embedded.
 */
@Configuration
@Profile("prod")
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${DATABASE_URL:${SPRING_DATASOURCE_URL:${spring.datasource.url:}}}")
    private String rawDatabaseUrl;

    @Value("${DATABASE_USERNAME:${SPRING_DATASOURCE_USERNAME:${spring.datasource.username:postgres}}}")
    private String defaultUsername;

    @Value("${DATABASE_PASSWORD:${SPRING_DATASOURCE_PASSWORD:${spring.datasource.password:postgres}}}")
    private String defaultPassword;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:2}")
    private int minIdle;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        if (rawDatabaseUrl == null || rawDatabaseUrl.isBlank() || rawDatabaseUrl.contains("YOUR_DATABASE_URL_HERE")) {
            log.error("DATABASE_URL is not configured properly. Current value: '{}'", rawDatabaseUrl);
            throw new IllegalArgumentException(
                    "DATABASE_URL is not set or still contains the placeholder 'YOUR_DATABASE_URL_HERE'. " +
                    "Please update DATABASE_URL in your Render Environment Variables with your PostgreSQL connection string."
            );
        }

        String finalJdbcUrl = rawDatabaseUrl;
        String finalUsername = defaultUsername;
        String finalPassword = defaultPassword;

        if (rawDatabaseUrl.startsWith("postgres://") || rawDatabaseUrl.startsWith("postgresql://")) {
                try {
                    URI uri = new URI(rawDatabaseUrl);
                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && !userInfo.isBlank()) {
                        String[] userParts = userInfo.split(":", 2);
                        if (userParts.length > 0 && !userParts[0].isEmpty()) {
                            finalUsername = userParts[0];
                        }
                        if (userParts.length > 1) {
                            finalPassword = userParts[1];
                        }
                    }

                    String host = uri.getHost();
                    int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                    String path = uri.getPath(); // includes leading '/'
                    String query = uri.getQuery();

                    finalJdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                    if (query != null && !query.isBlank()) {
                        finalJdbcUrl += "?" + query;
                    }
                    log.info("Converted Render database URL to standard JDBC URL: jdbc:postgresql://{}:{}{}", host, port, path);
                } catch (URISyntaxException e) {
                    log.warn("Could not parse database URL via URI, falling back to prefix replacement: {}", e.getMessage());
                    finalJdbcUrl = rawDatabaseUrl.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
                }
            }
        }

        config.setJdbcUrl(finalJdbcUrl);
        if (finalUsername != null && !finalUsername.isBlank()) {
            config.setUsername(finalUsername);
        }
        if (finalPassword != null && !finalPassword.isBlank()) {
            config.setPassword(finalPassword);
        }
        config.setDriverClassName(driverClassName);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);

        return new HikariDataSource(config);
    }
}
