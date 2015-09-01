package ru.webcrawler.dao;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webcrawler.Settings;
import ru.webcrawler.entity.Page;

import javax.sql.DataSource;
import java.sql.*;
import static org.junit.Assert.*;
/** 
* PageDaoJdbcImpl Tester. 
* 
* @author <Authors name> 
* @since <pre>сен 1, 2015</pre> 
* @version 1.0 
*/ 
public class PageDaoJdbcImplTest {
    private final static Logger logger = LoggerFactory.getLogger(PageDaoFileImpl.class);

    private PageDAO dao;
    private Page page;

    private String connectionUrl = Settings.getSettingsInstance().get("db.test.connection.url");//"jdbc:hsqldb:mem:.";
    private String driverName = Settings.getSettingsInstance().get("db.test.driver.name");//"org.hsqldb.jdbcDriver";
    private String userName = Settings.getSettingsInstance().get("db.test.user.name");//"SA";
    private String password = Settings.getSettingsInstance().get("db.test.password");//"";
    private String createTableScript = "CREATE TABLE Pages (ID IDENTITY, URL varchar(255) NOT NULL, TEXT LONGVARCHAR)";

    @Before
    public void before() throws Exception {
        dao = new PageDaoJdbcImpl(connectionUrl, driverName, userName, password);
        page = new Page("www.example.com", "test");
        Class.forName(driverName);
        try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
            PreparedStatement ps = connection.prepareStatement(createTableScript);) {
            ps.execute();
        }
    }

    @After
    public void after() throws Exception {

    }

    @Test
    public void testSave() throws Exception {
        logger.info("[Saving to DB] test started");
        dao.save(page);
        logger.info("Page saved to DB");

        try (Connection connection = DriverManager.getConnection(connectionUrl, userName, password);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Pages");
            ResultSet rs = ps.executeQuery()) {

            rs.next();
            String url = rs.getString(2);
            String text = rs.getString(3);

            assertFalse(rs.next());

            Page actual = new Page(url, text);
            assertEquals(page, actual);

            logger.info("[Saving to DB] test succeed");
        }
    }
} 
