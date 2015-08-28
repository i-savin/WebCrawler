package ru.webcrawler.dao;

import ru.webcrawler.entity.Page;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by isavin on 28.08.2015.
 */
public class PageDAOFileImpl implements PageDAO {

    private File file;

    public PageDAOFileImpl(String fileName) throws IOException {
        this.file = new File(fileName);
//        if (!(file.exists())) {
            file.createNewFile();
//        }
        file.setWritable(true);
    }

    @Override
    public void create(Page page) throws DaoException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(page.getLink());
            bw.write("\t");
            bw.write(page.getText());
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            throw new DaoException("Error writing page " + page.getLink() + " to file:", e);
        }
    }
}
