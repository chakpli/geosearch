# Intro:
- Lucene for doc index
- Search query by Zillow envelopes against lucene
- Credit https://github.com/ahinz/scala-shapefile for parsing shp files

# To build project:
- sbt clean; sbt assembly

# To run:
- scala -J-Xmx1024m -cp target/scala-2.11/geosearch-assembly-0.0.1.jar geo.Main
- scala -J-Xmx1024m -cp target/scala-2.11/geosearch-assembly-0.0.1.jar geo.Main reindex (takes ~30 mins to index >70000000 docs)
