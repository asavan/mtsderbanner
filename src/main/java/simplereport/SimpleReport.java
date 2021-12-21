package simplereport;

import java.io.IOException;
import java.util.List;

public interface SimpleReport {
    SimpleReport addRow();

    SimpleReport addCaptionRow(List<String> cells);

    SimpleReport addCell(String s);

    SimpleReport addCells(List<String> cells);

    SimpleReport skipCell();

    /**
     * @return fullFilename to saved report
     */
    String save(String dirName) throws IOException;

    String toString();

}
