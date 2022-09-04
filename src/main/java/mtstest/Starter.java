package mtstest;

import java.io.IOException;

public class Starter {
    public static void main(String[] args) throws IOException, InterruptedException {
        MtsParser m = new MtsParserServiceImpl(true, true, 3);
        m.parseList("smartfony", 32);
        m.exit();
    }
}
