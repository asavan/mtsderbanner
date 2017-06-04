package simplereport;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class AbstractSimpleReport implements SimpleReport {
    final String fileName;
    final StringBuilder sb = new StringBuilder();
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/MMMM/yyyy HH:mm:ss");

    AbstractSimpleReport(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public SimpleReport addCell(Object o) {
        if (o instanceof Date) {
            return addCell((Date) o);
        }
        addCell(o == null ? "" : o.toString());
        return this;
    }


    @Override
    public SimpleReport addCell(Date dt) {
        addCell(dt == null ? "" : simpleDateFormat.format(dt));
        return this;
    }


    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public SimpleReport addCells(List cells) {
        for (Object cell : cells) {
            addCell(cell);
        }
        return this;
    }


    @Override
    public SimpleReport addCaptionRow(List cells) {
        addRow();
        addCells(cells);
        return this;
    }

}
