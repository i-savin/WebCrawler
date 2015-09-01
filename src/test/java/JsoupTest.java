import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by isavin on 27.08.2015.
 */
public class JsoupTest {

    @Test
    public void documentTest() {
        try {
            Document document = Jsoup.connect("http://sunset/").timeout(10 * 1000).get();
            System.out.println(document.body().text());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
