# Metget

## WHY
A boring rainy saturday afternoon, passion for flight simulation and programming, slightly interested in looking at worldwide meteo condition directly from the shell.

## WHAT
A METAR retriever written in Java 

## WHAT the hell is a METAR?
METAR stands for METeorological Aerodrome Report, see [wikipedia for more](http://en.wikipedia.org/wiki/METAR "wikipedia")

## COMPILE
Install [maven](http://maven.apache.org/download.html "maven"), (Tried to compile on a Macosx Leopard machine [maven version 3.0.3 - java 1.6] and on a debian 6 machine [maven version 2.2.1 - java 1.7.0_03], all ok). 
Of course you need a JDK installed on your system.
After maven installation, go inside the metget dir and run this command:

```bash
mvn assembly:assembly
```

Maven will start to download *stroke* the full internet *stroke* lot of things (don't worry), at the end you'll find a compiled jar - including dependencies - inside project target directory.

## USE
```bash
java -jar metgetter-jar-with-dependencies.jar Alghero 'New york'
```

Will search for ICAO codes of Alghero and New York airports, if found, will download METARs.

## NOTES
ICAO codes are searched querying [this site](http://www.airlinecodes.co.uk "airlinecodes"). Doesn't know if it will work forever. Drop me a line if stops working. METARs are downloaded from NOAA web site. For more info you could check [this page](http://www.nws.noaa.gov/tg/datahelp.html "NOAA").

(Obviously this software doesn't pretend to be bugfree or -whoa- perfect, keep in mind that is been written for fun.)
