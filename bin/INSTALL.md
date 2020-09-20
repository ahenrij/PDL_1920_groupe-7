# Installation

## Prerequisites

- Java version 1.8

- IDE Java like Eclipse or IntelliJ

## Installation procedure

- Clone the project, URL=*https://github.com/manuc352/PDL_1920_groupe-7.git*;
- Install __maven__;
- In the class wikiMain, change the path at line 20 by your personal path to access your repertory inputdata;
- Same thing for the path at line 28 with the repertory output

### Maven Installation 

You have to install it with IntelliJ to give easier its using. Here is the approach with IntelliJ
- In the Project tool window, right-click the project folder and select New | Module. Alternatively, from the main menu, select File| New | Module to open the New Module wizard.
- If you used main menu to add a module then the process of adding a module is the same as Creating a new Maven project. 

You will find more information on the website of IntelliJ : https://www.jetbrains.com/help/idea/maven-support.html

## Before running

- If you don't change anything, the extractor will extract tables that are extracted from wikipedia pages which corresponds of the list of URL written in the file *wikiurls.txt* in the repertory *inputdata*. 
- If you want to run the extractor with only one URL, you have to replace links in *wikiurls.txt* by your URL.   

## After running 

- The system has created a file for each table in each wikipedia page. 
- These CSV files are registered in directory *output*. CSV files from the *wikitext* extractor are in the directory *wikitext*, and files from html extractor are in the directory *html*.

## Errors 

-  If you have a compilation warning of type : "release version 5 not supported", 
go to File> Settings > Build, execution, deployment > Java Compiler, in target bytecode version, write 1.8.   
