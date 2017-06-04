package simplereport;


import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractSimpleReport implements SimpleReport {
    protected final String fileName;
    protected StringBuffer sb = new StringBuffer();
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/MMMM/yyyy HH:mm:ss");

    protected AbstractSimpleReport(String fileName) {
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
    public SimpleReport skipRow(int countRow) {
        while (countRow-- > 0) {
            skipRow();
        }
        return this;
    }

    @Override
    public SimpleReport skipCell(int countCell) {
        while (countCell-- > 0) {
            skipCell();
        }
        return this;
    }

    @Override
    public SimpleReport addCells(Object... cells) {
        for (Object cell : cells) {
            addCell(cell);
        }
        return this;
    }

    @Override
    public SimpleReport addRow(Object... cells) {
        addRow();
        addCells(cells);
        return this;
    }

    @Override
    public SimpleReport addCaptionRow(Object... cells) {
        addRow(cells);
        return this;
    }

}
