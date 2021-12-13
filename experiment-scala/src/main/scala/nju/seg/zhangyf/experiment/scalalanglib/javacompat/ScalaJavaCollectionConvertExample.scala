package nju.seg.zhangyf.experiment.scalalanglib.javacompat

import scala.language.unsafeNulls
import scala.collection.mutable

import java.util
import javax.annotation.ParametersAreNonnullByDefault

import com.google.common.collect.Lists

// import org.scalatest.matchers.should.Matchers

/**
  * @see [[scala.collection.JavaConverters]]
  * @see [[scala.collection.convert.ImplicitConversions]]
  *
  * @author Zhang Yifan
  */
//noinspection ScalaUnusedSymbol,ScalaDeprecation
@ParametersAreNonnullByDefault
@deprecated(message = "Use `scala.jdk.CollectionConverters` instead.")
private object ScalaJavaCollectionConvertExample extends App { // with Matchers {

  def demoCollectionConverters(): Unit = {

    import scala.jdk.CollectionConverters.ListHasAsScala
    val javaList: util.List[String] = new util.ArrayList[String]()
    val scalaList: mutable.Buffer[String] = javaList.asScala

    import scala.jdk.CollectionConverters.BufferHasAsJava
    val javaList2: util.List[String] = scalaList.asJava
  }

  /** Uses decorators that enable converting between Scala and Java collections using extension methods, asScala and asJava.
    *
    * @see [[scala.jdk.CollectionConverters]], new in Scala 2.13
    * @see [[scala.collection.convert.AsJavaConverters]]
    * @see [[scala.collection.convert.AsScalaConverters]]
    * @see [[scala.collection.JavaConverters]], deprecated in Scala 2.13
    */
  def demoDeprecatedCollectionConverters(): Unit = {
    // `DecorateAsJava` defines asJava extension methods for JavaConverters.
    // `DecorateAsScala` defines asScala extension methods for JavaConverters.

    val scalaBuffer: mutable.Buffer[String] = mutable.Buffer("Str")
    val javaList: util.List[String] = Lists.newArrayList("Str")

    // 1. `DecorateAsJava` extends `AsJavaConverters`, which defines methods like `bufferAsJavaList`
    import scala.collection.JavaConverters.bufferAsJavaList
    val javaList2: util.List[String] = bufferAsJavaList(scalaBuffer)
    // javaList2 shouldBe javaList

    // 2. `DecorateAsJava`  defines methods like `bufferAsJavaListConverter`,
    // which can implicit convert a Scala collection to a convert adapter, which has a `asJava` method to returns a related java collection type.
    import scala.collection.JavaConverters.bufferAsJavaListConverter
    val javaList3: util.List[String] = scalaBuffer.asJava // bufferAsJavaListConverter(scalaBuffer).asJava
    // javaList3 shouldBe javaList

    // 3. `DecorateAsScala` and `AsScalaConverters` are similar to `DecorateAsJava` and `AsJavaConverters`
    import scala.collection.JavaConverters.asScalaBufferConverter
    val scalaBuffer2: mutable.Buffer[String] = javaList.asScala // asScalaBufferConverter(javaList).asScala
    // scalaBuffer2 shouldBe scalaBuffer
  }

  /**
    * @see [[scala.collection.convert.ImplicitConversions]]
    * @see [[scala.collection.convert.ImplicitConversionsToJava]]
    * @see [[scala.collection.convert.ImplicitConversionsToScala]]
    */
  def demoImplicitConversions(): Unit = {
    // `ImplicitConversions` defines implicit conversions for collections types like `Buffer <-> util.List`.
    val scalaBuffer: mutable.Buffer[String] = mutable.Buffer("Str")
    val javaList: util.List[String] = Lists.newArrayList("Str")

    import scala.collection.convert.ImplicitConversionsToScala.`list asScalaBuffer`
    val scalaBuffer2: mutable.Buffer[String] = javaList // `list asScalaBuffer`(javaList)
    // scalaBuffer2 shouldBe scalaBuffer
  }

  //region Run as an app

  this.demoCollectionConverters()
  this.demoDeprecatedCollectionConverters()
  this.demoImplicitConversions()

  //endregion Run as an app

}
