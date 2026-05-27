package com.cts.connectease.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading test data from Excel (.xlsx) files using Apache POI.
 *
 * <p>Usage with TestNG {@code @DataProvider}:
 * <pre>
 *   {@literal @}DataProvider(name = "myData")
 *   public static Object[][] myData() {
 *       return new ExcelDataReader("src/test/resources/testdata/ConnectEase_TestData.xlsx")
 *                  .getSheetData("MySheet");
 *   }
 * </pre>
 *
 * <p>Sheet format:
 * <ul>
 *   <li>Row 0 — header row (skipped automatically)</li>
 *   <li>Row 1+ — data rows; each becomes one DataProvider iteration</li>
 *   <li>Empty rows are skipped</li>
 *   <li>All cell values are returned as trimmed {@code String}</li>
 * </ul>
 */
public class ExcelDataReader {

    private final String filePath;

    /**
     * @param filePath path to the .xlsx file (relative to project root or absolute)
     */
    public ExcelDataReader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads all non-empty data rows from the named sheet.
     * The first row is treated as a header and is skipped.
     *
     * @param sheetName name of the worksheet to read
     * @return 2D {@code Object[][]} — one row per test iteration, one column per parameter
     * @throws RuntimeException if the file cannot be opened or the sheet is not found
     */
    public Object[][] getSheetData(String sheetName) {
        List<Object[]> rows = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException(
                    "[ExcelDataReader] Sheet '" + sheetName + "' not found in: " + filePath
                    + ". Available sheets: " + listSheetNames(workbook));
            }

            int firstRow = sheet.getFirstRowNum();
            int lastRow  = sheet.getLastRowNum();

            if (lastRow <= firstRow) {
                System.out.println("[ExcelDataReader] Sheet '" + sheetName
                    + "' has no data rows (header only). Returning empty array.");
                return new Object[0][];
            }

            // Determine column count from the header row
            Row headerRow = sheet.getRow(firstRow);
            int numCols   = (headerRow != null) ? headerRow.getLastCellNum() : 0;

            // Skip header (firstRow); iterate data rows
            for (int r = firstRow + 1; r <= lastRow; r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) continue;

                Object[] rowData = new Object[numCols];
                for (int c = 0; c < numCols; c++) {
                    Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData[c] = readCellAsString(cell);
                }
                rows.add(rowData);
            }

        } catch (IOException e) {
            throw new RuntimeException(
                "[ExcelDataReader] Cannot read Excel file: " + filePath, e);
        }

        System.out.println("[ExcelDataReader] Sheet '" + sheetName
            + "' — loaded " + rows.size() + " data row(s) from: " + filePath);
        return rows.toArray(new Object[0][]);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Reads a cell value as a trimmed String regardless of the cell type.
     * Numeric whole-number values (e.g. phone numbers) are returned without
     * a decimal point — "9000000001" not "9.000000001E9".
     */
    private String readCellAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                double val = cell.getNumericCellValue();
                // Avoid scientific notation for large integers (phone numbers, IDs)
                yield (val == Math.floor(val) && !Double.isInfinite(val))
                    ? String.valueOf((long) val)
                    : String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                // Evaluate formula result as numeric string where possible
                try   { yield String.valueOf((long) cell.getNumericCellValue()); }
                catch (Exception ignored) { yield cell.getCellFormula(); }
            }
            default -> "";
        };
    }

    /** Returns true if every cell in the row is blank or empty. */
    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null
                    && cell.getCellType() != CellType.BLANK
                    && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** Builds a comma-separated list of all sheet names for error messages. */
    private String listSheetNames(Workbook wb) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(wb.getSheetName(i));
        }
        return sb.append("]").toString();
    }
}
