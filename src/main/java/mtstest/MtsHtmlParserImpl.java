package mtstest;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by asavan on 04.06.2017.
 */
public class MtsHtmlParserImpl implements MtsHtmlParser {

    private static final Logger log = Logger.getLogger(MtsHtmlParserImpl.class);
    private static final String BASE_URL = "http://www.shop.mts.ru";


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
        Document doc = Jsoup.connect(url).timeout(10000).get();
        Elements phoneLink = doc.select(".image-l");
        for (Element e : phoneLink) {
            String linkHref = e.attr("href").trim();
            urls.add(BASE_URL + linkHref);
        }
        return urls;
    }

    public static SmartfonInfo parseOnePage(String url) throws IOException {
        log.debug(url);
        SmartfonInfo info = new SmartfonInfo();
        info.setOriginalUrl(url);

        Document doc = Jsoup.connect(url).timeout(30000).get();
        Elements artikul = doc.select(".artikul a");
        info.setArticul(artikul.html().trim());
        Elements img = doc.select(".thumbs-holder a");
        String imageUrl = img.attr("data-large-image");
        if (imageUrl == null || imageUrl.isEmpty()) {
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
        Map<String, String> properties = new java.util.LinkedHashMap<>(names.size());
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
