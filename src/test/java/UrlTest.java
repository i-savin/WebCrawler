import org.junit.Test;
import ru.webcrawler.entity.Url;

import static org.junit.Assert.*;

/**
 * Created by isavin on 27.08.2015.
 */
public class UrlTest {

    @Test
    public void testEquals() {
        Url url1 = new Url("link", 1);
        Url url2 = new Url("Link", 2);

        assertTrue(url1.equals(url2));
    }
}
