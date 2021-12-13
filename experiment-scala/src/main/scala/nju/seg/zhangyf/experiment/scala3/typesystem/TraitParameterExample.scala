package nju.seg.zhangyf.experiment.scala3.typesystem

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @see [[https://docs.scala-lang.org/scala3/reference/other-new-features/trait-parameters.html]]
 * @see [[https://docs.scala-lang.org/sips/trait-parameters.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object TraitParameterExample {

  sealed trait Greeting(val name: String):
    def msg = s"How are you, $name"

  final class C extends Greeting("Bob") :
    println(msg)

  // Arguments to a trait are evaluated immediately before the trait is initialized.
  // One potential issue with trait parameters is how to prevent ambiguities.
  // For instance, you might try to extend Greeting twice, with different parameters.
  //   class D extends C, Greeting("Bill") // error: parameter passed twice
  // In fact this program is illegal, because it violates the second rule of the following for trait parameters:
  //   If a class C extends a parameterized trait T, and its superclass does not, C must pass arguments to T.
  //   If a class C extends a parameterized trait T, and its superclass does as well, C must not pass arguments to T.
  //   Traits must never pass arguments to parent traits.

  // Here’s a trait extending the parameterized trait Greeting.

  sealed trait FormalGreeting extends Greeting :
    override def msg = s"How do you do, $name"

  // As is required, no arguments are passed to Greeting. However, this poses an issue when defining a class that extends FormalGreeting:
  //   class E extends FormalGreeting // error: missing arguments for `Greeting`.
  // The correct way to write E is to extend both Greeting and FormalGreeting (in either order):
  final class E extends FormalGreeting, Greeting("Bob")

  // This “explicit extension required” rule is relaxed if the missing trait contains only context parameters.
  // In that case the trait reference is implicitly inserted as an additional parent with inferred arguments.
  // For instance, here’s a variant of greetings where the addressee is a context parameter of type ImpliedName:
  final class ImpliedName(val name: String):
    override def toString = name

  sealed trait ImpliedGreeting(using val iname: ImpliedName):
    def msg = s"How are you, $iname"

  sealed trait ImpliedFormalGreeting extends ImpliedGreeting :
    override def msg = s"How do you do, $iname"

  final class F(using iname: ImpliedName) extends ImpliedFormalGreeting

  // The definition of F in the last line is implicitly expanded to
  //   class F(using iname: ImpliedName) extends
  //     Object,
  //     ImpliedGreeting(using iname),
  //     ImpliedFormalGreeting(using iname)

  // Note the inserted reference to the super trait ImpliedGreeting, which was not mentioned explicitly.

}
