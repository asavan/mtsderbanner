package mtstest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class MtsParserServiceImplTest {

    @Test
    public void testDateFormatter() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/MMMM/yyyy HH:mm:ss");
        String simple = simpleDateFormat.format(new Date());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MMMM/yyyy HH:mm:ss");
        String threadSafe = LocalDateTime.now().format(formatter);
        System.out.println(simple);
        assertEquals(simple, threadSafe);
    }

    @Test
    public void testParseOnePage() throws Exception {
        SmartfonInfo info = MtsHtmlParserImpl.parseOnePage(
                "https://shop.mts.ru/product/smartfon-apple-iphone-11-128gb-chernyj", MtsParserServiceImpl.BASE_URL);
        assertNotNull(info);
        assertNotEquals("", info.getArticul());
        System.out.println(info);
    }

    @Test
    public void testMobileShort() throws IOException, InterruptedException {
        MtsParser m = new MtsParserServiceImpl(false, false, 1);
        m.parseList("smartfony", 1);
        m.exit();
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
    public void testFullAsyncPages() throws IOException, InterruptedException {
        MtsParser m = new MtsParserServiceImpl(true, false, 14);
        m.parseList("mobilnyye-telefony", 8);
        m.parseList("planshety", 11);
        m.parseList("smartfony", 26);
        m.exit();
    }

    @Test
    public void testFullAsyncPhones() throws IOException, InterruptedException {
        MtsParser m = new MtsParserServiceImpl(false, true, 12);
        m.parseList("mobilnyye-telefony", 8);
        m.parseList("planshety", 11);
        m.parseList("smartfony", 26);
        m.exit();
    }

    @Test
    public void testFullAsync() throws IOException, InterruptedException {
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
