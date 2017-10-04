# Intro:
- Lucene for doc index (lat and long)
- Search queries of Zillow envelopes with Lucene
- Credit https://github.com/ahinz/scala-shapefile for parsing shp files
- use is_claimed businesses only

# To build project:
- sbt clean; sbt assembly

# To run:
- scala -J-Xmx6144m -cp target/scala-2.11/geosearch-assembly-0.0.1.jar geo.Main (take ~6mins to search all docs forall ~17k neighbors)
- scala -J-Xmx6144m -cp target/scala-2.11/geosearch-assembly-0.0.1.jar geo.Main reindex (takes ~15 mins to index ~7200000 docs and ~6mins to search all docs forall ~17k neighbors)
