package ru.webcrawler.dao;

import ru.webcrawler.entity.Page;

/**
 * Created by isavin on 28.08.2015.
 */
public interface PageDAO {

    public void create(Page page) throws DaoException;
}
