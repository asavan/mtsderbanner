package simplereport;

public class CsvReport extends AbstractSimpleReport {
    private static final char DELIMITER = ';';
    private int rowNum = 0;
    private final StringBuilder sb = new StringBuilder();


    public CsvReport(String fileName) {
        this(fileName, "csv");
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
    public String toString() {
        return sb.toString();
    }

}
