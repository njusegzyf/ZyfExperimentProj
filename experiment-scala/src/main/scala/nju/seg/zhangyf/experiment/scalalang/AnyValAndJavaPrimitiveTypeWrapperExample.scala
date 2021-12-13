package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
object AnyValAndJavaPrimitiveTypeWrapperExample {

  def demoAutoBoxingAndAutoUnboxing() : Unit = {

    // @note: Auto boxing and auto unboxing are done by implicit methods defined in `Predef`:
    //   implicit def boolean2Boolean(x: Boolean): java.lang.Boolean = x.asInstanceOf[java.lang.Boolean]
    //   implicit def Boolean2boolean(x: java.lang.Boolean): Boolean = x.asInstanceOf[Boolean]

    // auto boxing:
    val x : java.lang.Boolean = false

    // auto unboxing
    val xValue : Boolean = x
  }

  def demoNullConversion() : Unit = {

    // If an instance of Java primitive type points to null, then assigns it to an instance of `AnyVal`
    // will just make the instance of default value instance of throw an `NullPointerException`.
    // This is done by auto unboxing methods defined in `Predef` like:
    //   implicit def Boolean2boolean(x: java.lang.Boolean): Boolean = x.asInstanceOf[Boolean]

    // However, this differs from Java.
    // The following code in Java throws exception at runtime:
    //    final Integer x = null;
    //    final int xValue = x;
    // Exception in thread "main" java.lang.NullPointerException: Cannot invoke "java.lang.Integer.intValue()" because "x" is null

    val x: java.lang.Boolean = null
    val xValue: Boolean = x // `xValue` is false
    Predef.assert(!xValue)

    val i : java.lang.Integer = null
    val iValue : Int = i // `xValue` is false
    assert(iValue == 0)
  }

  def main(args: Array[String]): Unit = {
    this.demoAutoBoxingAndAutoUnboxing()
    this.demoNullConversion()
  }

}
