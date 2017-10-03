package geo

import java.io.File
import java.net.URL

import com.vividsolutions.jts.shape.GeometricShapeBuilder
import com.vividsolutions.jts.geom._

import geotrellis.shapefile
import geotrellis.shapefile.`package`.Record
import utils.Parser
import scalax.file.Path
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListMap
import scala.collection.mutable.HashSet
import utils.Report
import java.util.concurrent.TimeUnit

/**
 * @author bli
 * @date Oct 1, 2017
 */
 
object Main {
  
  val states = Array("AK","AL","AR","AZ","CA","CO","CT",
      "DC","DE","FL","GA","HI","IA","ID","IL","IN","KS",
      "KY","LA","MA","MD","ME","MI","MN","MO","MS","MT",
      "NC","ND","NE","NH","NJ","NM","NV","NY","OH","OK",
      "OR","PA","PR","RI","SC","SD","TN","TX","UT","VA",
      "VT","WA","WI","WV")
      
  def main(arg: Array[String]):Unit = {
    
    val report = Report()
    
    val indexPath = "./geo_spatial_index"
    val s = arg.length match {
      case l if l > 0 =>
        println("reindexing...")
        val path = Path.fromString(indexPath)
        path.deleteRecursively(continueOnFailure = false)
        val _s = new SpatialIndexer(indexPath)
        _s.indexDocuments(Parser.parseData("./business_dataset.json"))
        _s.setSearchIndexPath(indexPath)
        _s
      case _ =>
        println("will not reindex...")
        val _s = new SpatialIndexer(indexPath)
        _s.setSearchIndexPath(indexPath)
        _s
    }
    
    val pool = java.util.concurrent.Executors.newFixedThreadPool(8)

    states.map(state => {
      println("searching for state:" + state)
      val p = Parser.parseShp("./zillow/ZillowNeighborhoods-" + state + "/ZillowNeighborhoods-" + state + ".shp")
      p.zipWithIndex.map {
        case (record, i) =>
          val t = SearchRunnable(record, s, report, state+i)
          pool.execute(t)
      }
    })
    
    pool.shutdown
    while (!pool.isTerminated()){ Thread.sleep(2000)}
    
    println("Rank the top 10 neighborhoods out of " + report.getCountMap.size + " with most businesses")
    report.getCountMap.toList.sortWith(_._2 > _._2).slice(0, 10).map(i => println("city:" + i._1 + " doc count:" + i._2))
 
    println("Number of businesses that are not bound to a neighborhood=" + (s.getNumDoc - report.getIds.size) + " out of" + s.getNumDoc) 
  }
}