package mtstest;

import java.io.IOException;

public class Starter {
    public static void main(String[] args) throws IOException {
        MtsParser m = new MtsParserServiceImpl(true, true, 11);
        m.parseList("mobilnyye-telefony", 8);
        m.parseList("planshety", 11);
        m.parseList("smartfony", 26);
        m.exit();
    }
}
