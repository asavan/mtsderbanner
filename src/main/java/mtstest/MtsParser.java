package mtstest;

import java.io.IOException;

public interface MtsParser {
    void exit() throws InterruptedException;

    void parseList(String typeName, int pages) throws IOException;
}
