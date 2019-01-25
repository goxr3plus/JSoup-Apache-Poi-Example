package main.java.project.sonarparser.application.measures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dnl.utils.text.table.TextTable;

public class Main {

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

	/* Get latest versions of duplicate projects */

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
	exportExcel();

    }

    public void exportExcel() throws IOException {
	System.err.println("Creating excel");

	/* Create XSSFWorkbook & XSSFSheet */
	XSSFWorkbook workbook = new XSSFWorkbook();
	XSSFSheet sheet = workbook.createSheet("Datatypes in Java");

	int[] rowNum = { 0 };

	/* Iterate the data */
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
	});

	// Auto size column widths
	for (int i = 0; i < 5; i++)
	    sheet.autoSizeColumn(i);
	sheet.setColumnWidth(0, 1500);

	/* Create excel file */
	File file = new File("file.xlsx");
	System.err.println("File exists ... deleting " + Files.deleteIfExists(file.toPath()));

	try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
	    workbook.write(outputStream);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	System.out.println("Excel Created");
    }

    public static void main(final String[] args) {
	new Main();
    }

}
