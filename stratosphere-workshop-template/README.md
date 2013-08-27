# Stratosphere Workshop #

## Setup your local development environment ##

Install Java7 and maven3. You can use maven from the command line. Eclipse and IntelliJ have support for maven. Create a GitHub account. Fork this git project (so you have an exact copy of it in your github account afterwards) and clone it (making a local copy).

## First task ##

you have to update the pom.xml to the latest version. then run `mvn package` to get a jar file

check https://stratosphere.eu/wiki/doku.php/wiki:executepactprogram for details how to start it.

## Second task ##

We want to work with geographic data. Download http://download.geofabrik.de/europe/germany/bremen-latest.osm.pbf and run:
`export MAVEN_OPTS="-Xmx512m"` (you need to update your working copy, I fixed a bug at 10:30am)
`mvn -U clean compile exec:java -Dexec.mainClass="de.komoot.hackathon.PfbToJsonRecordsExporter" -Dexec.args="/path/to/bremen-latest.osm.pbf /targetdir/"`


Verification data can be found at http://dev.komoot.de/workshop/ The csv file contains all matches in the format: `"Way-id",<ignore>,"Area-id-1,"Area-id-2",...`


## Questions and Issues ##
  -  Q: maven cannot download stratosphere packages (eu.stratospere ...)
    -  A: check your $HOME/.m2/settings.xml: It should not contain a line `<mirrorOf>*</mirrorOf>` but `<mirrorOf>central</mirrorOf>`.
  - Q: I want to know more about Geometry Functions and Datatypes
    - A: Check out the Simple Feature Specification (http://en.wikipedia.org/wiki/Simple_Features) the popular postgresql extension PostGIS (http://postgis.net/docs/manual-2.1/) and the java implementation JTS (http://sourceforge.net/projects/jts-topo-suite/)

