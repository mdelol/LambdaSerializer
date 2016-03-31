package org.bwsw.lambdaserializer

import java.net.URL

/**
 * Created by krickiy_sp on 28.03.16.
 */
object Mainn {
  def main(args: Array[String]) = {
    lambdas.load()
  }
}

object lambdas {
  /*def save() = {
    val f = (x: Double) => x + 100;
    Serializer.serialize(f, "/home/krickiy_sp/lambda.lbd")
  }*/

  def load() = {
    val doubleToDouble = Serializer.deserialize[Double, Double]("/home/krickiy_sp/lambda.lbd")
    println(doubleToDouble(11))
  }
}