package nju.seg.zhangyf.experiment.scalalang

import scala.collection.mutable
import scala.language.implicitConversions
import scala.language.unsafeNulls

import java.util
import javax.annotation.ParametersAreNonnullByDefault

// import org.scalatest.matchers.should.Matchers

/**
 * @see [[http://stackoverflow.com/questions/5598085 Where does scala look for implicits]]
 * @see [[http://stackoverflow.com/questions/4465948 What are scala context and view bounds]]
 * @see [[http://stackoverflow.com/questions/8623055 Scala: Implicit parameter resolution precedence]]
 *
 * @author Zhang Yifan
 */
//noinspection ScalaUnusedSymbol
@ParametersAreNonnullByDefault
private object ImplicitExample extends App { // with Matchers {

  //region Types of Implicits

  // 1. Implicit Conversion
  // 2. Implicit Parameters
  //    Note: the default value of an optional parameter has higher priority than implicit value
  // 3. Context Bounds `[T : Ordering]`
  // 4. View Bounds
  //    Note: View bounds are deprecated: SI-7629. It's better to replace them with implicit parameters.

  //endregion Types of Implicits

  def testImplicitLookup(): Unit = {
    // When the compiler sees the need for an implicit, either because you are calling a method which does not exist on the object's class,
    // or because you are calling a method that requires an implicit parameter, it will search for an implicit that will fit the need.
    //
    // This search obey certain rules that define which implicits are visible and which are not.
    // The following table showing where the compiler will search for implicits was taken from an excellent presentation about implicits by Josh Suereth,
    // which I heartily recommend to anyone wanting to improve their Scala knowledge. It has been complemented since then with feedback and updates.
    //
    // The implicits available under number 1 below has precedence over the ones under number 2.
    // Other than that, if there are several eligible arguments which match the implicit parameter’s type,
    // a most specific one will be chosen using the rules of static overloading resolution (see Scala Specification §6.26.3).

    // 1) implicits visible to current invocation scope via local declaration, imports, outer scope, inheritance, package object that are accessible without prefix.
    //    Implicits defined in current scope
    //    Explicit imports
    //    wildcard imports
    // 2) implicit scope, which contains all sort of companion objects and package object that bear some relation to the implicit's type which we search for
    // (i.e. package object of the type, companion object of the type itself, of its type constructor if any, of its parameters if any, and also of its supertype and supertraits).
    //    Companion objects of a type
    //    Implicit scope of an argument's type (2.9.1)
    //    Implicit scope of type arguments (2.8.0)
    //    Outer objects for nested types

    // If at either stage we find more than one implicit, static overloading rule is used to resolve it.

    //region Static overloading rules

    //    The relative weight of an alternative A over an alternative B is a number from 0 to 2, defined as the sum of
    //    - 1 if A is as specific as B, 0 otherwise, and
    //    - 1 if A is defined in a class or object which is derived from the class or object defining B, 0 otherwise.
    //
    //    A class or object C is derived from a class or object D if one of the following holds:
    //    - C is a subclass of D, or
    //    - C is a companion object of a class derived from D, or
    //    - D is a companion object of a class from which C is derived.
    //
    //    An alternative A is more specific than an alternative B if the relative weight of A over B is greater than the relative weight of B over A.
    //
    // For views, if A is as specific view as B, A gets a relative weight of 1 over B.
    // If A is defined in a derived class in which B is defined, A gets another relative weight.

    //endregion Static overloading rules

    //region 1) implicits visible to current invocation scope

    // Implicits defined in current scope
    implicit val nf: () => Int = { () => 5 }

    def add(x: Int)(implicit y: () => Int): Int = x + y()

    add(5) // takes nf from the current scope

    // Explicit imports
    import scala.collection.convert.ImplicitConversions.`map AsScala`
    // Java map
    def env: util.Map[String, String] = System.getenv()

    // implicit conversion from Java Map to Scala Map
    val envAsScalaMap: mutable.Map[String, String] = env
    // `map AsScala`(env)
    // implicit conversion from Java Map to Scala Map and then invokes `apply` method on Scala `map`
    val term: String = env("TERM") // `map AsScala`(env)("TERM")

    // Wildcard Imports
    def sum[T: Integral](list: List[T]): T = {
      val integral = implicitly[Integral[T]]
      import integral._
      // get the implicits in question into scope
      list.foldLeft(integral.zero)(_ + _)
    }

    //endregion implicits visible to current invocation scope

    //region 2) implicit scope

    //region 2.1) Companion Objects of a Type

    // There are two object companions of note here.

    //region First, the object companion of the "source" type is looked into.

    // For instance, inside the object Option there is an implicit conversion to Iterable, so one can call Iterable methods on Option,
    // or pass Option to something expecting an Iterable.
    // For example,
    for {
      x <- List(1, 2, 3)
      y <- Some('x')
    } yield (x, y)
    // That expression is translated by the compiler to
    List(1, 2, 3).flatMap { x => Some('x').map { y => (x, y) } }
    // is expanded to:
    List(1, 2, 3).flatMap { x => Option.option2Iterable(Some('x').map { y => (x, y) }) }
    // However, List.flatMap expects a TraversableOnce, which Option is not.
    // The compiler then looks inside Option's object companion and finds the conversion to Iterable, which is a TraversableOnce, making this expression correct.

    //endregion First, the object companion of the "source" type is looked into.

    //region Second, the companion object of the expected type:

    val sortedList1: List[Int] = List(1, 2, 3).sorted
    // is expanded to:
    val sortedList2: List[Int] = List(1, 2, 3).sorted(Ordering.Int)
    // The method sorted takes an implicit Ordering. In this case, it looks inside the object Ordering, companion to the class Ordering, and finds an implicit Ordering[Int] there.

    // Note that companion objects of super classes are also looked into.
    // For example:
    def test1(): Unit = {
      class A(val n: Int)
      object A {
        implicit def aToStr(a: A): String = s"A: ${ a.n }"
      }
      class B(val x: Int, y: Int) extends A(y)
      val b = new B(5, 2)
      val s: String = b // s == "A: 2"
      // Notice : Scala compiler finds the implicit conversion in the companion object `A`, which is the companion object of `B`'s super class `A`.
    }

    //endregion Second, the companion object of the expected type:

    // Note : Implicits in companion objects also have different priorities.
    // For example, `object Predef extends LowPriorityImplicits with DeprecatedPredef`,
    // This means implicits direct defined in `Predef` have highest priority, and then implicits defined in `LowPriorityImplicits` and then the ones in `DeprecatedPredef`.

    //endregion 2.1) Companion Objects of a Type

    //region 2.2) Implicit Scope of an Argument's Type (This is available since Scala 2.9.1)

    // If you have a method with an argument type A, then the implicit scope of type A will also be considered.
    // By "implicit scope" I mean that all these rules will be applied recursively --
    // for example, the companion object of A will be searched for implicits, as per the rule above.
    //
    // Note that this does not mean the implicit scope of A will be searched for conversions of that parameter, but of the whole expression.
    // For example:
    def test2(): Unit = {
      class A(val n: Int) {
        def + (other: A): A = new A(n + other.n)
      }
      object A {
        implicit def fromInt(n: Int): A = new A(n)
      }

      // This becomes possible:
      1 + new A(1)
      // because it is converted into this:
      A.fromInt(1) + new A(1) // Notice : `A.fromInt` is available for the whole expression `1 + new A(1)`
    }

    //endregion 2.2) Implicit Scope of an Argument's Type (This is available since Scala 2.9.1)

    //region 2.3) Implicit Scope of Type Arguments (This is available since Scala 2.8.0)

    // This is required to make the type class pattern really work.
    // Consider Ordering, for instance: It comes with some implicits in its companion object, but you can't add stuff to it.
    // So how can you make an Ordering for your own class that is automatically found?
    // Let's start with the implementation:
    def test3(): Unit = {
      class A(val n: Int)
      object A {
        implicit val ord: Ordering[A] = (x: A, y: A) => implicitly[Ordering[Int]].compare(x.n, y.n)
      }

      // So, consider what happens when you call
      List(new A(5), new A(2)).sorted
      // As we saw, the method sorted expects an Ordering[A] (actually, it expects an Ordering[B], where B >: A).
      // There isn't any such thing inside Ordering, and there is no "source" type on which to look.
      // Obviously, it is finding it inside A, which is a type argument of Ordering.

      // This is also how various collection methods expecting CanBuildFrom work: the implicits are found inside companion objects to the type parameters of CanBuildFrom.

      // Note: Ordering is defined as trait Ordering[T], where T is a type parameter.
      // Previously, I said that Scala looked inside type parameters, which doesn't make much sense.
      // The implicit looked for above is Ordering[A], where A is an actual type, not type parameter: it is a type argument to Ordering.
      // See section 7.2 of the Scala specification.
    }

    //endregion 2.3) Implicit Scope of Type Arguments (This is available since Scala 2.8.0)

    //region 2.4) Outer Objects for Nested Types

    def test4(): Unit = {
      class A(val n: Int) {

        class B(val m: Int) {
          require(m < n)
        }

      }
      object A {
        implicit def bToString(b: A#B): String = s"B: ${ b.m }"
      }
      val a = new A(5)
      val b = new a.B(3)
      val s: String = b // s == "B: 3"
    }

    //endregion 2.4) Outer Objects for Nested Types

    //endregion 2) implicit scope

    //region The compiler only search actual class/trait

    def test5(): Unit = {
      class A
      object A {
        class A1
      }

      object B {
        type A1 = A.A1
        implicit def a1ToString(x: A.A1): String = ""
      }

      // THe following is error, means that the compiler only search the actual class
      // shapeless.test.illTyped { "val strFromA1: String = new B.A1" }
      // Error: type mismatch;
      // found   : A.A1
      // required: String

    }

    def test6(): Unit = {
      class A
      object A {
        class A1

        implicit def a1ToString(x: A.A1): String = ""
      }

      object B {
        type A1 = A.A1
      }

      // THe following is Ok
      val strFromA1: String = new B.A1
    }

    //endregion The compiler only search actual class/trait

    //region Implicits in Predef

    // The `Predef` object provides implicit conversions that are accessible in all Scala compilation units without explicit qualification,
    // and there are considered as 1) implicits visible to current invocation scope.

    def test7(): Unit = {
      // `Predef` defines `int2Integer`
      // implicit def int2Integer(x: Int): java.lang.Integer = x.asInstanceOf[java.lang.Integer]
      implicit def customInt2Integer(x: Int): Integer = Integer.valueOf(x)

      val x: Int = 10
      // val xAsInteger: Integer = x
      // Error: type mismatch;
      // found   : Int
      // required: Integer
      // Note that implicit conversions are not applicable because they are ambiguous:
      // both method int2Integer in object Predef of type (x: Int)Integer
      // and method customInt2Integer of type (x: Int)Integer
      // are possible conversion functions from Int to Integer
    }

    def test8(): Unit = {
      class A
      object A {
        implicit def customInt2Integer(x: Int): java.lang.Integer = Integer.valueOf(x)
      }

      object B extends A {
        val x: Int = 10

        def test(): Unit = {
          val xAsInteger: Integer = x // picks `Predef.int2Integer` (it is considered as implicits visible to current invocation scope)
        }
      }
    }

    //endregion Implicits in Predef

    trait Implicit {
      implicit lazy val intFoo: Int = 0
    }
  }

  /** Review where we can define our implicits to design an API without explicitly importing implicits.
   *
   * @see [[http://eed3si9n.com/implicit-parameter-precedence-again Implicit parameter precedence again]]
   */
  def testDefineImplicits(): Unit = {
    // Category 1 (implicits loaded to current scope) should be avoided
    // if you want to let your user write their code in arbitrary packages and classes and want to avoid import.
    //
    // On the other hand, the entire Category 2 (implicit scope) is wide open.

    //region Companion object of type T (or its part)

    // The first place to consider is the companion object of an associated type (in this case a type constructor):

    trait CanFoo[A] {
      def foos(x: A): String
    }
    object CanFoo {
      implicit val companionIntFoo: CanFoo[Int] = (x: Int) => "companionIntFoo:" + x.toString
    }
    //    object `package` {
    //      def foo[A: CanFoo](x: A): String = implicitly[CanFoo[A]].foos(x)
    //    }

    // Now, this can be invoked as foopkg.foo(1) without any import statement.

    //endregion Companion object of type T (or its part)

    //region Package object of type T

    // Another place to consider is the parent trait of package object.

    /*
        package foopkg

        trait CanFoo[A] {
          def foos(x: A): String
        }
        trait Implicit {
          implicit lazy val intFoo = new CanFoo[Int] {
            def foos(x: Int) = "intFoo:" + x.toString
          }
        }
        object `package` extends Implicit {
          def foo[A:CanFoo](x: A): String = implicitly[CanFoo[A]].foos(x)
        }
     */

    // Placing implicits into a trait consolidates them into one place, and gives opportunity for the user to reuse them if needed.
    // Mixing it into the package object loads them into the implicit scope.

    //endregion Package object of type T

    //region Static monkey patching

    // A popular use of implicits is for static monkey patching.
    // For example, we can add yell method to String, which makes it upper case and appends "!!".
    // The technical term for this is called view:
    // A view from type S to type T is defined by an implicit value which has function type S=>T or (=>S)=>T or by a method convertible to a value of that type.

    /*

    // package yeller
    case class YellerString(s: String) {
      def yell: String = s.toUpperCase + "!!"
    }
    trait YellerImplicit {
      implicit def stringToYellerString(s: String): YellerString = YellerString(s)
    }
    // object `package` extends Implicit
    object Yeller extends YellerImplicit

    // Unfortunately, however, "foo".yell won't work outside of yeller package because the compiler doesn't know about possible the implicit conversion.
    // One workaround is to break into Category 1 (implicits loaded to current scope) by calling import yeller._:
    // This is not bad since the import is consolidated into one thing.
    def test(): Unit = {
      import Yeller._
      println("banana".yell)
    }

    // user's package object
    // Can we get rid of the import statement? Another place in Category 1 is the user's package object, to which they can mixin Implicit trait:

    // package userpkg
    // object `package` extends yeller.Implicit
    // object Main extends App {
    //  println("banana".yell)
    // }

    // We can also mix the `Implicit` trait into our classes or traits.
    object TestObj extends YellerImplicit {
      println("banana".yell)
    }

    */

    //endregion Static monkey patching
  }

  def testLambdaFromFunctionsWithImplicitParameters(): Unit = {

    def minAndMax[T, TN >: T](xs: Iterable[T])(implicit num: Numeric[TN]): (TN, TN) = ???

    type MinAndMaxTestCase[T, TN >: T] = (Iterable[T], (TN, TN))

    def testMinAndMax1[T, TN >: T](testMethod: Iterable[T] => (TN, TN), testCase: MinAndMaxTestCase[T, TN])
    : Unit = ???

    def testMinAndMax2[T, TN >: T](testMethod: Iterable[T] => Numeric[TN] => (TN, TN), testCase: MinAndMaxTestCase[T, TN])
                                  (implicit num: Numeric[TN])
    : Unit = ???

    val testCase: MinAndMaxTestCase[Int, Int] = (Array(0), (0, 0))

    // Here we pass method `minAndMax`, we can only pass it as a function of type `Iterable[T] => (TN, TN)` by binding its implicit parameters,
    // This means the implicit parameters must be bound when it is convert to a lambda.
    testMinAndMax1[Int, Int](minAndMax[Int, Int], testCase)
    // type error
    // shapeless.test.illTyped.illTyped { "testMinAndMax2[Int, Int](minAndMax[Int, Int], testCase)" }
  }

}
