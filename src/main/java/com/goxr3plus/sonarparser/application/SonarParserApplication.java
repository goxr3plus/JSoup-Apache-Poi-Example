package main.java.com.goxr3plus.sonarparser.application;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import main.java.com.goxr3plus.sonarparser.manager.ExcelManager;
import main.java.com.goxr3plus.sonarparser.manager.JSoupParser;
import main.java.com.goxr3plus.sonarparser.model.Project;

@SpringBootApplication(scanBasePackages = "main.java.com.goxr3plus.sonarparser")
public class SonarParserApplication implements CommandLineRunner {

    @Autowired
    private JSoupParser jSoupParser;

    @Autowired
    private ExcelManager excelManager;

    public static void main(final String[] args) {
	SpringApplication.run(SonarParserApplication.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {

	/* Read the HTML to get this week coverage */
	List<Project> projects = jSoupParser.readHTML();

	/* Export to Excel File */
	if (projects != null)
	    excelManager.exportExcel(projects, LocalDate.of(2019, 1, 18));

	System.exit(0);
    }

}
