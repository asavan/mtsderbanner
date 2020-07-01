package mtstest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.io.IOException;


public class MtsParserServiceImplTest {

    @Test
    public void testParseOnePage() throws Exception {
        MtsHtmlParserImpl.parseOnePage("http://www.shop.mts.ru/smartfony/samsung/smartfon-galaxy-a5-2016-a510f-black.html");

        MtsHtmlParserImpl.parseOnePage("http://www.shop.mts.ru/smartfony/samsung/smartfon-galaxy-a3-2016-sm-a310f-white.html");
        MtsHtmlParserImpl.parseOnePage("http://www.shop.mts.ru/smartfony/samsung/smartfon-galaxy-a3-2016-sm-a310f-gold.html");

        SmartfonInfo info = MtsHtmlParserImpl.parseOnePage("http://www.shop.mts.ru/smartfony/archos/smartfon-40-helium-4g-dual-sim-lte-black.html");
        assertNotNull(info);
        System.out.println(info.getArticul());
    }

    @Test
    public void testMobileShort() throws IOException {
        MtsParser m = new MtsParserServiceImpl(true, true, 11);
        m.parseList("mobilnyye-telefony", 2);
    }

    @Test
    public void testMobileFull() throws IOException {
        MtsParser m = new MtsParserServiceImpl(true, true, 11);
        m.parseList("mobilnyye-telefony", 8);
    }

    @Test
    public void testFull() throws IOException {
        MtsParser m = new MtsParserServiceImpl(true, true, 10);
        m.parseList("mobilnyye-telefony", 8);
        m.parseList("planshety", 11);
        m.parseList("smartfony", 26);
    }

    @Test
    public void testFullAsyncPages() throws IOException {
        MtsParser m = new MtsParserServiceImpl(true, false, 14);
        m.parseList("mobilnyye-telefony", 8);
        m.parseList("planshety", 11);
        m.parseList("smartfony", 26);
    }

    @Test
    public void testFullAsyncPhones() throws IOException {
        MtsParser m = new MtsParserServiceImpl(false, true, 12);
        m.parseList("mobilnyye-telefony", 8);
        m.parseList("planshety", 11);
        m.parseList("smartfony", 26);
    }

    @Test
    public void testFullAsync() throws IOException {
        testFullAsyncPages();
        testFullAsyncPhones();
    }

    private static int f(int[] st, int level, int clCount, int stCount) {
        int count = 0;
        if (level == stCount) {
            int[] cl = new int[clCount];
            for (int k = 0; k < stCount; k++) {
                cl[st[k]] += 1;
            }
            boolean flag = false;
            for (int i = 0; i < clCount; i++) {
                if (cl[i] == 0) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                count++;
            }
            return count;
        }
        for (st[level] = 0; st[level] < clCount; st[level] += 1) {
            count += f(st, level + 1, clCount, stCount);
        }
        return count;
    }

    private static int fMain(int clCount, int stCount) {
        int[] st = new int[stCount];
        return f(st, 0, clCount, stCount);
    }

    @Test
    public void testSchool() {
        System.out.println(fMain(4, 6));
    }
}
