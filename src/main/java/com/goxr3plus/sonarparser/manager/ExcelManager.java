package main.java.com.goxr3plus.sonarparser.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import main.java.com.goxr3plus.sonarparser.model.Project;
import main.java.com.goxr3plus.sonarparser.tools.AbstractManager;
import main.java.com.goxr3plus.sonarparser.tools.StaticStaff;

@Component
public class ExcelManager extends AbstractManager{
    
    private final String basePath = "SonarExcel/";
    

    public void exportExcel(final List<Project> projects,final LocalDate previousWeekDate) throws IOException {
	System.err.println("Creating excel");

	/* Read previous week report */
	List<Project> previousWeekProjects = readPreviousWeekReport(previousWeekDate);

	/* Create XSSFWorkbook & XSSFSheet */
	XSSFWorkbook workbook = new XSSFWorkbook();
	XSSFSheet sheet = workbook.createSheet("Datatypes in Java");

	// --------------- Create Styles -----------------------//

	/* Create Default Style */
	XSSFFont font = workbook.createFont();
	font.setFontHeightInPoints((short) 10);
	font.setFontName("Arial");
	font.setBold(true);

	CellStyle defaultStyle = workbook.createCellStyle();
	defaultStyle.setAlignment(HorizontalAlignment.LEFT);
	defaultStyle.setFont(font);

	XSSFFont font2 = workbook.createFont();
	font2.setFontHeightInPoints((short) 10);
	font2.setFontName("Arial");
	font2.setBold(true);

	CellStyle defaultStyleRight = workbook.createCellStyle();
	defaultStyleRight.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
	defaultStyleRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	defaultStyleRight.setAlignment(HorizontalAlignment.RIGHT);
	defaultStyleRight.setFont(font2);

	/* Create RED Coverage Style */
	XSSFFont redFont = workbook.createFont();
	redFont.setFontHeightInPoints((short) 10);
	redFont.setColor(HSSFColor.WHITE.index);
	redFont.setFontName("Arial");
	redFont.setBold(true);

	CellStyle redStyle = workbook.createCellStyle();
	redStyle.setAlignment(HorizontalAlignment.RIGHT);
	redStyle.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
	redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	redStyle.setFont(redFont);

	/* Create GREEN Coverage Style */
	XSSFFont greenFont = workbook.createFont();
	greenFont.setFontHeightInPoints((short) 10);
	greenFont.setColor(HSSFColor.BLACK.index);
	greenFont.setFontName("Arial");
	greenFont.setBold(true);

	CellStyle greenStyle = workbook.createCellStyle();
	greenStyle.setAlignment(HorizontalAlignment.RIGHT);
	greenStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
	greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	greenStyle.setFont(greenFont);

	/* Iterate the data */
	int[] rowNum = { 0 };

	/* Create row 0-Descriptions */
	Row row0 = sheet.createRow(rowNum[0]);
	CellStyle cellStyle0 = row0.getSheet().getWorkbook().createCellStyle();
	cellStyle0.setAlignment(HorizontalAlignment.CENTER);
	for (int i = 0; i <= 4; i++) {

	    /* Create Row Count */
	    Cell cell = row0.createCell(i);
	    cell.setCellStyle(cellStyle0);
	    if (i == 0)
		cell.setCellValue("NO");
	    else if (i == 1)
		cell.setCellValue("Category");
	    else if (i == 2)
		cell.setCellValue("Component");
	    else if (i == 3)
		cell.setCellValue("Last Week");
	    else if (i == 4)
		cell.setCellValue("This week");

	}

	Arrays.stream(StaticStaff.datatypes).forEach(datatype -> {
	    rowNum[0]++;
	    Row row = sheet.createRow(rowNum[0]);

	    int[] colNum = { 0 };

	    /* Create Row Count */
	    Cell cell1 = row.createCell(colNum[0]++);
	    cell1.setCellStyle(defaultStyle);
	    cell1.setCellValue(rowNum[0]);

	    Arrays.stream(datatype).forEach(field -> {
		Cell cell = row.createCell(colNum[0]++);
		cell.setCellStyle(defaultStyle);
		if (field instanceof String) {
		    cell.setCellValue((String) field);
		} else if (field instanceof Integer) {
		    cell.setCellValue((Integer) field);
		}
	    });

	    // --------------- Add Coverage -----------------------//

	    /* Add Previous Week Coverage */
	    Project thisWeekProject = projects.get(rowNum[0] - 1);
	    Project previousWeekProject = previousWeekProjects.stream().filter(project -> project.getName().equals(thisWeekProject.getName())).findFirst()
		    .get();
	    Cell cell4 = row.createCell(3);
	    cell4.setCellStyle(defaultStyleRight);
	    cell4.setCellValue(previousWeekProject.getCoverage());

	    /* Add Current Week Coverage */
	    Cell cell5 = row.createCell(4);
	    Double coveragePreviousWeek = previousWeekProject.getCoverageAsDouble();
	    Double coverageThisWeek = thisWeekProject.getCoverageAsDouble();
	    if (coveragePreviousWeek > coverageThisWeek)
		cell5.setCellStyle(redStyle);
	    else if (coverageThisWeek > coveragePreviousWeek)
		cell5.setCellStyle(greenStyle);
	    else
		cell5.setCellStyle(defaultStyleRight);
	    cell5.setCellValue(thisWeekProject.getCoverage());
	});

	// Auto size column widths
	for (int i = 0; i < 7; i++)
	    sheet.autoSizeColumn(i);
	sheet.setColumnWidth(0, 1500);
	sheet.setColumnWidth(3, 3500);
	sheet.setColumnWidth(4, 3500);

	/* Create excel file */
	File file = getSonarQubeReport(LocalDate.now());
	System.err.println("File exists ... deleting " + FileUtils.deleteQuietly(file));

	/* Write excel file */
	try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
	    workbook.write(outputStream);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	System.err.println("Excel Created");
    }

    private File getSonarQubeReport(final LocalDate localDate) {
	return new File(basePath + "SonarQube_" + localDate + ".xlsx");
    }

    /**
     * Extract data from previous week report
     * 
     * @return A list containing the final column of the previous week report as
     *         Strings
     */
    public List<Project> readPreviousWeekReport(final LocalDate previousWeekDate) {

	List<Project> results = new ArrayList<>();

	try {

	    // Creating a Workbook from an Excel file (.xls or .xlsx)
	    Workbook workbook = WorkbookFactory.create(getSonarQubeReport(previousWeekDate));

	    // Getting the Sheet at index zero
	    Sheet sheet = workbook.getSheetAt(0);

	    // Create a DataFormatter to format and get each cell's value as String
	    DataFormatter dataFormatter = new DataFormatter();

	    // 3. Or you can use Java 8 forEach loop with lambda
	    int[] rowCounter = { 0 };
	    int[] columnNumber = { 0 };
	    sheet.forEach(row -> {
		String[] name = { "" };
		String[] coverage = { "" };
		if (rowCounter[0] >= 1) {
		    row.forEach(cell -> {

			/* First find the name */
			if (columnNumber[0] == 2) {
			    name[0] = dataFormatter.formatCellValue(cell);
			}
			/* Then put in the name the coverage too */
			else if (columnNumber[0] == 4) {
			    coverage[0] = dataFormatter.formatCellValue(cell);
			}
			columnNumber[0]++;
		    });

		    results.add(new Project(name[0], "", coverage[0], ""));
		}

		rowCounter[0]++;
		columnNumber[0] = 0;
	    });

	    // Closing the workbook
	    workbook.close();
	} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
	    e.printStackTrace();
	}

	return results;
    }

}
