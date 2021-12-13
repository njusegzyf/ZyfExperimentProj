package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @author Zhang Yifan
  */
//noinspection ScalaUnusedSymbol
@ParametersAreNonnullByDefault
private object DefaultParameterExample extends App {

  this.testDefaultParameter()
  this.testImplicitParameterWithDefaultParameter()

  /**
    * @see [[http://docs.scala-lang.org/tour/default-parameter-values.html Scala default parameter doc]]
    * @see [[https://en.wikipedia.org/wiki/Parameter_%28computer_programming%29#Parameters_and_arguments Different of parameters and arguments]]
    */
  def testDefaultParameter(): Unit = {

    // Scala provides the ability to give parameters default values that can be used to allow a caller to omit those parameters.
    def log(message: String, level: String = "INFO"): Unit = println(s"$level: $message")

    log("System starting") // prints INFO: System starting
    log("User not found", "WARNING") // prints WARNING: User not found

    // The parameter level has a default value so it is optional. On the last line, the argument "WARNING" overrides the default argument "INFO".
    // Where you might do overloaded methods in Java, you can use methods with optional parameters to achieve the same effect.

    // In Scala 2.12, the compiler generates code like :
    // private static final void log$1(String message, String level) { ... }
    // private static final String log$default$2$1() { return "INFO"; }
    // and `log("System starting")` is compiled to `log$1("System starting", log$default$2$1()`

    // So that default parameters in Scala are not optional when called from Java code:
    //    final class Point(val x: Double = 0, val y: Double = 0)
    //    public class Main {
    //      public static void main(final String[] args) {
    //        Point point = new Point(1);  // does not compile
    //      }
    //    }

    // In fact, we can define two or more methods with default parameters, but this will fail call like `log("System starting")`,
    // which needs one default parameter but have two candidate.
    //    def log(message: String, level: Int = 0): Unit = {
    //      log(message, level = if (level == 0) "INFO" else "WARNING")
    //    }

    // However, if the caller omits an argument, any following arguments must be named.
    final class Point(val x: Double = 0, val y: Double = 0)

    // Here we have to say y = 1.
    val point1: Point = new Point(y = 1)
    assert(point1.x == 0 && point1.y == 1)

    val point2: Point = new Point(1) // same as `new Point(x = 1)`
    assert(point2.x == 1 && point2.y == 0)
  }

  //region Combine implicit parameters and default parameters

  /**
    * @see [[nju.seg.zhangyf.scala.bookExamples.CH21ImplicitConversionsAndParameters]]
    */
  def testImplicitParameterWithDefaultParameter(): Unit = {

    final class Cla
    val defaultCla = new Cla

    def func(implicit v: Cla = defaultCla): Unit = {
      println(f"Parameter v is default value : ${ v eq defaultCla }")
    }

    func() // no implicit value, use defaultCla

    implicit val implicitCla: Cla = new Cla
    func() // has implicit value, still use defaultCla

    // This means the default value of an optional parameter has higher priority than implicit value.
    // If we combine implicit parameters with optional parameters,
    // without giving the implicit parameters, the compiler will always use the default value instead of picking an implicit value in scope.
  }

  //endregion Implicit parameters and default parameters

}
