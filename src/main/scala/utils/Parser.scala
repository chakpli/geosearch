package utils

import scala.io.Source
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jvalue2extractable
import org.json4s.string2JsonInput
import com.vividsolutions.jts.geom.GeometryFactory
import geotrellis.shapefile.`package`.Record
import geotrellis.shapefile

case class Bio(bio: String)
case class Coordinate(latitude: Option[Double], longitude: Option[Double])
case class Location(coordinate:Coordinate)
case class Data(is_claimed: Boolean, attributes:Bio, id:Int, location:Location)

object Parser {
  
  def parseShp(path:String):Seq[Record] = {
    implicit val g: GeometryFactory = new GeometryFactory()
    shapefile.Parser(path)
  }
    
  def parseData(path:String):Iterator[Data] = {
    val data = Source.fromFile(path).getLines
    implicit val formats = DefaultFormats
    data.map(d=>parse(d).extract[Data])
  }
  
  def main(arg: Array[String]):Unit = {
    
    val datas = parseData("./business_dataset.json")
    
    //trigger
    datas.foreach(println(_))
  }
  
}