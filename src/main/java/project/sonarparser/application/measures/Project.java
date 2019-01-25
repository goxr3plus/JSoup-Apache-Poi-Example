package main.java.project.sonarparser.application.measures;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;

public class Project implements Comparable<Project> {

    private String name;
    private String version;
    private String coverage;
    private String lastAnalysis;

    public Project() {
    }

    public Project(final String name, final String version, final String coverage, final String lastAnalysis) {
	super();
	this.name = name;
	this.version = version;
	this.coverage = coverage;
	this.lastAnalysis = lastAnalysis;
    }

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public String getVersion() {
	return version;
    }

    public void setVersion(final String version) {
	this.version = version;
    }

    public String getCoverage() {
	if (coverage.contains("No Coverage"))
	    return coverage;
	else {
	    NumberFormat formatter = new DecimalFormat("#0.00");
	    return formatter.format(getCoverageAsDouble()).concat("%");
	}
    }

    public void setCoverage(final String coverage) {
	this.coverage = coverage;
    }

    public String getLastAnalysis() {
	return lastAnalysis;
    }

    public void setLastAnalysis(final String lastAnalysis) {
	this.lastAnalysis = lastAnalysis;
    }

    public Double getCoverageAsDouble() {
	if (coverage != null && !coverage.isEmpty() && !coverage.equals("") && !coverage.equals("No Coverage"))
	    return Double.valueOf(coverage.replace("%", ""));
	return 0.00;
    }

    public LocalDate getLastAnalysisLocalDate() {

	if (lastAnalysis.contains(":"))
	    return LocalDate.now();

	String[] splitter = lastAnalysis.split(" ");

	int year = Integer.parseInt(splitter[2]);
	int month = getMonthInt(splitter[0]);
	int dayOfMonth = Integer.parseInt(splitter[1]);

	return LocalDate.of(year, month, dayOfMonth);
    }

    /**
     * Return month as int from a String like {Sept,Oct,Jan,etc}
     * 
     * @param monthFormalabbreviation
     * @return
     */
    private int getMonthInt(final String monthFormalabbreviation) {
	String[] shortMonths = new DateFormatSymbols().getShortMonths();

	for (int i = 0; i < (shortMonths.length - 1); i++) {
	    String shortMonth = shortMonths[i];
	    if (shortMonth.equals(monthFormalabbreviation))
		return i + 1;
	}
	return 0;
    }

    @Override
    public String toString() {
	return "Project [name=" + name + ", version=" + version + ", coverage=" + coverage + ", lastAnalysis=" + lastAnalysis + "]";
    }

    @Override
    public int compareTo(final Project project2) {
	return project2.getLastAnalysisLocalDate().compareTo(getLastAnalysisLocalDate());
    }

}
