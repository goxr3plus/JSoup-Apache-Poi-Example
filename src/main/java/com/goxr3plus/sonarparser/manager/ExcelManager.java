package main.java.com.goxr3plus.sonarparser.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import main.java.com.goxr3plus.sonarparser.comporator.NameComporator;
import main.java.com.goxr3plus.sonarparser.model.Project;

@Component
public class ExcelManager extends AbstractManager {

    @Value("${files.basePath}")
    private String basePath;

    private XSSFFont font;
    private CellStyle defaultStyle;
    private XSSFFont font2;
    private CellStyle defaultStyleRight;
    private XSSFFont redFont;
    private CellStyle redStyle;
    private XSSFFont greenFont;
    private CellStyle greenStyle;

    /**
     * Get the given projects and create the Excel File
     * 
     * @param projects
     * @param previousWeekDate
     * @throws IOException
     */
    public void exportExcel(final List<Project> projects, final LocalDate previousWeekDate) throws IOException {

	log.info("Entered exportExcel ");

	/* Read previous week report */
	List<Project> previousWeekProjects = readPreviousWeekReport(previousWeekDate);
	Collections.sort(previousWeekProjects, new NameComporator());
	Collections.sort(projects, new NameComporator());

	/* Create XSSFWorkbook & XSSFSheet */
	XSSFWorkbook workbook = new XSSFWorkbook();
	XSSFSheet sheet = workbook.createSheet("Datatypes in Java");

	// --------------- Create Styles -----------------------//

	/* Init Styles */
	initStyles(workbook);

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

	projects.forEach(thisWeekProject -> {
	    rowNum[0]++;
	    Row row = sheet.createRow(rowNum[0]);

	    int[] colNum = { 0 };

	    /* Create Row Count */
	    Cell rowCountCell = row.createCell(colNum[0]++);
	    rowCountCell.setCellStyle(defaultStyle);
	    rowCountCell.setCellValue(rowNum[0]);

	    /* Application Category */
	    Cell categoryCell = row.createCell(colNum[0]++);
	    categoryCell.setCellValue(thisWeekProject.getCategory());

	    /* Application Name */
	    Cell nameCell = row.createCell(colNum[0]++);
	    nameCell.setCellValue(thisWeekProject.getName());

	    // --------------- Add Coverage -----------------------//

	    /* Add Previous Week Coverage */
	    Project previousWeekProject = previousWeekProjects.stream().filter(project -> project.getName().equals(thisWeekProject.getName())).findFirst()
		    .get();
	    Cell previousWeekCovCell = row.createCell(colNum[0]++);
	    previousWeekCovCell.setCellStyle(defaultStyleRight);
	    previousWeekCovCell.setCellValue(previousWeekProject.getCoverage());
	    System.err.println("----------------------------------------");
	    System.err.println(rowNum[0] + " This Week => " + thisWeekProject.getName() + ":" + thisWeekProject.getCoverage());
	    System.err.println(rowNum[0] + " Prev Week => " + previousWeekProject.getName() + ":" + previousWeekProject.getCoverage());
	    System.err.println("----------------------------------------");

	    /* Add Current Week Coverage */
	    Cell thisWeekCovCell = row.createCell(colNum[0]++);
	    Double coveragePreviousWeek = previousWeekProject.getCoverageAsDouble();
	    Double coverageThisWeek = thisWeekProject.getCoverageAsDouble();
	    if (coveragePreviousWeek > coverageThisWeek)
		thisWeekCovCell.setCellStyle(redStyle);
	    else if (coverageThisWeek > coveragePreviousWeek)
		thisWeekCovCell.setCellStyle(greenStyle);
	    else
		thisWeekCovCell.setCellStyle(defaultStyleRight);
	    thisWeekCovCell.setCellValue(thisWeekProject.getCoverage());
	});

	// Auto size column widths
	for (int i = 0; i < 7; i++)
	    sheet.autoSizeColumn(i);
	sheet.setColumnWidth(0, 1500);
	sheet.setColumnWidth(3, 3500);
	sheet.setColumnWidth(4, 3500);

	/* Create excel file */
	File file = getSonarQubeReport(LocalDate.now());
	log.error("File {} exists ... deleting = {}", file.getName(), FileUtils.deleteQuietly(file));

	/* Write excel file */
	try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
	    workbook.write(outputStream);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	log.info("Exited exportExcel ");
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

	// Creating a Workbook from an Excel file (.xls or .xlsx)
	try (Workbook workbook = WorkbookFactory.create(getSonarQubeReport(previousWeekDate))) {

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

    private void initStyles(final XSSFWorkbook workbook) {

	font = workbook.createFont();
	font.setFontHeightInPoints((short) 10);
	font.setFontName("Arial");
	font.setBold(true);

	defaultStyle = workbook.createCellStyle();
	defaultStyle.setAlignment(HorizontalAlignment.LEFT);
	defaultStyle.setFont(font);

	font2 = workbook.createFont();
	font2.setFontHeightInPoints((short) 10);
	font2.setFontName("Arial");
	font2.setBold(true);

	defaultStyleRight = workbook.createCellStyle();
	defaultStyleRight.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
	defaultStyleRight.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	defaultStyleRight.setAlignment(HorizontalAlignment.RIGHT);
	defaultStyleRight.setFont(font2);

	redFont = workbook.createFont();
	redFont.setFontHeightInPoints((short) 10);
	redFont.setColor(HSSFColor.WHITE.index);
	redFont.setFontName("Arial");
	redFont.setBold(true);

	redStyle = workbook.createCellStyle();
	redStyle.setAlignment(HorizontalAlignment.RIGHT);
	redStyle.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
	redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	redStyle.setFont(redFont);

	greenFont = workbook.createFont();
	greenFont.setFontHeightInPoints((short) 10);
	greenFont.setColor(HSSFColor.BLACK.index);
	greenFont.setFontName("Arial");
	greenFont.setBold(true);

	greenStyle = workbook.createCellStyle();
	greenStyle.setAlignment(HorizontalAlignment.RIGHT);
	greenStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
	greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	greenStyle.setFont(greenFont);
    }

}
