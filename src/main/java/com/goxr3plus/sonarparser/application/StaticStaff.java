package main.java.com.goxr3plus.sonarparser.application;

import java.util.Arrays;
import java.util.List;

import dnl.utils.text.table.TextTable;
import main.java.com.goxr3plus.sonarparser.model.Project;

public class StaticStaff {
    
    public static void printToConsole(final List<Project> projects) {
	
	/* Prepare the printing table */
	String[] columnNames = { "Counting", "Name", "Coverage", "Last Updated", "Version" };
	int totalRows = projects.size();
	String[][] items = new String[totalRows][columnNames.length];
	int row = 0;
	int counter = 0;
	
	//Create an array to print the results as a table
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
    }

    public static final List<String> ignoreList = Arrays.asList(
	    "Auditing",
	    "Lottery Validations",
	    "Winning Certificates UI",
	    "Auth UI",
	    "Promotion Engine UI",
	    "ATS Service",
	    "NAM",
	    "Alerting",
	    "EJK Adaptor",
	    "Game Management Service",
	    "IGMS Service",
	    "Messaging",
	    "Promotion Engine",
	    "Messaging UI",
	    "Alerting UI",
	    "Lottery UI",
	    "IGMS UI",
	    "ATS UI",
	    "Cluster Management UI",
	    "Terminal Transactions UI",
	    "Fraud UI",
	    "Lottery Risk Management",
	    "Terminal Adaptor",
	    "Accounting",
	    "API Gateway J",
	    "Auth",
	    "Game Scheduler",
	    "Information Store",
	    "Fraud", 
	    "DMS UI",
	    "Pulse UI",
	    "DMS", 
	    "Auditing UI");
    
//    public static final Object[][] datatypes =
//	    { 
//		{ "APIs            ", "Auditing               " }, 
//		{ "APIs            ", "Lottery Validations    " },
//		{ "Users Operators ", "Winning Certificates UI" },
//		{ "Users Operators ", "Auth UI                " },
//		{ "Users Operators ", "Promotion Engine UI    " },
//		{ "APIs            ", "ATS Service            " },
//		{ "APIs            ", "NAM                    " },
//		{ "APIs            ", "Alerting               " },
//		{ "APIs            ", "EJK adaptor            " },
//		{ "APIs            ", "Game Management Service" },
//		{ "APIs            ", "IGMS Service           " },
//		{ "APIs            ", "Messaging              " },
//		{ "APIs            ", "Promotion Engine       " },
//		{ "Users Operators ", "Messaging UI           " },
//		{ "Users Operators ", "Alerting UI            " },
//		{ "Users Operators ", "Lottery UI             " },
//		{ "Users Operators ", "IGMS UI                " },
//		{ "Users Operators ", "ATS UI                 " },
//		{ "Users Operators ", "Cluster Management UI  " },
//		{ "Users Operators ", "Terminal Transactions UI" },
//		{ "Users Operators ", "Fraud UI               " },
//		{ "Users Operators ", "Lottery Risk Management" },
//		{ "Terminals & User", "Terminal Adaptor       " },
//		{ "APIs            ", "Accounting             " },
//		{ "APIs            ", "API Gateway J          " },
//		{ "APIs            ", "Auth                   " },
//		{ "APIs            ", "Game Scheduler         " },
//		{ "APIs            ", "Information Store      " },
//		{ "APIs            ", "Fraud                  " },
//		{ "Users Operators ", "Auditing UI            " },
//		{ "Users Operators ", "DMS UI                 " },
//		{ "Users Operators ", "Pulse UI               " },
//		{ "APIs            ", "DMS                    " },
//	     };
    
    


}
