# Retired Galago [![maven](https://github.com/jjfiv/retired-galago/actions/workflows/maven.yml/badge.svg)](https://github.com/jjfiv/retired-galago/actions/workflows/maven.yml) [![Jitpack.io](https://jitpack.io/v/jjfiv/retired-galago.svg)](https://jitpack.io/#jjfiv/retired-galago)

This version of Galago is kept around for determining backwards-compatibility with Galago indexes and scoring. It supports no new features or development. 

- Extraneous dependencies have been removed!
- ***That means no HTML/Web server.*** This is probably the most controversial feature I've removed, but the old versions of Jetty are not getting any safer. Make your own little frontend with something quick & modern like [Javalin.io](https://javalin.io/).
- No Wiki Parsing (also removed 3!!! XML libraries).
- No Stanford NLP. (Saved you 4GiB of downloads!)
- No galago typebuilder.
- No new tupleflow pipelines.

## Using it

You can also get instructions from the [jitpack.io page](https://jitpack.io/#jjfiv/retired-galago).

1. Add the jitpack.io repo:

```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

2. Add this as a dependency:

```xml
	<dependency>
	    <groupId>com.github.jjfiv</groupId>
	    <artifactId>retired-galago</artifactId>
	    <version>2021.04</version>
	</dependency>
```

## License

Retired-Galago is distributed under the BSD license, like the [original Lemur Project's Galago](https://lemurproject.org/galago). See the LICENSE file for details.

