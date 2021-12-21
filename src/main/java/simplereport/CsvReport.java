package simplereport;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CsvReport extends AbstractSimpleReport {
    private static final char DELIMITER = ';';
    private static final Charset ENCODING = StandardCharsets.UTF_8;

    private int rowNum = 0;


    public CsvReport(String fileName) {
        super(fileName + ".csv");
    }

    public CsvReport(String fileName, String ext) {
        super(fileName + "." + ext);
    }

    @Override
    public SimpleReport addRow() {
        if (rowNum > 0) {
            sb.append("\r\n");
        }
        rowNum++;
        return this;
    }

    private String csvEscapeString(String s) {
        return csvEscapeString(s, DELIMITER);
    }

    private static String csvEscapeString(String s, char delimiter) {
        if (s.indexOf('"') < 0 && s.indexOf('\r') < 0 && s.indexOf('\n') < 0 && s.indexOf(delimiter) < 0) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length() + 10);
        sb.append('"');
        if (s.indexOf('"') >= 0) {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '"') {
                    sb.append('"');
                }
                sb.append(s.charAt(i));
            }
        }
        sb.append('"');
        return sb.toString();
    }

    @Override
    public SimpleReport addCell(String s) {
        if (s != null) {
            sb.append(csvEscapeString(s));
        }
        sb.append(DELIMITER);
        return this;
    }

    @Override
    public SimpleReport skipCell() {
        sb.append(DELIMITER);
        return this;
    }


    @Override
    public String save(String dir) throws IOException {
        if (dir == null || dir.length() == 0) {
            dir = "";
        } else if (!dir.endsWith("/") && !dir.endsWith("\\")) {
            dir = dir + '/';
        }
        String fullFileName = dir + fileName;

        stringToFile(sb.toString(), fullFileName);

        return fullFileName;
    }

    private static void stringToFile(String tx, String fileName) throws IOException {
        Files.write( Paths.get(fileName), tx.getBytes(ENCODING));
    }
}
