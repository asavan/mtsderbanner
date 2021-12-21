package simplereport;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class AbstractSimpleReport implements SimpleReport {
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    private final String fileName;

    AbstractSimpleReport(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public SimpleReport addCells(List<String> cells) {
        for (String cell : cells) {
            addCell(cell);
        }
        return this;
    }

    @Override
    public SimpleReport addCaptionRow(List<String> cells) {
        addRow();
        addCells(cells);
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

        stringToFile(toString(), fullFileName);

        return fullFileName;
    }

    private static void stringToFile(String tx, String fileName) throws IOException {
        Files.write( Paths.get(fileName), tx.getBytes(ENCODING));
    }

}
