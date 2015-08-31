package ru.webcrawler.dao;

import ru.webcrawler.entity.Page;

/**
 * Created by isavin on 28.08.2015.
 */
public interface PageDAO {

    public void save(Page page) throws DaoException;
}
