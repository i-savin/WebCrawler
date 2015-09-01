package ru.webcrawler.dao;

import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.entity.Page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import static org.junit.Assert.*;

public class PageDaoFileImplTest {
    private final static Logger logger = LoggerFactory.getLogger(PageDaoFileImpl.class);

    public static final String FILE_NAME = "test.txt";
    private PageDAO dao;
    private Page page;

    @Before
    public void before() throws Exception {
        dao = new PageDaoFileImpl(FILE_NAME);
        page = new Page("www.example.com", "test");
        logger.info("Dao created, new page created");
    }

    @After
    public void after() throws Exception {
        File file = new File(FILE_NAME);
        file.delete();
    }

    @Test
    public void testSave() throws Exception {
        logger.info("[Saving to file] test started");
        dao.save(page);
        File file = new File(FILE_NAME);
        logger.info("Page saved to file {}: {}", FILE_NAME, file.exists());

        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(FILE_NAME)))) {
            line = br.readLine();
            String[] pageParams = line.split("\t");
            assertEquals(pageParams.length, 2);

            Page actual = new Page(pageParams[0], pageParams[1]);
            assertEquals(page, actual);
        }
        logger.info("[Saving to file] test succeed");
    }


} 
