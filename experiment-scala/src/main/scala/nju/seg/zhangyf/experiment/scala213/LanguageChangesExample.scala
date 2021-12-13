package nju.seg.zhangyf.experiment.scala213

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @see [[https://github.com/scala/scala/releases/tag/v2.13.0]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
object LanguageChangesExample {

  //region Doc

  /* Language changes

2.13 is primarily a library release, not a language/compiler release. Regardless, some language changes are included:

Features:

    Literal types
        Literals (for strings, integers, and so on) now have associated literal types. (#5310)
        See the original proposal, SIP-23, for motivation and details.
        The compiler will provide instances of a new typeclass scala.ValueOf[T] for all singleton types T.
        A Singleton upper bound prevents widening (e.g. T <: Int with Singleton).
        The value of a singleton type can be accessed by calling method valueOf[T]. Example: val one: 1 = valueOf[1]
    Partial unification on by default
        Improves type constructor inference, fixes SI-2712.
        We recommend this [great explanation of this feature] (https://gist.github.com/djspiewak/7a81a395c461fd3a09a6941d4cd040f2).
        This feature is no longer considered experimental (#5102)
        The compiler no longer accepts -Ypartial-unification.
    By-name implicits with recursive dictionaries
        This extends by-name method arguments to support implicit (not just explicit) parameters.
        This enables implicit search to construct recursive values.
        The flagship use-case is typeclass derivation.
        Details: see the by-name implicits SIP, #6050, #7368
    Underscores in numeric literals
        Underscores can now be used as a spacer. (#6989)
        Example: 1_000_000

Experimental features:

    Macro annotations
        There is no more "macro paradise" compiler plugin for 2.13.
        Instead, macro annotations are handled directly by the compiler.
        Macro annotations are enabled with the -Ymacro-annotations flag. #6606
        Macro annotations remain experimental.

Deprecations:

    Procedure syntax deprecated
        Deprecated: def m() { ... }) #6325
        Use instead: def m(): Unit = { ... }
    View bounds deprecated
        Deprecated: A <% B (#6500)
        Use instead: (implicit ev: A => B)
    Symbol literals deprecated
        Symbols themselves remain supported, only the single-quote syntax is deprecated. (#7395)
        Library designers may wish to change their APIs to use String instead.
        Deprecated: 'foo
        Use instead: Symbol("foo")
    Unicode arrows deprecated
        In particular, the single arrow operators had the wrong precedence. (#7540)
        Deprecated: ⇒, →, ←
        Use instead: =>, ->, <-
    postfixOps syntax disabled by default
        The syntax, already deprecated in 2.12, causes an error in 2.13 unless the feature is explicitly enabled. (#6831)
        Error: xs size
        Use instead: xs.size

Adjustments:

    Imports, including wildcard imports, now shadow locally defined identifiers. (#6589)
    Underscore is no longer a legal identifier unless backquoted (bug#10384)
        val _ = is now a pattern match
        implicit val _ = is also now a pattern match which does not introduce an identifier and therefore does not add anything to implicit scope
    Don't assume unsound type for ident/literal patterns. (#6502)
        Matches of the form case x@N involve calling equals, so it was unsound to type x as N.type.
        Consider rewriting as case x:N.type.
    Make extractor patterns null safe. (#6485)
        null is treated as no match.
    Better typing for overloaded higher-order methods (#6871, #7631)
        This change was a key enabler for the new collections design.
    Rework unification of Object and Any in Java/Scala interop (#7966)
    Name-based pattern matching has changed to enable immutable Seq matches (#7068)
    Automatic eta-expansion of zero-argument methods is no longer deprecated (#7660)
    Improve binary stability of extension methods (#7896)
    Macros must now have explicit return types (#6942)
    Mixin fields with trait setters are no longer JVM final (#7028)
        In addition, object fields are now static (#7270)
    Support implicitNotFound on parameters (#6340)
    Disallow repeated parameters except in method signatures (#7399)
    Value-discard warnings can be suppressed via type ascription to Unit. (#7563)
    x op () now parses as x.op(()) not x.op() (#7684)

    */

  //endregion Doc

  /**
   * @see [[https://docs.scala-lang.org/sips/42.type.html]]
   */
  def demoLiteralTypes(): Unit = {

    // Literal types
    // Literals (for strings, integers, and so on) now have associated literal types. (#5310)
    // See the original proposal, SIP-23, for motivation and details.
    // The compiler will provide instances of a new typeclass scala.ValueOf[T] for all singleton types T.
    // A Singleton upper bound prevents widening (e.g. T <: Int with Singleton).
    // The value of a singleton type can be accessed by calling method valueOf[T]. Example: val one: 1 = valueOf[1]

    // val declaration
    val one: 1 = 1

    // param type, type arg
    def foo(x: 1): Option[1] = Some(x)

    // type parameter bound
    def bar[T <: 1](t: T): T = t

    // type ascription
    foo(1: 1)

    // The `.type` singleton type forming operator can be applied to values of all subtypes of Any.
    // To prevent the compiler from widening our return type we assign to a final val.
    def foo2[T](t: T): t.type = t

    val v1: 23 = foo2(23)

    // The presence of an upper bound of Singleton on a formal type parameter indicates that singleton types should be inferred for type parameters at call sites.
    // To help see this we introduce type constructor Id to prevent the compiler from widening our return type.
    type Id[A] = A

    def wide[T](t: T): Id[T] = t

    def narrow[T <: Singleton](t: T): Id[T] = t

    val v2: Id[Int] = wide(23)
    val v3: 23 = narrow(23)

    // Pattern matching against literal types and isInstanceOf tests are implemented via equality/identity tests of the corresponding values.
    assert((1: Any) match { // result is true
             case one: 1 => true
             case _      => false
           })
    assert((1: Any).isInstanceOf[1])

    // A scala.ValueOf[T] type class and corresponding scala.Predef.valueOf[T] operator has been added yielding the unique value of types with a single inhabitant.
    def foo3[T](implicit v: ValueOf[T]): T = v.value

    val v4: 13 = foo3[13]
  }

  /**
   * @see [[https://nschejtman.medium.com/understanding-partial-unification-in-scala-317614c5dac7]]
   * @see [[https://www.sderosiaux.com/articles/2018/04/12/an-ode-to-the-kind-projector-and-to-the-partial-unification-of-scala/]]
   */
  def demoPartialUnification(): Unit = {

  }

}
