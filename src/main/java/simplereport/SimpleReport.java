package simplereport;

import java.util.Date;
import java.util.List;

public interface SimpleReport {
    SimpleReport addRow();

    SimpleReport addCaptionRow(List cells);

    SimpleReport addCell(String s);

    SimpleReport addCell(Object o);

    SimpleReport addCell(Date dt);

    SimpleReport addCells(List cells);

    SimpleReport skipCell();

    /**
     * @return fullFilename to saved report
     */
    String save(String dirName);

    String toString();

}
