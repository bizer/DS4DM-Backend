package de.mannheim.uni.ds4dm.tableprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.mannheim.uni.ds4dm.model.Table;
import de.mannheim.uni.ds4dm.model.TableColumn;
import de.mannheim.uni.utils.Variables;

/**
 * Detects the key column of the table.
 *
 * @author petar
 *
 */
public class TableKeyIdentifier {

    public void indenfityLODKeys(Table table) {

        identifyKeys(table);
    }
    
    private static final Pattern prefLabelPattern = Pattern.compile("([^#]*#)?([a-z]{1,9})?prefLabel$");
    private static final Pattern namePattern =Pattern.compile("([^#]*#)?name$");
    private static final Pattern labelPattern = Pattern.compile("([^#]*#)?([a-z]{1,9})?label$");
    private static final Pattern titlePattern = Pattern.compile("([^#]*#)?([a-z]{1,9})?title$");
    private static final Pattern labelPattern2 =Pattern.compile("([^#]*#)?.*Label$");
    private static final Pattern namePattern2 = Pattern.compile("([^#]*#)?.*Name$");
    private static final Pattern titlePattern2 = Pattern.compile("([^#]*#)?.*Title$");
    private static final Pattern alternateNamePattern = Pattern.compile("([^#]*#)?([a-z]{1,9})?alternateName$");
    
    public void identifyKeys(Table table) {
        TableColumn key = null;
        int keyColumnIndex = -1;
        List<Double> columnUniqueness = new ArrayList<>(table.getColumns().size());

        for (int i=table.getColumns().size()-1; i>=0; i--) {
            TableColumn column = table.getColumns().get(i);
//            System.out.println("header: " + column.getHeader());
//            System.out.println("dt: " + column.getDataType());
            if (column.getDataType() != TableColumn.ColumnDataType.string) {
                continue;
            }
            if (prefLabelPattern.matcher(column.getHeader()).matches()) {
                key = column;
                break;
            }
            if (namePattern.matcher(column.getHeader()).matches()) {
                key = column;
                break;
            }
            if (labelPattern.matcher(column.getHeader()).matches()) {
                key = column;
            }

            if (titlePattern.matcher(column.getHeader()).matches()) {
                key = column;
            }
            if (labelPattern2.matcher(column.getHeader()).matches()) {
                key = column;
            }

            if (namePattern2.matcher(column.getHeader()).matches()) {
                key = column;
            }

            if (titlePattern2.matcher(column.getHeader()).matches()) {
                key = column;
            }
            if (alternateNamePattern.matcher(column.getHeader()).matches()) {
                key = column;
            }

        }
        if (key != null) {
            keyColumnIndex = table.getColumns().indexOf(key);
            if (table.getColumns().get(keyColumnIndex).getColumnUniqnessRank() >= Variables.keyUniqueness
                    && table.getColumns().get(keyColumnIndex).getColumnStatistic().getAverageValueLength() > 3.5
                    && table.getColumns().get(keyColumnIndex).getColumnStatistic().getAverageValueLength() <= 200) {
                table.getColumns().get(keyColumnIndex).setKey(true);
                table.setHasKey(true);
                return;
            }
            //the found key does not fit the requirements, see if another column does
            key = null;
        }

        for (TableColumn column : table.getColumns()) {
            columnUniqueness.add(column.getColumnUniqnessRank());
        }

        if (columnUniqueness.isEmpty()) {
            return;
        }
        double maxCount = -1;
        int maxColumn = -1;

        for (int i = 0; i < columnUniqueness.size(); i++) {
            if (columnUniqueness.get(i) > maxCount && table.getColumns().get(i).getDataType() == TableColumn.ColumnDataType.string
                    && table.getColumns().get(i).getColumnStatistic().getAverageValueLength() > 3.5
                    && table.getColumns().get(i).getColumnStatistic().getAverageValueLength() <= 200) {
                maxCount = (Double) columnUniqueness.get(i);
                maxColumn = i;
            }
        }

        if (key == null) {
            if (maxColumn == -1) {
                table.setHasKey(false);
                return;
            }
            key = table.getColumns().get(maxColumn);
        }
        keyColumnIndex = table.getColumns().indexOf(key);

        if (columnUniqueness.get(keyColumnIndex) < Variables.keyUniqueness) {
            table.setHasKey(false);
            return;
        }

        table.getColumns().get(keyColumnIndex).setKey(true);
        table.setHasKey(true);
    }
}
