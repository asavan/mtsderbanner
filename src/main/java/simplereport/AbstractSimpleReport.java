package simplereport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class AbstractSimpleReport implements SimpleReport {
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
        return addRow().addCells(cells);
    }

    @Override
    public String save(String dir) throws IOException {
        if (dir == null || dir.isEmpty()) {
            dir = "";
        } else if (!dir.endsWith("/") && !dir.endsWith("\\")) {
            dir = dir + '/';
        }
        String fullFileName = dir + fileName;

        stringToFile(toString(), fullFileName);

        return fullFileName;
    }

    private static void stringToFile(String tx, String fileName) throws IOException {
        Files.writeString( Paths.get(fileName), tx);
    }

}
