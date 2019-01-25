package main.java.project.sonarparser.application.measures;

public class Project {

    private String name;
    private String version;
    private String loc;
    private String bugs;
    private String codeSmells;
    private String coverage;
    private String lastAnalysis;
    private boolean exists;

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

    public String getLoc() {
	return loc;
    }

    public void setLoc(final String loc) {
	this.loc = loc;
    }

    public String getBugs() {
	return bugs;
    }

    public void setBugs(final String bugs) {
	this.bugs = bugs;
    }

    public String getCodeSmells() {
	return codeSmells;
    }

    public void setCodeSmells(final String codeSmells) {
	this.codeSmells = codeSmells;
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

    public boolean isExists() {
	return exists;
    }

    public void setExists(final boolean exists) {
	this.exists = exists;
    }
    
    

}
