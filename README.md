# Intro:
- Lucene for doc index
- Search query by Zillow envelopes against lucene
- Credit https://github.com/ahinz/scala-shapefile for parsing shp files

# To build project:
- sbt clean; sbt assembly

# To run:
- scala -J-Xmx6144m -cp target/scala-2.11/geosearch-assembly-0.0.1.jar geo.Main (take ~60mins to search all docs foreach ~17k neighbors)
- scala -J-Xmx6144m -cp target/scala-2.11/geosearch-assembly-0.0.1.jar geo.Main reindex (takes ~30 mins to index ~72000000 docs and take ~60mins to search all docs foreach ~17k neighbors)
