package nju.seg.zhangyf.experiment.scalalanglib.javacompat

import scala.language.unsafeNulls
import scala.collection.immutable.HashMap
import scala.concurrent.Future

import java.{ util, lang => jl }
import java.util.{ Optional, OptionalDouble }
import java.util.stream.DoubleStream
import javax.annotation.ParametersAreNonnullByDefault

import com.google.common.collect.Lists

// import org.scalactic.TypeCheckedTripleEquals
// import org.scalatest.matchers.should.Matchers

/**
 * @see See [[nju.seg.zhangyf.scala.scalalanglibexamples.javacompat.ScalaJavaCollectionConvertExample ScalaJavaCollectionCompat]]
 *      for collection conversions defined in Scala library.
 * @see [[https://github.com/scala/scala-java8-compat]]
 *
 * @author Zhang Yifan
 */
//noinspection ScalaUnusedSymbol
@ParametersAreNonnullByDefault
object ScalaJava8CompatExample { // extends Matchers with TypeCheckedTripleEquals {

  def main(args: Array[String]): Unit = {

    //region Functional Interfaces for Scala functions

    // A set of Functional Interfaces for scala.FunctionN. These are designed for convenient construction of Scala functions using Java 8 lambda syntax.

    val function: util.function.Function[String, String] = { (s: String) => s }
    import scala.compat.java8.FunctionConverters.asScalaFromFunction
    val future: Future[String] = Future.successful("Test")
    import scala.concurrent.ExecutionContext.Implicits.global
    future.map { asScalaFromFunction(function) }

    //endregion Functional Interfaces for Scala functions

    //region Converters between scala.FunctionN and java.util.function

    // A set of converters that enable interconversion between Java's standard Functional Interfaces
    // defined in java.util.function and Scala's Function0, Function1, and Function2 traits.
    // These are intended for use when you already have an instance of a java.util.function and need a Scala function,
    // or have a Scala function and need an instance of a java.util.function.
    //
    // The .asScala extension method will convert a java.util.function to the corresponding Scala function.
    // The .asJava extension method will convert a Scala function to the most specific corresponding Java functional interface.
    // If you wish to obtain a less specific functional interface,
    // there are named methods that start with asJava and continue with the name of the Java functional interface.
    // For instance, the most specific interface corresponding to the Scala function val rev = (s: String) => s.reverse is UnaryOperator[String],
    // and that is what rev.asJava will produce. However, asJavaFunction(rev) will return a java.util.function.Function[String, String] instead.
    //
    // The asJava methods can also be called conveniently from Java.
    // There are additional asScalaFrom methods (e.g. asScalaFromUnaryOperator) that will perform the functional-interface-to-Scala-function conversion;
    // this is primarily of use when calling from Java since the .asScala extension method is more convenient in Scala.

    // In Scala:

    val foo: Int => Boolean = { i => i >= 7 }

    def testBig(ip: util.function.IntPredicate): Boolean = ip.test(9)
    import scala.compat.java8.FunctionConverters.enrichAsJavaIntPredicate
    // testBig(foo.asJava) shouldBe true

    val bar = new util.function.UnaryOperator[String] {
      override def apply(s: String): String = s.reverse
    }
    import scala.compat.java8.FunctionConverters.enrichAsScalaFromUnaryOperator
    // List("cod", "herring").map(bar.asScala) should ===(List("doc", "gnirreh"))

    def testA[A](p: util.function.Predicate[A])(a: A): Boolean = p.test(a)
    import scala.compat.java8.FunctionConverters.asJavaPredicate
    println(s"testA(asJavaPredicate(foo))(4) result : ${ testA(asJavaPredicate(foo))(4) }") // Print false

    // println(testA(foo.asJava)(4))  <-- doesn't work
    //                                    IntPredicate does not extend Predicate!

    // In Java:

    //    import java.util.function.*;
    //    import scala.compat.java8.FunctionConverters;
    //
    //    class Example {
    //      String foo(UnaryOperator<String> f) {
    //        return f.apply("halibut");
    //      }
    //      String bar(scala.Function1<String, String> f) {
    //        return foo(functionConverters.asJavaUnaryOperator(f));
    //      }
    //      String baz(Function<String, String> f) {
    //        return bar(functionConverters.asScalaFromFunction(f));
    //      }
    //    }

    //endregion Converters between scala.FunctionN and java.util.function

    //region Converters between scala.concurrent and java.util.concurrent

    // @see https://github.com/scala/scala-java8-compat#converters-between-scalaconcurrent-and-javautilconcurrent

    //endregion Converters between scala.concurrent and java.util.concurrent

    //region Converters between scala.Option and java.util classes Optional, OptionalDouble, OptionalInt, and OptionalLong.

    // A set of extension methods to enable explicit conversion between Scala Option and the Java 8 optional types,
    // Optional, OptionalDouble, OptionalInt, and OptionalLong.
    // Note that the four Java classes have no inheritance relationship despite all encoding optional types.

    import scala.compat.java8.OptionConverters._

    class Test {
      val o: Option[Double] = Option(2.7)
      val oj: Optional[Double] = o.asJava
      val ojd: OptionalDouble = o.asPrimitive
      val ojds: Option[Double] = ojd.asScala // Option(2.7) again
    }

    //endregion Converters between scala.Option and java.util classes Optional, OptionalDouble, OptionalInt, and OptionalLong.

    //region Converters from Scala collections to Java 8 Streams

    // Scala collections gain seqStream and parStream as extension methods that produce a Java 8 Stream running sequentially or in parallel, respectively.
    // These are automatically specialized to a primitive type if possible, including automatically applied widening conversions.
    // For instance, List(1,2).seqStream produces an IntStream, and so does List(1.toShort, 2.toShort).parStream.
    // Maps additionally have seqKeyStream, seqValueStream, parKeyStream, and parValueStream methods.
    import scala.compat.java8.StreamConverters._

    import java.util.{ stream => jus }
    val intList: List[Int] = List(0)
    val refList: List[AnyRef] = List(new AnyRef)
    val refVector: Vector[AnyRef] = Vector[AnyRef]()

    // Scala (lazy) stream
    val scalaStreamOfList: LazyList[Int] = intList.to(LazyList)
    // a Java 8 Stream running sequentially
    val seqStreamOfList: jus.Stream[AnyRef] = refList.seqStream
    // the above is expand to :
    //    EnrichAnySteppableWithSeqStream(refList)(scala.compat.java8.StreamConverters.richLinearSeqCanStep,
    //                                             scala.compat.java8.StreamShape.anyStreamShape[AnyRef])
    //    .seqStream

    // automatically specialized
    val seqIntStreamOfList: jus.IntStream = intList.seqStream
    // a Java 8 Stream running in parallel
    // Note : Scala List (LinkedList) can not convert to par stream (lacks a implicit steppize object for `List`)
    val parStreamOfVector: jus.Stream[AnyRef] = refVector.parStream
    // the above is expand to :
    //    EnrichAnySteppableWithParStream(refVector)(scala.compat.java8.StreamConverters.richVectorCanStep,
    //                                               scala.compat.java8.StreamShape.anyStreamShape[AnyRef])
    //    .parStream

    // Scala collections also gain accumulate and stepper methods that produce utility collections that can be useful when working with Java 8 Streams.
    // accumulate produces an Accumulator or its primitive counterpart (DoubleAccumulator, etc.), which is a low-level collection designed for efficient collection and dispatching of results to and from Streams.
    // Unlike most collections, it can contain more than Int.MaxValue elements.
    import scala.compat.java8.collectionImpl.Accumulator
    val accumulator: Accumulator[AnyRef] = refList.accumulate
    // is expand to:
    collectionCanAccumulate(refList).accumulate

    // stepper produces a Stepper which is a fusion of Spliterator and Iterator.
    // Steppers underlie the Scala collections' instances of Java 8 Streams.
    // Steppers are intended as low-level building blocks for streams.
    import scala.compat.java8.collectionImpl.Stepper
    val stepper: Stepper[AnyRef] = refVector.stepper
    // the above is expand to :
    //    richVectorCanStep(refVector).stepper

    // Usually you would not create them directly or call their methods but you can implement them alongside custom collections to get better performance when streaming from these collections.
    //
    // Java 8 Streams gain toScala[Coll] and accumulate methods, to make it easy to produce Scala collections or Accumulators, respectively, from Java 8 Streams.
    // For instance, myStream.toScala[Vector] will collect the contents of a Stream into a scala.collection.immutable.Vector.
    // Note that standard sequential builders are used for collections, so this is best done to gather the results of an expensive computation.
    val javaArrayList: util.ArrayList[AnyRef] = Lists.newArrayList(new AnyRef)
//    val vectorFromJavaArrayList: Vector[Integer] =
//      javaArrayList.stream
//                   .map { _ => Integer.valueOf(0) } // Scala lambda can be convert to a Java SAM
//                   .toScala[Vector]
    // vectorFromJavaArrayList should ===(Vector(Integer.valueOf(0)))

    // Finally, there is a Java class, ScalaStreamSupport, that has a series of stream methods that can be used to obtain Java 8 Streams from Scala collections from within Java.
    import scala.compat.java8.ScalaStreamSupport
    // get a Java `Stream`
    val javaRefStream: util.stream.Stream[AnyRef] = ScalaStreamSupport.stream(Vector(new AnyRef))
    // get a Java `DoubleStream`, notice method `doubleStream` is defined in Java and thus requires a `Vector[java.lang.Double]` instead of a `Vector[Double]`
    val javaDoubleStream: DoubleStream = ScalaStreamSupport.doubleStream(Vector[jl.Double](jl.Double.valueOf(1.0d)))
    // javaDoubleStream.sum shouldBe 1.0d

    //region Performance Considerations

    // For sequential operations, Scala's iterator almost always equals or exceeds the performance of a Java 8 stream.
    // Thus, one should favor iterator (and its richer set of operations) over seqStream for general use.
    // However, long chains of processing of primitive types can sometimes benefit from the manually specialized methods in DoubleStream, IntStream, and LongStream.
    //
    // Note that although iterator typically has superior performance in a sequential context, the advantage is modest (usually less than 50% higher throughput for iterator).
    //
    // For parallel operations, parStream and even seqStream.parallel meets or exceeds the performance of Scala parallel collections methods (invoked with .par).
    // Especially for small collections, the difference can be substantial.
    // In some cases, when a Scala (parallel) collection is the ultimate result,
    // Scala parallel collections can have an advantage as the collection can (in some cases) be built in parallel.
    //
    // Because the wrappers are invoked based on the static type of the collection,
    // there are also cases where parallelization is inefficient when interfacing with Java 8 Streams
    // (e.g. when a collection is typed as Seq[String] so might have linear access like List, but actually is a WrappedArray[String] that can be efficiently parallelized)
    // but can be efficient with Scala parallel collections.
    // The parStream method is only available when the static type is known to be compatible with rapid parallel operation;
    // seqStream can be parallelized by using .parallel, but may or may not be efficient.
    //
    // If the operations available on Java 8 Streams are sufficient, the collection type is known statically with enough precision to enable parStream,
    // and an Accumulator or non-collection type is an acceptable result, Java 8 Streams will essentially always outperform the Scala parallel collections.

    //endregion Performance Considerations

    //region Scala Usage Example

    def streamTestScala(): Unit = {
      val m: HashMap[String, Int] = collection.immutable.HashMap("fish" -> 2, "bird" -> 4)
      // 6, potentially computed in parallel

      // Error since Scala 2.13:
      // Error:(219, 22) parValueStream can only be called on maps where `valueStepper` returns a `Stepper with EfficientSplit`
      //
      //      val s: Int = m.parValueStream.sum
      //      // List("fish", "bird")
      //      val t: Seq[String] = m.seqKeyStream.toScala[List]
      //      val a: Accumulator[(String, Int)] = m.accumulate
      //
      //      val n: Long = a.stepper.fold(0) { _ + _._1.length } +
      //                    a.parStream.count // 8 + 2 = 10
      //
      //      import scala.compat.java8.collectionImpl.LongAccumulator
      //      val b: LongAccumulator = util.Arrays.stream(Array(2L, 3L, 4L)).accumulate
      //      val l: List[Long] = b.to(List) // List(2L, 3L, 4L)
    }

    // Using Java 8 Streams with Scala Function Converters

    // Scala can emit Java SAMs for lambda expressions that are arguments to methods that take a Java SAM rather than a Scala Function.
    // However, it can be convenient to restrict the SAM interface to interactions with Java code (including Java 8 Streams)
    // rather than having it propagate throughout Scala code.
    //
    // Using Java 8 Stream converters together with function converters allows one to accomplish this with only a modest amount of fuss.

    import scala.compat.java8.FunctionConverters._

    /*
    def mapToSortedString[A](xs: Vector[A], f: A => String, sep: String): String =
      xs.parStream // Creates java.util.stream.Stream[String]
        .map[String] { f.asJava } // `asJava` wraps a Scala Function to a Java Function (java.util.function.Function[T, R])
        .sorted // Maps A to String and sorts (in parallel)
        .toArray
        .mkString(sep) // Back to an Array to use Scala's mkString

    // Note that explicit creation of a new lambda will tend to lead to improved type inference and at least equal performance:
    def mapToSortedString2[A](xs: Vector[A], f: A => String, sep: String) =
      xs.parStream.
        map[String] { a => f(a.nn) } // Explicit lambda creates a SAM wrapper for f
        .sorted
        .toArray
        .mkString(sep)
    */

    //endregion Scala Usage Example

    //region Java Usage Example

    // To convert a Scala collection to a Java 8 Stream from within Java, it usually suffices to call ScalaStreamSupport.stream(xs) on your collection xs.
    // If xs is a map, you may wish to get the keys or values alone by using fromKeys or fromValues.
    // If the collection has an underlying representation that is not efficiently parallelized (e.g. scala.collection.immutable.List),
    // then fromAccumulated (and fromAccumulatedKeys and fromAccumulatedValues) will first gather the collection into an Accumulator
    // and then return a stream over that accumulator. If not running in parallel, from is preferable (faster and less memory usage).
    //
    // Note that a Scala Iterator cannot fulfill the contract of a Java 8 Stream (because it cannot support trySplit if it is called).
    // Presently, one must call fromAccumulated on the Iterator to cache it, even if the Stream will be evaluated sequentially,
    // or wrap it as a Java Iterator and use static methods in Spliterator to wrap that as a Spliterator and then a Stream.

    // Here is an example of conversion of a Scala collection within Java 8:

    //    import scala.collection.mutable.ArrayBuffer;
    //    import scala.compat.java8.ScalaStreamSupport;
    //
    //    public class StreamConvertersExample {
    //      public int MakeAndUseArrayBuffer() {
    //        ArrayBuffer<String> ab = new ArrayBuffer<String>();
    //        ab.$plus$eq("salmon");
    //        ab.$plus$eq("herring");
    //        return ScalaStreamSupport.stream(ab).mapToInt(x -> x.length()).sum();  // 6+7 = 13
    //      }
    //    }

    //endregion Java Usage Example

    //endregion Converters from Scala collections to Java 8 Streams
  }

}
