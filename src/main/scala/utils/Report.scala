package utils

import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet

/**
 * @author bli
 * @date Oct 1, 2017
 */
 
case class Report() {
  private val countMap = new HashMap[String, Int]()
  private val docIds = new HashSet[String]()
  
  def getCountMap:Map[String,Int] = countMap.toMap
  def getIds:Set[String] = docIds.toSet
  
  def incrCountMap(k:String, v:Int) = synchronized{
    countMap.get(k) match{ 
      case None => countMap.put(k, v)
      case Some(_k)=> countMap.put(k, _k + v)}
  }
  
  def addIds(s:Seq[String]) = synchronized{
    s.map(_s=>docIds.add(_s))
  }
}