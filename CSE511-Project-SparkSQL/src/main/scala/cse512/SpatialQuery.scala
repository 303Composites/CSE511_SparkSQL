package cse512

import org.apache.spark.sql.SparkSession

object SpatialQuery extends App{
  def runRangeQuery(spark: SparkSession, arg1: String, arg2: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Contains",(queryRectangle:String, pointString:String)=> {
      val rect = queryRectangle.split(",")
      val point = pointString.split(",")
      if ( point(0).toDouble >= rect(0).toDouble && point(0).toDouble <= rect(2).toDouble
        && point(1).toDouble >= rect(1).toDouble && point(1).toDouble <= rect(3).toDouble ) {
        true
      }
      else{
        false
      }

    }
    )

    val resultDf = spark.sql("select * from point where ST_Contains('"+arg2+"',point._c0)")
    resultDf.show()

    return resultDf.count()

  }

  def runRangeJoinQuery(spark: SparkSession, arg1: String, arg2: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    val rectangleDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg2);
    rectangleDf.createOrReplaceTempView("rectangle")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Contains",(queryRectangle:String, pointString:String)=> {
      val rect = queryRectangle.split(",")
      val point = pointString.split(",")
      if ( point(0).toDouble >= rect(0).toDouble && point(0).toDouble <= rect(2).toDouble
        && point(1).toDouble >= rect(1).toDouble && point(1).toDouble <= rect(3).toDouble ) {
        true
      }
      else{
        false
      }

    }
    )

    val resultDf = spark.sql("select * from rectangle,point where ST_Contains(rectangle._c0,point._c0)")
    resultDf.show()

    return resultDf.count()
  }

  def runDistanceQuery(spark: SparkSession, arg1: String, arg2: String, arg3: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Within",(pointString1:String, pointString2:String, distance:Double)=> {
      val point1 = pointString1.split(",")
      val point2 = pointString2.split(",")
      if ( math.hypot( point1(0).toDouble - point2(0).toDouble, point1(1).toDouble - point2(1).toDouble ) <= distance ){
        true
      }
      else{
        false
      }

    }
    )

    val resultDf = spark.sql("select * from point where ST_Within(point._c0,'"+arg2+"',"+arg3+")")
    resultDf.show()

    return resultDf.count()
  }

  def runDistanceJoinQuery(spark: SparkSession, arg1: String, arg2: String, arg3: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point1")

    val pointDf2 = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg2);
    pointDf2.createOrReplaceTempView("point2")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Within",(pointString1:String, pointString2:String, distance:Double)=> {
      val point1 = pointString1.split(",")
      val point2 = pointString2.split(",")
      if ( math.hypot( point1(0).toDouble - point2(0).toDouble, point1(1).toDouble - point2(1).toDouble ) <= distance ){
        true
      }
      else{
        false
      }

    }
    )
    val resultDf = spark.sql("select * from point1 p1, point2 p2 where ST_Within(p1._c0, p2._c0, "+arg3+")")
    resultDf.show()

    return resultDf.count()
  }
}
