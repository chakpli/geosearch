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
    
    states.map(state => {
      println("searching for state:" + state)
      val p= Parser.parseShp("./zillow/ZillowNeighborhoods-" + state + "/ZillowNeighborhoods-" + state + ".shp")
      p.zipWithIndex.map {
        case (v, i) =>
          val ids = s.searchBBoxAndGetDocID(v)
          report.addIds(ids)
          report.incrCountMap(state + i, ids.length)
          //println("searched for city in " + state + i + " contains doc size:" + ids.length)
      }
    })
    
    println("Rank the top 10 neighborhoods with most businesses")
    report.getCountMap.toList.sortWith(_._2 > _._2).slice(0,10).map(i => println("city:" + i._1 + " doc count:" + i._2))
 
    println("Number of businesses that are not bound to a neighborhood=" + (s.getNumDoc - report.getIds.size) + " out of" + s.getNumDoc) 
  }
}