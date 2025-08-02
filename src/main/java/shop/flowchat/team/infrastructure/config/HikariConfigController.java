package shop.flowchat.team.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HikariConfigController {

    private final DataSource dataSource;

    @GetMapping("/teams/hikari-config")
    public Map<String, Object> hikariSettings() {
        HikariDataSource hikari = (HikariDataSource) dataSource;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("poolName", hikari.getPoolName());
        result.put("maximumPoolSize", hikari.getMaximumPoolSize());
        result.put("minimumIdle", hikari.getMinimumIdle());
        result.put("idleTimeout", hikari.getIdleTimeout());
        result.put("maxLifetime", hikari.getMaxLifetime());
        result.put("keepaliveTime", hikari.getKeepaliveTime());
        result.put("connectionTimeout", hikari.getConnectionTimeout());
        result.put("validationTimeout", hikari.getValidationTimeout());
        result.put("jdbcUrl", hikari.getJdbcUrl());
        result.put("username", hikari.getUsername());
        return result;
    }
}
