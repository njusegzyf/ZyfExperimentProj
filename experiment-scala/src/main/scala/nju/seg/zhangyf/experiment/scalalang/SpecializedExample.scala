package nju.seg.zhangyf.experiment.scalalang

import scala.reflect.ClassTag

import java.{ lang => jl }
import javax.annotation.ParametersAreNonnullByDefault

/**
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
private object SpecializedExample extends App {

  //region Specialized method

  def overloadMethod(v: Any): Unit = println("Overload method for Object.")

  def overloadMethod(v: Boolean): Unit = println("Overload method for Boolean.")

  def nonSpecializedMethod[T](v: T): Unit = {
    println(v.getClass.toString)
    v match {
      // since `v` is an `AnyRef`, it can not be a boolean value, and thus the following case is test whether it is a boxed boolean (java.lang.Boolean)
      case _: Boolean => println("Non specialized method for Boxed Boolean.")
      case _: AnyRef  => println("Non specialized method for Object.")
      case _          => println("Unknown.")
    }
    println()
  }

  def specializedMethod1[@specialized T](v: T): Unit = {
    println(v.getClass.toString) // can be `Bool`

    // for specialized methods, the Scala compiler (2.11.8 and 2.13.1) generates code that
    // box the value to its boxed type, and than do pattern match as regular `AnyRef`.
    // This means the "case _ : Boolean " always do type test as "instanceOf java.lang.Boolean".
    v match {
      case v2: Boolean =>
        println(v2.getClass.toString) // always `java.lang.Boolean`
        println("Specialized method for value type Bool and Boxed Boolean.")

      // since v will be value types in the specialized methods, the following case is wrong :
      // case _ : AnyRef => println("Specialized method for Object.") // Error: isInstanceOf cannot test if value types are references.

      case _ => println("Specialized method for Object.")
    }
    println()
  }

  /** Even though the `Function1` is specialized, it only has specialized methods but there is only one `Function1` type.
    * So that all the specialized methods has an input of type Function0<Object> (except one with Function0<BoxedUnit>),
    * and invoke overloadMethod(Any).
    * However, for `val v = f()`, the specialized methods will unbox the value like:
    * boolean f1 = BoxesRunTime.unboxToBoolean(f.apply()).
    */
  def specializedMethod2[@specialized T](f: () => T): T = {
    val v: T = f()
    // It seems that the overload resolution will not be affected by specialization.
    // The invocations of `overloadMethod` in all specialized methods are static bind to `def overloadMethod(v: Any): Unit`
    // even for a specialized method for Boolean, which first box the boolean value and then invoke the method with following bytecode:
    //  INVOKESTATIC scala/runtime/BoxesRunTime.boxToByte (B)Ljava/lang/Byte;
    //  INVOKEVIRTUAL nju/seg/zhangyf/scala/otherExamples/SpecializedExample$.overloadMethod (Ljava/lang/Object;)V
    overloadMethod(v)
    v
  }

  def testSpecializedMethod(): Unit = {

    // direct invoke `overloadMethod`
    overloadMethod("") // print "Overload method for Object."
    overloadMethod(true) // print "Overload method for Boolean."
    println()

    // invoke `nonSpecializedMethod`
    nonSpecializedMethod(()) // parameter is `BoxedUnit`, print 'Non specialized method for Object.'
    nonSpecializedMethod("") // print "Non specialized method for Object."
    nonSpecializedMethod(true) // `true` is boxed, so print "Non specialized method for Boxed Boolean."
    nonSpecializedMethod(jl.Boolean.TRUE) // print "Non specialized method for Boxed Boolean."
    println()

    // invoke `nonSpecializedMethod`
    specializedMethod1(true) // boolean, and print "Specialized method for Boxed Boolean."
    specializedMethod1(jl.Boolean.TRUE) // java.lang.Boolean, print "Specialized method for Boxed Boolean."
    println()

    // invoke `overloadMethod` in `specializedMethod2`
    specializedMethod2 { () => "" } // print "Method for Object."
    specializedMethod2 { () => true } // print "Method for Object."
    println()
  }

  //endregion Specialized method

  //region Specialized class

  /** This class generates a `SpecializedClass<A>` Java class and 9 specialized Java class like
    * `SpecializedClass$mcB$sp extends SpecializedClass<Object>` for 9 sub type of `AnyVal` (8 Java primitive types and BoxedUnit).
    *
    * In Scala 2.11.8, the base class `SpecializedClass<A>` has a field for the `value` : `public final A value;`,
    * a method to test whether this instance is a specialized instance : `public boolean specInstance$() { return false; }`.
    * Each method like `public A value() { return (A)this.value; }` will have 9 specialized versions like:
    * `public short value$mcS$sp() { return BoxesRunTime.unboxToShort(value()); }`.
    *
    * A specialized class like `SpecializedClass$mcB$sp` looks like :
    * {{{
    * public class SpecializedExample$SpecializedClass$mcS$sp extends SpecializedExample.SpecializedClass<Object> {
    * public final short value$mcS$sp;
    * public SpecializedExample$SpecializedClass$mcS$sp(short value$mcS$sp) { super(null); }
    * public boolean specInstance$() { return true; }
    * public short value() { return value$mcS$sp(); }
    * public short value$mcS$sp() { return this.value$mcS$sp; }
    * }
    * }}}
    */
  class SpecializedClass[@specialized A](val value: A)

  def testSpecializedClass(): Unit = {

    // Since specialized java class extends the generic class, it is safe to use `Class` instances to do type tests like :
    val valueOfAnyRef: SpecializedClass[AnyRef] = new SpecializedClass(new AnyRef)
    val valueOfInt: SpecializedClass[Int] = new SpecializedClass(0)

    val classInstance = classOf[SpecializedClass[_]]
    assert(classInstance isInstance valueOfAnyRef)
    assert(classInstance isInstance valueOfInt)

    // note : `classOf[SpecializedClass[Int]]` will returns the same instance of `classOf[SpecializedClass[_]]`,
    // which is the class instance of the generic Java class `SpecializedClass<A>`,
    // so it is wrong to use it to do type tests.
    val errorIntClassInstance = classOf[SpecializedClass[Int]]
    assert(errorIntClassInstance == classInstance)
    assert(errorIntClassInstance isInstance valueOfAnyRef) // this is wrong and `errorIntClassInstance.isInstance(valueOfAnyRef)` will return true
    assert(errorIntClassInstance isInstance valueOfInt)

    // Instead use pattern match with `ClassTag` to do type tests on type parameters.
    def testValueOfType[A: ClassTag](v: SpecializedClass[_]): Boolean = {
      v.value match {
        case _: A => true
        case _    => false
      }
    }

    assert(!testValueOfType[Int](valueOfAnyRef))
    assert(testValueOfType[Int](valueOfInt))
  }

  //endregion Specialized class

  //region App

  this.testSpecializedMethod()
  this.testSpecializedClass()

  //endregion App

}
