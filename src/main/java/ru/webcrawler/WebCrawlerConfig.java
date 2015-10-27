package ru.webcrawler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.webcrawler.repository.Repository;
import ru.webcrawler.repository.impl.JdbcRepository;

import javax.sql.DataSource;

/**
 * Created by isavin on 15.10.15.
 */
@Configuration
@ComponentScan
public class WebCrawlerConfig {

    @Bean
    public JdbcOperations jdbcOperations(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:hsql://localhost/xdb");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    public Repository repository() {
        return new JdbcRepository(jdbcOperations(dataSource()));
    }
}
