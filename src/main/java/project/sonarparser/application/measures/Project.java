package main.java.project.sonarparser.application.measures;

public class Project implements Comparable<Project> {

    private String name;
    private String version;
    private String coverage;
    private String lastAnalysis;

    public Project() {
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
	return coverage;
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

    @Override
    public String toString() {
	return "Project [name=" + name + ", version=" + version + ", coverage=" + coverage + ", lastAnalysis=" + lastAnalysis + "]";
    }

    @Override
    public int compareTo(final Project project2) {
	if (project2.getCoverage() == null && getCoverage() != null)
	    return 1;
	if (!project2.getVersion().isEmpty() && !getVersion().isEmpty())
	    return Integer.valueOf(project2.getVersion()).compareTo(Integer.valueOf(getVersion()));
	return 0;
    }

}
