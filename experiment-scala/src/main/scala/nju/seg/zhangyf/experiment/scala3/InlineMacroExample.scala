package nju.seg.zhangyf.experiment.scala3

import scala.util.Random

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @see [[https://docs.scala-lang.org/scala3/guides/macros/inline.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object InlineMacroExample {

  /* Inlining is a common compile-time metaprogramming technique, typically used to achieve performance optimizations.
     As we will see, in Scala 3, the concept of inlining provides us with an entrypoint to programming with macros.
       It introduces inline as a soft keyword.
       It guarantees that inlining actually happens instead of being best-effort.
       It introduces operations that are guaranteed to evaluate at compile-time.
   */

  def demoInlineConsants(): Unit = {

    // The simplest form of inlining is to inline constants in programs:
    inline val pi = 3.141592653589793

    // The usage of the keyword inline in the inline value definitions above guarantees that all references to pi and pie are inlined:
    val pi2 = pi + pi // val pi2 = 6.283185307179586

    //In the code above, the references pi and pie are inlined. Then an optimization called “constant folding” is applied by the compiler,
    // which computes the resulting value pi2 at compile-time.

    // Inline (Scala 3) vs. final (Scala 2)
    // In Scala 2, we would have used the modifier final in the definition that is without a return type:
    // final val pi = 3.141592653589793
    // The final modifier will ensure that pi will take a literal type.
    // Then the constant propagation optimization in the compiler can perform inlining for such definitions.
    // However, this form of constant propagation is best-effort and not guaranteed.
    // Scala 3.0 also supports final val-inlining as best-effort inlining for migration purposes.

    // Currently, only constant expressions may appear on the right-hand side of an inline value definition.
    // Therefore, the following code is invalid, though the compiler knows that the right-hand side is a compile-time constant value:
    //   val pi = 3.141592653589793
    //   inline val pi2 = pi + pi // error
    // Note that by defining inline val pi, the addition can be computed at compile time.
    // This resolves the above error and pi2 will receive the literal type 6.283185307179586d.
  }

  //region Inline Methods

  def demoInlineMethods(): Unit = {

    // We can also use the modifier inline to define a method that should be inlined at the call-site:
    inline def logged[T](level: Int, message: => String)(inline op: T): T =
      println(s"[$level]Computing $message")
      val res = op
      println(s"[$level]Result of $message: $res")
      res

    // When an inline method like logged is called, its body will be expanded at the call-site at compile time!
    // That is, the call to logged will be replaced by the body of the method.
    // The provided arguments are statically substituted for the parameters of logged, correspondingly.
    //
    // Therefore, the compiler inlines the following call
    //   logged(logLevel, getMessage()) { computeSomething() }
    // and rewrites it to:
    //   val level   = logLevel
    //   def message = getMessage()
    //   println(s"[$level]Computing $message")
    //   val res = computeSomething()
    //   println(s"[$level]Result of $message: $res")
    //   res

    // Semantics of Inline Methods
    // Our example method logged uses three different kinds of parameters, illustrating that inlining handles those parameters differently:
    //
    //   By-value parameters. The compiler generates a val binding for by-value parameters.
    //     This way, the argument expression is evaluated only once before the method body is reduced.
    //     In some cases, when the arguments are pure constant values, the binding is omitted and the value is inlined directly.
    //
    //   By-Name parameters. The compiler generates a def binding for by-name parameters.
    //     This way, the argument expression is evaluated every time it is used, but the code is shared.
    //
    //   Inline parameters. Inline parameters do not create bindings and are simply inlined.
    //     This way, their code is duplicated everywhere they are used.
    //
    // The way the different parameters are translated guarantees that inlining a call will not change its semantics.
    // This implies that the initial elaboration (overloading resolution, implicit search, …), performed while typing the body of the inline method, will not change when inlined.
    //
    // For example, consider the following code:
    //
    // class Logger:
    //   def log(x: Any): Unit = println(x)
    //
    // class RefinedLogger extends Logger:
    //   override def log(x: Any): Unit = println("Any: " + x)
    //   def log(x: String): Unit = println("String: " + x)
    //
    // inline def logged[T](logger: Logger, x: T): Unit =
    //   logger.log(x)
    //
    // The separate type checking of logger.log(x) will resolve the call to the method Logger.log which takes an argument of the type Any.
    // Now, given the following code:
    //   logged(new RefinedLogger, "✔️")
    // It expands to:
    //   val logger = new RefinedLogger
    //   val x = "✔️"
    //   logger.log(x)
    // Even though now we know that x is a String, the call logger.log(x) still resolves to the method Logger.log which takes an argument of the type Any.
    // Note that because of late-binding, the actual method called at runtime will be the overridden method RefinedLogger.log.

    // Inlining preserves semantics
    //   Regardless of whether logged is defined as a def or inline def, it performs the same operations with only some differences in performance.
  }

  def demoInlineParameters(): Unit = {

    // One important application of inlining is to enable constant folding optimisation across method boundaries.
    // Inline parameters do not create bindings and their code is duplicated everywhere they are used.

    inline val pi = 3.1415926

    inline def perimeter(inline radius: Double): Double =
      2.0 * pi * radius

    // In the above example, we expect that if the radius is statically known then the whole computation can be performed at compile-time.
    //
    // The following call
    //   perimeter(5.0)
    // is rewritten to:
    //   2.0 * pi * 5.0
    // Then pi is inlined:
    //   2.0 * 3.141592653589793 * 5.0
    // Finally, it is constant folded to
    //   31.4159265359

    // Inline parameters should be used only once
    // We need to be careful when using an inline parameter more than once.
    //
    // Consider the following code:

    //   inline def printPerimeter(inline radius: Double): Double =
    //     println(s"Perimeter (r = $radius) = ${perimeter(radius)}")
    // It works perfectly fine when a constant or reference to a val is passed to it.
    //   printPerimeter(5.0)
    // inlined as
    //   println(s"Perimeter (r = ${5.0}) = ${31.4159265359}")
    // But if a larger expression (possibly with side-effects) is passed, we might accidentally duplicate work.
    //   printPerimeter(longComputation())
    // inlined as
    //   println(s"Perimeter (r = ${longComputation()}) = ${6.283185307179586 * longComputation()}")

    // A useful application of inline parameters is to avoid the creation of closures, incurred by the use of by-name parameters.

    inline def assert1(cond: Boolean, msg: => String) =
      if !cond then
        throw new Exception(msg)

    val x = false
    assert1(x, "error1")
    // is inlined as
    val cond = x

    def msg = "error1"

    if !cond then
      throw new Exception(msg)

    // In the above example, we can see that the use of a by-name parameter leads to a local definition msg, which allocates a closure before the condition is checked.
    // If we use an inline parameter instead, we can guarantee that the condition is checked before any of the code that handles the exception is reached.
    // In the case of an assertion, this code should never be reached.

    inline def assert2(cond: Boolean, inline msg: String) =
      if !cond then
        throw new Exception(msg)

    assert2(x, "error2")
    // is inlined as
    val cond2 = x
    if !cond2 then
      throw new Exception("error2")
  }

  def demoInlineConditionals(): Unit = {
    // If the condition of an if is a known constant (true or false), possibly after inlining and constant folding,
    // then the conditional is partially evaluated and only one branch will be kept.

    // For example, the following power method contains some if that will potentially unroll the recursion and remove all method calls.
    inline def power(x: Double, inline n: Int): Double =
      if (n == 0) {
        1.0
      } else if (n % 2 == 1) {
        x * power(x, n - 1)
      } else {
        power(x * x, n / 2)
      }

    // Calling power with statically known constants results in the following code:
    power(2, 2)

    // first inlines as
    {
      val x = 2
      if (2 == 0) 1.0 // dead branch
      else if (2 % 2 == 1) x * power(x, 2 - 1) // dead branch
           else power(x * x, 2 / 2)
    }

    // partially evaluated to
    {
      val x = 2
      power(x * x, 1)
    }
    // ......

    // In contrast, let us imagine we do not know the value of n:
    //   power(2, unknownNumber)
    // Driven by the inline annotation on the parameter, the compiler will try to unroll the recursion.
    // But without any success, since the parameter is not statically known.

    // To guarantee that the branching can indeed be performed at compile-time, we can use the inline if variant of if.
    // Annotating a conditional with inline will guarantee that the conditional can be reduced at compile-time
    // and emits an error if the condition is not a statically known constant.
    //
    inline def inlinePower(x: Double, inline n: Int): Double =
      inline if (n == 0) 1.0
      else inline if (n % 2 == 1) x * power(x, n - 1)
           else power(x * x, n / 2)

    inlinePower(2, 2) // Ok
    // inlinePower(2, Random.nextInt())
    // compiler error:
    // Cannot reduce `inline if` because its condition is not a constant value: util.Random.nextInt().==(0)
    //      inline if (n == 0) 1.0
  }

  def demoInlineMethodOverriding(): Unit = {

    // To ensure the correct behavior of combining the static feature of inline def with the dynamic feature of interfaces and overriding, some restrictions have to be imposed.
    //
    // Effectively final
    // Firstly, all inline methods are effectively final. This ensures that the overload resolution at compile-time behaves the same as the one at runtime.
    //
    // Signature preservation
    // Secondly, overrides must have the exact same signature as the overridden method including the inline parameters.
    // This ensures that the call semantics are the same for both methods.
    //
    // Retained inline methods
    // It is possible to implement or override a normal method with an inline method.

    // Consider the following example:
    //
    trait Logger:
      def log(x: Any): Unit

    class PrintLogger extends Logger :
      inline def log(x: Any): Unit = println(x)

    // However, calling the log method directly on PrintLogger will inline the code, while calling it on Logger will not.
    // To also admit the latter, the code of log must exist at runtime. We call this a retained inline method.

    // For any non-retained inline def or val the code can always be fully inlined at all call sites.
    // Hence, those methods will not be needed at runtime and can be erased from the bytecode.
    // However, retained inline methods must be compatible with the case that they are not inlined.
    // In particular, retained inline methods cannot take any inline parameters.
    // Furthermore, an inline if (as in the power example) will not work, since the if cannot be constant folded in the retained case.
    // Other examples involve metaprogramming constructs that only have meaning when inlined.
  }

  def demoAbstractInlineMethods(): Unit = {
    // It is also possible to create abstract inline definitions.

    trait InlineLogger:
      inline def log(inline x: Any): Unit

    class PrintLogger extends InlineLogger :
      inline def log(inline x: Any): Unit = println(x)

    // This forces the implementation of log to be an inline method and also allows inline parameters.
    // Counterintuitively, the log on the interface InlineLogger cannot be directly called.
    // The method implementation is not statically known and we thus do not know what to inline.
    // Calling an abstract inline method thus results in an error.
    // The usefulness of abstract inline methods becomes apparent when used in another inline method:
    inline def logged(logger: InlineLogger, x: Any) =
      logger.log(x)

    logged(new PrintLogger, "Test")

    // inlined as
    val logger: PrintLogger = new PrintLogger
    logger.log("Test")
    // After inlining, the call to log is de-virtualized and known to be on PrintLogger.
    // Therefore also the code of log can be inlined.
  }

  /* Summary of inline methods
       All inline methods are final.
       Abstract inline methods can only be implemented by inline methods.
       If an inline method overrides/implements a normal method then it must be retained and retained methods cannot have inline parameters.
       Abstract inline methods cannot be called directly (except in inline code).
  */

  //endregion Inline Methods

  def demoTransparentInlineMethods(): Unit = {
    // Transparent inlines are a simple, yet powerful, extension to inline methods and unlock many metaprogramming usecases.
    // Calls to transparents allow for an inline piece of code to refine the return type based on the precise type of the inlined expression.
    // In Scala 2 parlance, transparents capture the essence of whitebox macros.

    transparent inline def default(inline name: String): Any =
      inline if name == "Int" then 0
      else inline if name == "String" then ""
           else null

    val n0: Int = default("Int")
    val s0: String = default("String")

    // Note that even if the return type of default is Any, the first call is typed as an Int and the second as a String.
    // The return type represents the upper bound of the type within the inlined term.
    // We could also have been more precise and have written instead
    //   transparent inline def default(inline name: String): 0 | "" = ...

    // While in this example it seems the return type is not necessary, it is important when the inline method is recursive.
    // There it should be precise enough for the recursion to type but will get more precise after inlining.

    //  Transparents affect binary compatibility
    //  It is important to note that changing the body of a transparent inline def will change how the call site is typed.
    //  This implies that the body plays a part in the binary and source compatibility of this interface.
  }

  def demoCompiletimeOperations(): Unit = {

    //region Inline Matches

    // Like inline if, inline matches guarantee that the pattern matching can be statically reduced at compile time and only one branch is kept.
    // In the following example, the scrutinee, x, is an inline parameter that we can pattern match on at compile time.

    inline def half(x: Any): Any =
      inline x match
        case x: Int    => x / 2
        case x: String => x.substring(0, x.length / 2)

    half(6)
    // expands to:
    // val x = 6
    // x / 2

    half("hello world")
    // expands to:
    // val x = "hello world"
    // x.substring(0, x.length / 2)

    // This illustrates that inline matches provide a way to match on the static type of some expression.
    // As we match on the static type of an expression, the following code would fail to compile.

    // val n: Any = 3
    // half(n) // error: n is not statically known to be an Int or a Double
    // Notably, The value n is not marked as inline and in consequence at compile time there is not enough information about the scrutinee to decide which branch to take.

    //endregion Inline Matches

    // scala.compiletime
    // The package scala.compiletime provides useful metaprogramming abstractions that can be used within inline methods to provide custom semantics.
  }

  def demoMacros(): Unit = {
    // Inlining is also the core mechanism used to write macros. Macros provide a way to control the code generation and analysis after the call is inlined.

    // inline def power(x: Double, inline n: Int) =
    //   ${ powerCode('x, 'n)  }
    //
    // def powerCode(x: Expr[Double], n: Expr[Int])(using Quotes): Expr[Double] = ...
  }

}
