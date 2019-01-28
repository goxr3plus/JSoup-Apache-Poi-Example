package main.java.com.goxr3plus.sonarparser.comporator;

import java.util.Comparator;

import main.java.com.goxr3plus.sonarparser.model.Project;

public class NameComporator implements Comparator<Project> {

    @Override
    public int compare(final Project o1, final Project o2) {
	return o1.getName().compareTo(o2.getName());
    }

}
