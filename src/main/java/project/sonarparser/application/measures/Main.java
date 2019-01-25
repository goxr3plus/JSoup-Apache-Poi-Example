package main.java.project.sonarparser.application.measures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

import dnl.utils.text.table.TextTable;

public class Main {

    private final String basePath = "SonarExcel/";
    private final LocalDate previousWeekDate = LocalDate.of(2019, 1, 18);

    public Main() {

	File input = new File("SonarQube.html");

	try {
	    Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

	    // Parse the damn html
	    Element table = doc.getElementById("measures-table").getElementsByTag("tbody").first();
	    parseTable(table);

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Parse the html table
     * 
     * @param table
     * @throws IOException
     */
    public void parseTable(final Element table) throws IOException {

	int counter = 0;
	List<Project> projects = new ArrayList<>();
	for (Element element : table.getElementsByTag("tbody").first().getElementsByTag("tr")) {
	    int skipColumns = 0;
	    Project project = new Project();
	    for (Element el : element.getElementsByTag("td")) {
		String text = el.text().replaceAll("master|develop|L10", "").replace("Java", "J").trim();

		// Skip first two columns
		if (skipColumns < 1)
		    ++skipColumns;
		else {
		    counter++;
		    switch (counter) {
		    case 1:
			project.setName(StringUtils.capitalize(text));
			break;
		    case 2:
			project.setCoverage(text);
			break;
		    case 3:
			project.setLastAnalysis(text);
			break;
		    case 4:
			project.setVersion(text);

			break;
		    default:
		    }

		}

	    }
	    projects.add(project);
	    counter = 0;
	}

	/* Get only the projects we want */
	projects = projects.stream().filter(project -> StaticStaff.ignoreList.contains(project.getName()))
		.peek(project -> project.setVersion(project.getVersion().replaceAll("\\Q.\\E|LVS_|-|not provided|SNAPSHOT", "").trim()))
		.collect(Collectors.toList());

	/*
	 * Create a multimap because some Projects have duplicate name and different
	 * version
	 */
	final ImmutableListMultimap<String, Project> multiMap = Multimaps.index(projects, Project::getName);
	int[] r = { 0 };
	multiMap.keySet().forEach(projectName -> {
	    List<Project> sortedProjects = multiMap.get(projectName).stream().sorted().collect(Collectors.toList());
	    System.err.println((r[0]++) + " " + sortedProjects);
	});

	/* Prepare the printing table */
	String[] columnNames = { "Counting", "Name", "Coverage", "Last Updated", "Version" };
	int totalRows = projects.size();
	String[][] items = new String[totalRows][columnNames.length];
	int row = 0;
	counter = 0;

	for (Project project : projects) {
	    items[row][counter] = String.valueOf(row);
	    items[row][++counter] = project.getName();
	    items[row][++counter] = project.getCoverage();
	    items[row][++counter] = project.getLastAnalysis();
	    items[row][++counter] = project.getVersion();

	    row++;
	    counter = 0;
	}

	// Print the results as a table
	TextTable tt = new TextTable(columnNames, items);
	tt.printTable();

	/* Export to Excel File */
	exportExcel(projects);

    }

    public void exportExcel(final List<Project> projects) throws IOException {
	System.err.println("Creating excel");

	/* Read previous week report */
	List<String> previousWeekProjects = readPreviousWeekReport();
	// System.err.println(previousWeekProjects);

	/* Create XSSFWorkbook & XSSFSheet */
	XSSFWorkbook workbook = new XSSFWorkbook();
	XSSFSheet sheet = workbook.createSheet("Datatypes in Java");

	/* Create bold font */
	XSSFFont font = workbook.createFont();
	font.setFontHeightInPoints((short) 10);
	font.setFontName("Arial");
	font.setBold(true);

	/* Iterate the data */
	int[] rowNum = { 0 };
	Arrays.stream(StaticStaff.datatypes).forEach(datatype -> {
	    Row row = sheet.createRow(rowNum[0]++);

	    /* Align all cells to left */
	    CellStyle cellStyle = row.getSheet().getWorkbook().createCellStyle();
	    cellStyle.setAlignment(HorizontalAlignment.LEFT);
	    int[] colNum = { 0 };

	    /* Create Row Count */
	    Cell cell1 = row.createCell(colNum[0]++);
	    cell1.setCellStyle(cellStyle);
	    cell1.setCellValue(rowNum[0]);

	    Arrays.stream(datatype).forEach(field -> {
		Cell cell = row.createCell(colNum[0]++);
		cell.setCellStyle(cellStyle);
		if (field instanceof String) {
		    cell.setCellValue((String) field);
		} else if (field instanceof Integer) {
		    cell.setCellValue((Integer) field);
		}
	    });

	    /* Create Coverage Style */
	    CellStyle cellStyle2 = row.getSheet().getWorkbook().createCellStyle();
	    cellStyle2.setAlignment(HorizontalAlignment.RIGHT);
	    cellStyle2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
	    cellStyle2.setFillBackgroundColor(IndexedColors.RED.getIndex());
	    cellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    font.setColor(HSSFColor.BLACK.index);
	    cellStyle2.setFont(font);

	    /* Add Previous Week Coverage */
//	    Cell cell4 = row.createCell(4);
//	    cell4.setCellStyle(cellStyle2);
//	    String previousWeekCoverage = previousWeekProjects.get(rowNum[0]).isEmpty() ? "0.0%" : previousWeekProjects.get(rowNum[0]);
//	    cell4.setCellValue(previousWeekCoverage);

	    /* Add Current Week Coverage */
	    Cell cell5 = row.createCell(5);
	    cell5.setCellStyle(cellStyle2);
	    String coverage = projects.get(rowNum[0]).getCoverage().isEmpty() ? "0.0%" : projects.get(rowNum[0]).getCoverage();
	    cell5.setCellValue(coverage);
	});

	// Auto size column widths
	for (int i = 0; i < 7; i++)
	    sheet.autoSizeColumn(i);
	sheet.setColumnWidth(0, 1500);
	sheet.setColumnWidth(3, 5500);
	sheet.setColumnWidth(4, 5500);

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

    /**
     * Extract data from previous week report
     * 
     * @return A list containing the final column of the previous week report as
     *         Strings
     */
    private List<String> readPreviousWeekReport() {

	List<String> results = new ArrayList<>();

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
		if (rowCounter[0] >= 1)
		    row.forEach(cell -> {

			/* We need last week report coverage column */
			if (columnNumber[0] == 4) {
			    String cellValue = dataFormatter.formatCellValue(cell);
			    results.add(cellValue);
			}
			columnNumber[0]++;
		    });
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

    private File getSonarQubeReport(final LocalDate localDate) {
	return new File(basePath + "SonarQube_" + localDate + ".xlsx");
    }

    public static void main(final String[] args) {
	new Main();
    }

}
