Like all human work, the work of our predecessors last year was not perfect as our work will no doubt be this year as well.
We took over the project from our predecessors and we found a lot of anomalies that we somehow tried to correct which are:
long and sometimes poorly commented methodsthat are difficult to understand.
Some errors (in the tests) are due to inconsistencies in the tests and others are due to changes in the contents of wikipedia pages or urls which no longer exist or which no longer work.

we were able to correct our colleagues tests but some of our tests may not work as well next year because there may be urls that will no longer work for next year but to correct that you just have to display the urls that no longer work in the console and replace it in the related test,  but the tests will not fail because of the change of the contents of the wikipedia pages.

After running the tests on the 336 URLS, the execution time for the html extractor is 438 ms for 1620  csv files created and
the number of files created by the wikitext extractor is 1546  for an execution time of 346 ms.
we found that the html extractor creates more files than the wikitext extractor.
To reinforce the evaluation of the performance of the extractors we have recreated other tests such as testPerformanceExtractorHtml and testPerformanceExtractorWikiText which browse all the Csv files to find if there are empty csv files and according to these tests the extractors extract well tables in urls if there are any

For the tests (terrainverite) none passed because the content of the files contained in the verite folder was manually created by our colleagues from last year to compare with the files created by the extractor and since the content of the wikipedia pages change then the tests failed.
but we were able to rectify that by doing:
we created another test which allows to contact the wikipedia page first to make the csv files
and put them in the verite folder instead of creating these files manually as our colleagues did 
last year because the content wikipedia pages is brought changed and if this content changes then the "tesVerite Terrain" do not work, problem that we faced.