package main.java.com.goxr3plus.sonarparser.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

import main.java.com.goxr3plus.sonarparser.application.StaticStaff;
import main.java.com.goxr3plus.sonarparser.model.Project;


@Component
public class JSoupParser extends AbstractManager {

    @Value("${files.sonarQubePath}")
    private String sonarQubePath;

    /**
     * Parse the html table
     * 
     * @param table
     * @throws IOException
     */
    public List<Project> readHTML() throws IOException {

	log.info("Entered readHTML");

	try {
	    File input = new File(sonarQubePath);
	    Document doc = Jsoup.parse(input, "UTF-8", "http://example.com/");

	    // Parse the damn html
	    Element table = doc.getElementById("measures-table").getElementsByTag("tbody").first();

	    int counter = 0;
	    List<Project> projects = new ArrayList<>();
	    for (Element element : table.getElementsByTag("tr")) {
		int skipColumns = 0;
		Project project = new Project();
		for (Element el : element.getElementsByTag("td")) {

		    // Replace unwanted words from the final text
		    String text = el.text().replaceAll("master|develop|L10", "").replace("Java", "J").trim();

		    // Skip first column
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

	    /* Create a multimap because of multiple projects with same name */
	    final ImmutableListMultimap<String, Project> multiMap = Multimaps.index(projects, Project::getName);
	    projects = multiMap.keySet().stream().map(projectName -> {
		List<Project> sortedProjects = multiMap.get(projectName).stream().sorted().collect(Collectors.toList());
		return sortedProjects.get(0);
	    }).collect(Collectors.toList());

	    /* Add projects that never had coverage */
	    List<String> projectNames = projects.stream().map(Project::getName).collect(Collectors.toList());
	    for (String projectName : StaticStaff.ignoreList) {
		if (!projectNames.contains(projectName)) {
		    projects.add(new Project(projectName, "not provided", "No Coverage", ""));
		}
	    }

	    /* Print to console for debugging purposes */
	    StaticStaff.printToConsole(projects);

	    return projects;

	} catch (IOException e) {
	    e.printStackTrace();
	    log.info("Error in readHTML --{}", e.getMessage());
	}

	log.info("Exiting readHTML---Failed");

	return null;
    }
}
