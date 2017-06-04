package simplereport;

import java.util.Date;

public interface SimpleReport {
    SimpleReport addRow();

    SimpleReport addRow(Object... cells);

    SimpleReport addCaptionRow(Object... cells);

    SimpleReport skipRow();

    SimpleReport skipRow(int countRow);

    SimpleReport addCell(String s);

    SimpleReport addCell(Object o);

    SimpleReport addCell(Date dt);

    SimpleReport addCells(Object... cells);

    SimpleReport skipCell();

    SimpleReport skipCell(int countCell);

    /**
     * @return fullFilename to saved report
     */
    String save(String dirName);

    String toString();

}
