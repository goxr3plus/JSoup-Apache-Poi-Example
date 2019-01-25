package main.java.project.sonarparser.application.measures;

import java.util.Arrays;
import java.util.List;

public class StaticStaff {

    public static final List<String> ignoreList = Arrays.asList(
	    "Auditing",
	    "Lottery Validations",
	    "Winning Certificates UI",
	    "Auth UI", "Promotion Engine UI",
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
	    "Accounting", "API Gateway J",
	    "Auth",
	    "Game Scheduler",
	    "Information Store",
	    "Fraud", 
	    "Auditing UI",
	    "DMS UI",
	    "Pulse UI",
	    "DMS", 
	    "Auditing UI");
    
    public static final Object[][] datatypes =
	    { 
		{ "APIs            ", "Auditing               " }, 
		{ "APIs            ", "Lottery Validations    " },
		{ "Users Operators ", "Winning Certificates UI" },
		{ "Users Operators ", "Auth UI                " },
		{ "Users Operators ", "Promotion Engine UI    " },
		{ "APIs            ", "ATS Service            " },
		{ "APIs            ", "NAM                    " },
		{ "APIs            ", "Alerting               " },
		{ "APIs            ", "EJK adaptor            " },
		{ "APIs            ", "Game Management Service" },
		{ "APIs            ", "IGMS Service           " },
		{ "APIs            ", "Messaging              " },
		{ "APIs            ", "Promotion Engine       " },
		{ "Users Operators ", "Messaging UI           " },
		{ "Users Operators ", "Alerting UI            " },
		{ "Users Operators ", "Lottery UI             " },
		{ "Users Operators ", "IGMS UI                " },
		{ "Users Operators ", "ATS UI                 " },
		{ "Users Operators ", "Cluster Management UI  " },
		{ "Users Operators ", "Terminal Transactions U" },
		{ "Users Operators ", "Fraud UI               " },
		{ "Users Operators ", "Lottery Risk Management" },
		{ "Terminals & User", "Terminal Adaptor       " },
		{ "APIs            ", "Accounting             " },
		{ "APIs            ", "API Gateway J          " },
		{ "APIs            ", "Auth                   " },
		{ "APIs            ", "Game Scheduler         " },
		{ "APIs            ", "Information Store      " },
		{ "APIs            ", "Fraud                  " },
		{ "Users Operators ", "Auditing UI            " },
		{ "Users Operators ", "DMS UI                 " },
		{ "Users Operators ", "Pulse UI               " },
		{ "APIs            ", "DMS                    " },
		{ "Users Operators ", "Auditing UI            " }
	     };

}
