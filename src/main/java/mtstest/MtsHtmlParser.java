package mtstest;

import java.io.IOException;
import java.util.List;

/**
 *
 * Created by asavan on 04.06.2017.
 */
public interface MtsHtmlParser {
    SmartfonInfo parseOne(String paramString) throws IOException;

    List<String> parsePAGENPage(String paramString) throws IOException;

}
