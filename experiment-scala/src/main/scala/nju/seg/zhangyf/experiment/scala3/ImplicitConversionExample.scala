package nju.seg.zhangyf.experiment.scala3

import javax.annotation.ParametersAreNonnullByDefault

import nju.seg.zhangyf.experiment.scala3.typesystem.ContextualAbstractionExample.Comparator

/**
 * @see [[https://docs.scala-lang.org/scala3/book/ca-implicit-conversions.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object ImplicitConversionExample {

  given Conversion[String, Int] with {
    override def apply(s: String): Int = Integer.parseInt(s)
  }

  // `Conversion` is a `FunctionalInterface` so a function object can be convert to a `Conversion`
  given Conversion[Int, String | Null] = { String.valueOf(_) }

  import scala.language.implicitConversions

  // a method that expects an Int
  def plus1(i: Int): Int = i + 1

  // pass it a String that converts to an Int
  plus1("1")

  // The Predef package contains “auto-boxing” conversions that map primitive number types to subclasses of java.lang.Number.
  // For instance, the conversion from Int to java.lang.Integer can be defined as follows:
  given intToInteger: Conversion[Int, java.lang.Integer | Null] = java.lang.Integer.valueOf(_)

  // previous implementation: implicit method
  implicit def intToInterger2(a: Int): Integer | Null = java.lang.Integer.valueOf(a)

}
