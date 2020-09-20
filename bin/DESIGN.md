# Class diagram

The code part consists of:

* 8 classes including 1 interface

* 1 class implements the interface

* 1 inheriting classes

* 1 class enum 

* 4 "classic" java classes

### The interface  « Converter »:
It has two abstract methods implemented by ConverterToCsv (convertFromHtml and convertFromWikitext).

### The class enum « ExtractType»:
This class defines html and wikitext values as a possible value for data extraction.

### The class « WikipediaMatrix »:
The Main create a WikipediaMatrix and determines the setters of setUrlsMatrix()* and setExtractType().  Depending on the type (HTML and Wikitext) declared in setExtractType(), we use getConvertResult(). 
The method getConvertConvert(), Depending onhis type and for each UrlMatrix uses the UrlMatrix* class  UrlMatrix  using the setter setFilesMatrix()* to return a set of UrlMatrix.

###### *setUrlsMatrix': Creates a set of UrlMatrix.

###### *UrlMatrix: obtained upstream by setUrlsMatrix.

###### *SetFilesMatrix: Use convertFromHtml or convertFromWikitext and return a set of FileMatrix.

### class  « UrlMatrix »:

The Main gets a Set of FileMatrix obtained using setFilesMatrix().

### class  « ConverterToCsv »:

Used by the setFilesMatrix () method through the Converter interface. We use one of two methods that returns a set of FileMatrix .
A piece of WikipediaMatrix class:"setFilesMatrix (converter.convertFromHtml (urlMatrix.getLink ()))"
 
convertFromHtml
we get the tables thanks to the url in parameter, if they are relevant (Isrelevant()*) and not imbedded (isNested()**) with another parent element of the HTML page, we start creating a new CSV file called FileMatrix using the convertHtmlTable method. It is composed of a header, a body and a footer. For each part, we use writeInCsv (),to help writing each section of the CSV file.

Priority Cell: 
A class modeling the so-called "priority cells". These cells are cells are located on more than one row and / or column. This class has rowspan and colspan attributes, which correspond to the numbers of cells and columns on which the cell is "spreads", as well as the row and col attributes that correspond to the row and column number of the cell.

convertFromWikitext
For a given url this method creates a Mediawikibot (automated tools that can be used to do tedious work or repetitive tasks related to a wiki.) Then check if it is relevant (Isrelevant ()*) and not nested (Isnested()**) with another element parent of the HTML page. Then start creating a new CSV file called "FileMatrix" using the convertHtmlTable method. It consists of a header, a body and a footer and for each part using writeInCsv (), For each part, we use writeInCsv (),to help writing each section of the CSV file.

→ the priorityCell interest is to avoid duplicate data in the CSV file, so instead of repeating the data, when the priorityCell has already been noted, only a comma is written instead of rewriting the data.

###### *isRelevant(): For a given array, check if the array has a class wikitable and not class box or nav.

###### **isNested(): For a given array, check if the array contains another array inside

###  class Csv extends FileMatrix


### class « FileMatrix »:  saveCsv

For each FileMatrix obtained using the UrlMatrix class I save the file using the method saveCsv (String csvPath).

# UML Diagram

<img align="center" src="/src/img/ipdl_uml.png">

# Sequence diagram

<img align="center" src="/src/img/sequence1.png">
<img align="center" src="/src/img/sequence2.png">

