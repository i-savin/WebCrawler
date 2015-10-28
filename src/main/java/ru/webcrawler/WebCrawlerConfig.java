package ru.webcrawler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.webcrawler.domain.Transfer;
import ru.webcrawler.domain.Writer;
import ru.webcrawler.entity.Page;

import javax.sql.DataSource;
import java.util.concurrent.BlockingQueue;

/**
 * Created by isavin on 15.10.15.
 */
@Configuration
@ComponentScan
public class WebCrawlerConfig {

    @Bean
    public JdbcOperations jdbcOperations() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:hsql://localhost/");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean
    @Scope(value = "prototype")
    public Writer writer(BlockingQueue<Page> pages) {
        return new Writer(pages);
    }

    @Bean
    @Scope(value = "prototype")
    public Transfer transfer(BlockingQueue<Page> pages, String url, int depth) {
        return new Transfer(pages, url, depth);
    }
}
