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

	@Value("${files.printToConsole}")
	private boolean printToConsole;

	/**
	 * Parse the html table
	 * 
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
					String text = el.text().replaceAll("master|develop|L10|NG", "").replace("Java", "J").trim();

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

			/* Fix problems */
			projects.stream().filter(project -> project.getName().equals("Lottery Application")).findFirst()
					.ifPresent(project -> project.setName("Lottery UI"));
			projects.stream().filter(project -> project.getName().equals("Cluster Management")).findFirst()
					.ifPresent(project -> project.setName("Cluster Management UI"));

			/* Get only the projects we want */
			projects = projects.stream().filter(project -> StaticStaff.includedProjects.contains(project.getName()))
					.map(project -> {
						project.setVersion(
								project.getVersion().replaceAll("\\Q.\\E|LVS_|-|not provided|SNAPSHOT", "").trim());
						return project;
					}).collect(Collectors.toList());

			/* Create a multimap because of multiple projects with same name */
			final ImmutableListMultimap<String, Project> multiMap = Multimaps.index(projects, Project::getName);
			projects = multiMap.keySet().stream().map(projectName -> {
				List<Project> sortedProjects = multiMap.get(projectName).stream().sorted().collect(Collectors.toList());
				if (projectName.equalsIgnoreCase("Lottery Validations")) // Due to 2 different versions
					return sortedProjects.get(0);
				else
					return sortedProjects.get(0);
			}).collect(Collectors.toList());

			/* Add projects that never had coverage */
			List<String> projectNames = projects.stream().map(Project::getName).collect(Collectors.toList());
			for (String projectName : StaticStaff.includedProjects) {
				if (!projectNames.contains(projectName)) {
					projects.add(new Project(projectName, "not provided", "No Coverage", ""));
				}
			}

			/* Set Project Category */
			projects.forEach(project -> {
				String name = project.getName();
				if (name.contains("UI"))
					project.setCategory("Users Operators");
				else if (name.equalsIgnoreCase("Terminal Adaptor"))
					project.setCategory("Terminals & Users");
				else
					project.setCategory("APIs");
			});

			/* Print to console for debugging purposes */
			if (printToConsole)
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
