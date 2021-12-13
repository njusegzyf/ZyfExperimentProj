package nju.seg.zhangyf.experiment.scala212

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @see http://scala-lang.org/news/2.12.0#lambda-syntax-for-sam-types
  *
  * @author Zhang Yifan
  */
//noinspection ScalaUnusedSymbol
@ParametersAreNonnullByDefault
private object LambdaForSamExample {

  def lambdaToSam(): Unit = {
    // The Scala 2.12 type checker accepts a function literal as a valid expression for any Single Abstract Method (SAM) type,
    // in addition to the FunctionN types from standard library.
    // This improves the experience of using libraries written for Java 8 from Scala code.

    val r: Runnable = { () => println("Run!") }
    r.run()

    // Note that only lambda expressions are converted to SAM type instances, not arbitrary expressions of FunctionN type.
    // And Scala standard function types (FunctionN, etc) are not compatible with Java standard function types (in java.util.function).
    val f: () => Unit = { () => println("Run!") } // `() => Unit` is alias for `Function0[Unit]`

    // Note: `illTyped` is Ok in Scala 2.12 with shapeless 2.12:2.3.2, but failed in Scala 2.13 with shapeless 2.13:2.3.3
    // import shapeless.test.illTyped
    // illTyped("val fasterRunnable: Runnable = f") // error: type mismatch;

    // The language specification has the full list of requirements for SAM conversion
    // (http://www.scala-lang.org/files/archive/spec/2.12/06-expressions.html#sam-conversion).

    // With the use of default methods, Scala’s built-in FunctionN traits are compiled to SAM interfaces.
    // Specialized function classes are also SAM interfaces and can be found in the package scala.runtime.java8.
    // This allows creating Scala functions from Java using Java’s own lambda syntax:
    //
    // public class A {
    //   scala.Function1<String, String> f = s -> s.trim();
    // }

    // Thanks to an improvement in type checking, the parameter type in a lambda expression can be omitted even when the invoked method is overloaded.
    // See #5307 (https://github.com/scala/scala/pull/5307) for details.
    // In the following example, the compiler infers parameter type Int for the lambda:

    trait MyFun {def apply(x: Int): String }

    object T {
      def m(f: Int => String) = 0
      def m(f: MyFun) = 1
    }

    assert(T.m(x => x.toString) == 0)
    // Note that though both methods are applicable, overloading resolution selects the one with type `Int => String`, as explained in more detail below.
    // (http://scala-lang.org/news/2.12.0#sam-conversion-in-overloading-resolution)
  }

}
