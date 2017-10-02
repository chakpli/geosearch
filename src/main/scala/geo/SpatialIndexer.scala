package geo


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;

import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.distance.DistanceUtils;
import com.spatial4j.core.shape.Point;
import com.spatial4j.core.shape.Shape;
import java.io.File
import utils.Data
import geotrellis.shapefile.`package`.Record


/**
 * @author bli
 * @date Oct 1, 2017
 */
 
case class SpatialIndexer(indexPath: String) {

  private var indexReader: IndexReader = null
  private var searcher: IndexSearcher = null

  val a = new StandardAnalyzer(Version.LUCENE_4_10_4)
  val iwc = new IndexWriterConfig(Version.LUCENE_4_10_4, a)

  val directory = new SimpleFSDirectory(new File(indexPath))
  val indexWriter = new IndexWriter(directory, iwc)

  val ctx = SpatialContext.GEO

  val grid = new GeohashPrefixTree(ctx, 11)
  val strategy = new RecursivePrefixTreeStrategy(grid, "location")

  def getNumDoc: Int = {
    indexReader.numDocs
  }
  
  def getDocument(scoreDoc: ScoreDoc):Document = {
    searcher.doc(scoreDoc.doc)
  }
  
  def newGeoDocument(id: Int, name: String, shape: Shape): Document = {

    val ft = new FieldType()
    ft.setIndexed(true)
    ft.setStored(true)

    val doc = new Document()

    doc.add(new IntField("id", id, Store.YES));
    doc.add(new Field("name", name, ft));
    for (f <- strategy.createIndexableFields(shape)) {
      doc.add(f)
    }

    doc.add(new StoredField(strategy.getFieldName(), ctx.toString(shape)))

    doc
  }

  def indexDocuments(datas: Iterator[Data]) {
    datas.zipWithIndex.foreach(i => {
      val c = i._1.location.coordinate
      val id = i._1.id
      (c.latitude, c.longitude) match {
        case (Some(lt), Some(lg)) =>
          if (Math.abs(lt) <= 90.0 && Math.abs(lg) <= 180.0)
            indexWriter.addDocument(newGeoDocument(id, id.toString, ctx.makePoint(lg, lt)))
          else
            println("not index" + i._2)
        case (_, _) => println("not index" + i._2)
      }
    })
    
    indexWriter.commit();
    indexWriter.close();
  }

  def setSearchIndexPath(indexPath: String): Unit = {
    indexReader = DirectoryReader.open(new SimpleFSDirectory(new File(indexPath)))
    searcher = new IndexSearcher(indexReader)
  }

  def searchBBoxAndGetDocID(r:Record):Array[String] ={
    val en = r.g.getEnvelopeInternal
    val docs = searchBBox(en.getMinX, en.getMinY, en.getMaxX, en.getMaxY)
    docs.map(d => getDocument(d).get("id"))
  }
  
  def searchBBox(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double):Array[ScoreDoc] = {

    val sargs = new SpatialArgs(SpatialOperation.IsWithin, ctx.makeRectangle(minLat, maxLat, minLng, maxLng));

    val filter = strategy.makeFilter(sargs)
    val limit = Int.MaxValue
    val topDocs = searcher.search(new MatchAllDocsQuery(), filter,limit)

    val scoreDocs = topDocs.scoreDocs
    /*
    for (s <- scoreDocs) {
      val doc = searcher.doc(s.doc);
      System.out.println("found" + doc.get("id") + "\t" + doc.get("name"));
    }*/
    
    scoreDocs
  }
  

}