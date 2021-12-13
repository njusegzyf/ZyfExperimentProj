package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @see [[http://docs.scala-lang.org/overviews/core/value-classes.html Value Classes and Universal Traits]]
  * @see [[http://docs.scala-lang.org/sips/completed/value-classes.html SIP-15 - Value Classes]]
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
private object ValueClassExample {

  /** `Printable` is a universal trait.
    * A universal trait is a trait that extends Any, only has defs as members, and does no initialization.
    */
  trait Printable extends Any {
    def print(): Unit = println(this)
  }

  /** `Wrapper` is a value class.
    * A value class can only extend universal traits and cannot be extended itself.
    */
  final class Wrapper(val underlying: Int) extends AnyVal with Printable

  /** Value classes are a new mechanism in Scala to avoid allocating runtime objects.
    * This is accomplished through the definition of new AnyVal subclasses.
    */
  def introduction(): Unit = {

    // notice : Value classes can not be local class
    // class Wrapper1(val underlying: Int) extends AnyVal // error

    // It has a single, public val parameter that is the underlying runtime representation.
    // The type at compile time is Wrapper, but at runtime, the representation is an Int.
    // A value class can define defs, but no vals, vars, or nested traits, classes or objects:

    // A value class can only extend universal traits and cannot be extended itself.
    // A universal trait is a trait that extends Any, only has defs as members, and does no initialization.

    // Universal traits allow basic inheritance of methods for value classes, but they incur the overhead of allocation. For example,

    val w = new Wrapper(3)
    w.print() // actually requires instantiating a Wrapper instance
  }

  // Note: You can use case classes and/or extension methods for cleaner syntax in practice.
  // but value class parameter must not be a var
  final case class Meter(value: Double) extends AnyVal with Printable {

    def + (m: Meter): Meter = Meter(value + m.value)

    override def print(): Unit = println(this.value)
  }

  def usages(): Unit = {
    //region Extension methods

    // One use case for value classes is to combine them with implicit classes (SIP-13) for allocation-free extension methods.
    // Using an implicit class provides a more convenient syntax for defining extension methods,
    // while value classes remove the runtime overhead.
    // A good example is the RichInt class in the standard library. RichInt extends the Int type with several methods.
    // Because it is a value class, an instance of RichInt doesn’t need to be created when using RichInt methods.

    // The following fragment of RichInt shows how it extends Int to allow the expression 3.toHexString:
    //  implicit class RichInt(val self: Int) extends AnyVal {
    //    def toHexString: String = java.lang.Integer.toHexString(self)
    //  }

    // At runtime, this expression 3.toHexString is optimised to the equivalent of a method call
    // on a static object (RichInt$.MODULE$.extension$toHexString(3)), rather than a method call on a newly instantiated object.

    //endregion Extension methods

    //region Correctness

    // Another use case for value classes is to get the type safety of a data type without the runtime allocation overhead.
    // For example, a fragment of a data type that represents a distance might look like:

    // Code that adds two distances, such as

    val x = Meter(3.4)
    val y = Meter(4.3)
    val z = x + y

    // will not actually allocate any Meter instances, but will only use primitive doubles at runtime.

    //endregion Correctness
  }

  /** Because the JVM does not support value classes, Scala sometimes needs to actually instantiate a value class.
    * Full details may be found in SIP-15.
    */
  def allocation(): Unit = {

    // Allocation Summary
    // A value class is actually instantiated when:
    //    a value class is treated as another type.
    //    a value class is assigned to an array.
    //    doing runtime type tests, such as pattern matching.

    //region Allocation Details

    // Whenever a value class is treated as another type, including a universal trait, an instance of the actual value class must be instantiated.

    // A method that accepts a value of type Printable will require an actual Meter instance.
    // In the following example, the Meter classes are actually instantiated:
    def print1(p: Printable): Unit = p.print()

    print1(Meter(0))

    // If the signature were instead, then allocations would not be necessary.
    def print2(p: Meter): Unit = p.print()

    print2(Meter(0))

    // Another instance of this rule is when a value class is used as a type argument.
    // For example, the actual Meter instance must be created for even a call to identity:
    def identity[T](t: T): T = t

    identity(Meter(5.0))

    // Another situation where an allocation is necessary is when assigning to an array, even if it is an array of that value class.
    val m = Meter(5.0)
    // The array here contains actual Meter instances and not just the underlying double primitives.
    val array = Array[Meter](m)

    // Lastly, type tests such as those done in pattern matching or asInstanceOf require actual value class instances:

    val p = Meter(3)
    p match {
      // new P instantiated here
      case Meter(3) => println("Matched 3")
      case Meter(x) => println("Not 3")
    }

    //endregion Allocation Details
  }

  /** Value classes currently have several limitations, in part because the JVM does not natively support the concept of value classes.
    * Full details on the implementation of value classes and their limitations may be found in SIP-15.
    */
  def limitations(): Unit = {

    // A value class …
    //    … must have only a primary constructor with exactly one public, val parameter whose type is not a value class.
    //        (From Scala 2.11.0, the parameter may be non-public.)
    //    … may not have specialized type parameters.
    //    … may not have nested or local classes, traits, or objects
    //    … may not define a equals or hashCode method.
    //    … must be a top-level class or a member of a statically accessible object
    //    … can only have defs as members. In particular, it cannot have lazy vals, vars, or vals as members.
    //    … cannot be extended by another class.
  }

}
