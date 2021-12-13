package nju.seg.zhangyf.experiment.scala212

import scala.language.implicitConversions

import java.{ util => ju }
import javax.annotation.ParametersAreNonnullByDefault

/**
  * @see [[http://scala-lang.org/news/2.12.0#breaking-changes ]]
  * @see [[nju.seg.zhangyf.experimentscala.scala212.LambdaForSamExamples SAM examples]]
  *
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
object BreakingChangesExample extends App {

  // run as an app
  this.testSamConversionPrecedesImplicits()
  this.testSamConversionInOverloadingResolution()

  // Note: `illTyped` is Ok in Scala 2.12 with shapeless 2.12:2.3.2, but failed in Scala 2.13 with shapeless 2.13:2.3.3

  /** The SAM(Single Abstract Method) conversion built into the type system, which convert lambda expressions to SAM type instances,
    * takes priority over implicit conversion of function types to SAM types.
    * This can change the semantics of existing code relying on implicit conversion to SAM types.
    *
    * @see http://scala-lang.org/news/2.12.0#sam-conversion-precedes-implicits
    */
  def testSamConversionPrecedesImplicits(): Unit = {

    trait MySam {def i(): Int }
    implicit def convert(fun: () => Int): MySam = new MySam {def i() = 1 }

    val sam1: MySam = () => 2 // Uses SAM conversion, not the implicit, while in Scala 2.11 the implicit conversion `convert` will be picked
    sam1.i() // Returns 2

    // Note that SAM conversion only applies to lambda expressions, not to arbitrary expressions with Scala FunctionN types:
    val fun: () => Int = () => 2 // Type Function0[Int]

    def acceptFun(arg: ju.function.Supplier[Int]): Unit = {}

    acceptFun(() => 0) // OK, convert lambda to SAM type `Consumer[Int]`
    // import shapeless.test.illTyped
    // illTyped { "acceptFun(fun)" } // type mismatch, can not convert `() => Int` (scala.Function0[Int]) to SAM type `Consumer[Int]`

    val sam2: MySam = fun // uses implicit conversion
    sam2.i() // returns 1

  }

  /** In order to improve source compatibility, overloading resolution has been adapted to
    * prefer methods with Function-typed arguments over methods with parameters of SAM types.
    *
    * @see http://scala-lang.org/news/2.12.0#sam-conversion-in-overloading-resolution
    */
  def testSamConversionInOverloadingResolution(): Unit = {

    // The following example is identical in Scala 2.11 and 2.12:
    object T {
      def m(f: () => Unit) = 0
      def m(r: Runnable) = 1
    }
    // In Scala 2.11, the first alternative was chosen because it is the only applicable method.
    // In Scala 2.12, both methods are applicable, therefore overloading resolution needs to pick the most specific alternative.
    // The specification for type compatibility has been updated to consider SAM conversion, so that the first alternative is more specific.
    val f: () => Unit = () => ()
    assert(T.m(f) == 0)

    // Note that SAM conversion in overloading resolution is always considered, also if the argument expression is not a function literal (like in the example).
    // That is, the argument `f` can be converted into a SAM.
    // This is unlike SAM conversions of expressions themselves:

    // shapeless.test.illTyped { "val r : Runnable = f" } // SAM conversions are not considered

    // While the adjustment to overloading resolution improves compatibility overall, code does exist that compiles in 2.11 but is ambiguous in 2.12:
    object T2 {
      def m(f: () => Unit, o: Object) = 0
      def m(r: Runnable, s: String) = 1
    }
    // shapeless.test.illTyped { """T2.m(() => (), "")""" } // error: ambiguous reference to overloaded definition
  }

}
