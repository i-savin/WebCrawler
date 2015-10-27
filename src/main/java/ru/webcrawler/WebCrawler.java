package ru.webcrawler;

import org.jsoup.helper.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.webcrawler.domain.Transfer;
import ru.webcrawler.domain.Writer;
import ru.webcrawler.entity.Page;
import ru.webcrawler.repository.Repository;
import ru.webcrawler.repository.impl.JdbcRepository;

import javax.sql.DataSource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by isavin on 26.08.2015.
 */
@Configuration
@ComponentScan
public class WebCrawler {

    private final static Logger logger = LoggerFactory.getLogger(WebCrawler.class);

    public static void main(String[] args) {
        Validate.isTrue(args.length == 2, "usage: WebCrawler url depth");
        String link = args[0];
        int depth = Integer.valueOf(args[1]);

        BlockingQueue<Page> pages = new LinkedBlockingQueue<>();
        Thread transferThread = new Thread(new Transfer(pages, link, depth));
        Thread writerThread = new Thread(new Writer(pages));
        transferThread.start();
        writerThread.start();
    }

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
