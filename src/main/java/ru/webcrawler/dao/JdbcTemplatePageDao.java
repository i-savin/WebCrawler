package ru.webcrawler.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import ru.webcrawler.entity.Page;

/**
 * Created by isavin on 03.09.2015.
 */
public class JdbcTemplatePageDao implements PageDAO{

    private final static String INSERT_SQL_SCRIPT = "INSERT INTO PUBLIC.PAGES (URL, TEXT) VALUES (?, ?)";

    private JdbcTemplate template;

    @Override
    public void save(Page page) throws DaoException {
        try {
            template.update(INSERT_SQL_SCRIPT, new Object[]{page.getLink(), page.getText()});
        } catch (Exception e) {
            throw new DaoException("Error writing page " + page.getLink() + " to DB", e);
        }
    }

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }
}
