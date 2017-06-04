package mtstest;

import java.io.IOException;

public interface MtsParser {
    void exit();

    void parseList(String typeName, int pages) throws IOException;
}
