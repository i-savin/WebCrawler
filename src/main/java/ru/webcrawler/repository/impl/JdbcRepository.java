package ru.webcrawler.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import ru.webcrawler.entity.Page;
import ru.webcrawler.repository.Repository;

/**
 * @author isavin
 */
@org.springframework.stereotype.Repository
public class JdbcRepository implements Repository {

    private final static String INSERT_SQL_SCRIPT = "INSERT INTO PUBLIC.PAGES (URL, TEXT) VALUES (?, ?)";

    private JdbcOperations jdbcOperations;

    @Autowired
    public JdbcRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public void save(Page page) {
        jdbcOperations.update(INSERT_SQL_SCRIPT, page.getLink(), page.getText());
    }
}
