# Wikipedia Matrix The Truth

Wikipedia Matrix THE TRUTH is a table extractor via two ways : HTML and WIKITEXT.
the purpose of this project is to improve the first version of the project which has 
been done by the students of Master degree from last year.
It's goal is to extract as many relevant tables from wikipedia links.
the output format is csv.
After analysis of the program we have listed the following problems:
* difference between the results of the extraction methods 
Indeed, html extraction provides more tables than wikitext extraction.
* criteria for selecting relevant tables not refined

Like any project we have several differents version and the purpose of this it's to improve it, 
from version to version over time to make it more powerful and better than the others versions,
the current objective is to have a better extractor than the other versions.

## Getting Started
clone https://github.com/manuc352/PDL_1920_groupe-7 for development and testing purposes

## Prerequisites
#### For Users
* JRE >= 1.8
* java IDE  (Eclispe,Intelliji Idea etc..)
* [Maven](https://maven.apache.org/) - Dependency Management to get all dependencies of this project.
* for installing and testing we are inviting you to click on, this below link
[Install.md](https://github.com/manuc352/PDL_1920_groupe-7/blob/master/INSTALL.md)
* you will see A step by step series of examples that tell you how to install  and test this,after that
you can run the project for testing and extracting csv files from wikipedia pages which are 
available in the directory inputdat--> wikiurls.txt.

### For Developpers
After having those prerequisites above you should add these below
* [jsoup](https://jsoup.org) is a Java library for working with real-world HTML. 
It provides a very convenient API for extracting and manipulating data, 
using the best of DOM, CSS, and jquery-like methods.
* [bliki](http://www.dropwizard.io/1.0.2/docs/) -  java parser library for converting Wikipedia wikitext notation to HTML.
* [Maven](https://maven.apache.org/) 
- Apache maven is a software project management and comprehension tool
Maven’s primary goal is to allow a developer to comprehend the complete state of a development effort in the shortest period of time. 
In order to attain this goal, there are several areas of concern that Maven attempts to deal with:
* Making the build process easy
* Providing a uniform build system and quality project information
* Providing guidelines for best practices development
* Allowing transparent migration to new features

you will find more informations to increase your comprehension of the project when you will read the below files
[Install.md](https://github.com/manuc352/PDL_1920_groupe-7/blob/master/INSTALL.md) and
[Design.md](https://github.com/manuc352/PDL_1920_groupe-7/blob/master/DESIGN.md)

## Functionalities of  the application
#### the functionalities which were implemented by the first group to work on the project
* extraction of csv files through tables from wikipedia pages whose urls are in the wikiurls.txt file of the inputdata directory
* implementation of some tests to verify a good extraction 

#### Future functionalities 
* statistics on extracted files and tables not taken into account according to the selection criteria of the tables to be extracted
* automatic testing of file extraction quality , these tests will show also the  weaknesses of the extractor.

## Deployment
run mvn package this Build the project to generate the artefac.

## Versioning
For the versions available, see the [tags on this repository](https://github.com/manuc352/PDL_1920_groupe-7/releases). 

## Built With
* [bliki](http://www.dropwizard.io/1.0.2/docs/) -  java parser library for converting Wikipedia wikitext notation to HTML.
* [jsoup](https://jsoup.org/) -Java library for working with real-world HTML.
* [Maven](https://maven.apache.org/) - Dependency Management.

## Authors
As we have already said it this project has been developed by those students
* Jocelin DEGNI
* Yann ATTOUBE
* Anderson KONAN
* Kiko DAGNOGO

now it's our turn to improve their work 

* Emmanuel CHAUVEL 
* Narcisse KOUADIO
* Oceane THELISMA
* Noussi AMAL
* Karima GRAMI
