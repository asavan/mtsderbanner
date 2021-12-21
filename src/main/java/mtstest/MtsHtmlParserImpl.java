package mtstest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by asavan on 04.06.2017.
 */
public class MtsHtmlParserImpl implements MtsHtmlParser {

    private static final Logger log = LogManager.getLogger(MtsHtmlParserImpl.class);
    private static final String BASE_URL = "https://shop.mts.ru";


    @Override
    public SmartfonInfo parseOne(String url) throws IOException {
        return parseOnePage(url);
    }

    @Override
    public List<String> parsePAGENPage(String url) throws IOException {
        return parsePAGENPageStatic(url);
    }

    public static List<String> parsePAGENPageStatic(String url) throws IOException {
        List<String> urls = new ArrayList<>(20);
        log.info(url);
        Document doc = setupSpider(url)
                .timeout(10000).get();
        Elements phoneLink = doc.select(".card-product-description__heading");
        for (Element e : phoneLink) {
            String linkHref = e.attr("href").trim();
            log.trace(linkHref);
            urls.add(BASE_URL + linkHref);
        }
        return urls;
    }

    private static Connection setupSpider(String url) {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .cookie("qrator_jsr", "1640121185.251.epgsWGX8g3NjlVtu-jmh4do4a7k8hsg7ge62tvndjo2jop1lb-00")
                .cookie("qrator_jsid", "1640121185.251.epgsWGX8g3NjlVtu-28u85qqql4mdgpb0rl02s9erbfr1rqme");
    }

    public static SmartfonInfo parseOnePage(String url) throws IOException {
        log.debug(url);
        SmartfonInfo info = new SmartfonInfo();
        info.setOriginalUrl(url);

        Document doc = setupSpider(url).timeout(30000).get();
        Elements img = doc.select(".product-gallery-item__image");
        String imageUrl = img.attr("data-src");
        if (imageUrl.isEmpty()) {
            log.error("Empty image " + url);
            return null;
        }
        imageUrl = BASE_URL + imageUrl;
        info.setMainPhoto(imageUrl);


        Elements names = doc.select(".tech-specs .name");
        Elements values = doc.select(".tech-specs .value");
        if (names.size() != values.size()) {
            log.error("ERROR " + url);
            log.error(names.size());
            log.error(values.size());
            return null;
        }
        int i = 0;
        Map<String, String> properties = new LinkedHashMap<>(names.size());
        for (Element e : values) {
            String value = e.html().trim();
            String name = names.get(i).html().trim();
            properties.put(name, value);
            i++;
        }
        info.setProperties(properties);
        return info;
    }


}
