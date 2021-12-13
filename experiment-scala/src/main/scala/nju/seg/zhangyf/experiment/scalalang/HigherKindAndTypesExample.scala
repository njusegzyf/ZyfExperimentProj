package nju.seg.zhangyf.experiment.scalalang

import scala.language.{ existentials, higherKinds, implicitConversions }

import java.io.Closeable
import javax.annotation.ParametersAreNonnullByDefault

/**
 * @see http://docs.scala-lang.org/style/naming-conventions.html#higher-kinds-and-parameterized-type-parameters
 * @author Zhang Yifan
 */
//noinspection ScalaUnusedSymbol
@ParametersAreNonnullByDefault
private object HigherKindAndTypesExample {

  //region Higher kinds

  // Key[_] means that the type `Key` is a higher order type that takes a type parameter.
  final class HigherOrderMap[Key[_], Value[_]]

  // `Seq` takes one type parameter, so that `Seq` can be used.
  val x1: HigherOrderMap[Seq, Seq] = new HigherOrderMap[Seq, Seq]

  // `V[_, _]` means that the Type `V` is a order kind type that takes two type parameters.
  final class HighOrderList[V[_, _]]

  // `V[A, B] <: Map[_, _]` means that the a `V[_, _]` must extends `Map[_, _]`.
  final class HighOrderList1[V[A, B] <: Map[C, D]]

  // It is the same as written `V[A, B] <: Map[C, D]`.
  final class HighOrderList2[V[A, B] <: Map[C, D]]

  // `V[X, Y] <: Map[X, Y]` means the a `V[A, B]` must extends `Map[C, D]`.
  final class HighOrderList3[V[X, Y] <: Map[X, Y]]

  // A high order type can also be covariant or contravariant.
  final class HighOrderList4[+V[X, Y] <: Map[X, Y]]

  val x2: HighOrderList[Map] = new HighOrderList[Map]

  // `KH[_[_, _]]` means that the type `KH` is a higher order type that takes a high order type parameter,
  // which is required to take two type parameters.
  final class HigherHigherOrder[K[_], KMap <: HigherOrderMap[K, K], KH[_[_, _]]]

  val x3: HigherHigherOrder[Seq, HigherOrderMap[Seq, Seq], HighOrderList] = new HigherHigherOrder[Seq, HigherOrderMap[Seq, Seq], HighOrderList]

  final class HigherHigherOrder2[K[X] <: Seq[X], KMap <: HigherOrderMap[K, K], KH[X[_, _]] <: HighOrderList[X]]

  val x4: HigherHigherOrder[Seq, HigherOrderMap[Seq, Seq], HighOrderList] = new HigherHigherOrder[Seq, HigherOrderMap[Seq, Seq], HighOrderList]

  //region Monad

  // import scalaz.Monad

  // the content bound : Monad offers the necessary evidence to inform the reader that M[_] is the type of the Monad
  // context bound 在 Scala 2.8.0 中引入，也被称作 type class pattern

  //  def monadPoint1[M[_] : Monad](v: Int): M[Int] = {
  //    val monad = implicitly[Monad[M]]
  //    monad.point(v)
  //  }

  // def monadPoint2[M[_]](v: Int)(implicit monad: Monad[M]): M[Int] = monadPoint1(v)

  //endregion Monad

  // +TC[X] <: Iterable[X] means that TC is a higher kind type,
  // and TC[X] should extends Iterable[X] while TC itself is covariant.
  class ZipMultiIterable[T, +TC[X] <: Iterable[X]]

  //endregion Higher kinds

  //region Existential type

  // Java bounded wildcards:
  //  java.util.List<String> s1 = ...;
  //  java.util.List<? extends Object> s2 = s1;
  //  java.util.List<Object> s3 = ...;
  //  java.util.List<? super String> s4 = s3;

  val s1: java.util.List[String] = new java.util.ArrayList[String]
  // val s2: java.util.List[T] forSome {type T <: Any} = s1
  // type `ListAny1` and `ListAny2` are the same as `java.util.List[T] forSome { type T <: Any }`
  type ListAny1 = java.util.List[_ <: Any]
  type ListAny2 = java.util.List[_]

  val s3: java.util.List[Any] = new java.util.ArrayList[Any]
  // val s4: java.util.List[T] forSome {type T >: String} = s3
  // type `ListSuperString` is the same as `java.util.List[T] forSome {type T >: String}`
  type ListSuperString = java.util.List[_ >: String]
  // val s5: ListSuperString = s4

  //endregion Existential type

  //region Structural type

  type StructuralType = {
    def call(): Unit
    val v: Int
  }

  final class ST {
    def call(): Unit = {}
    val v: Int = 12
  }

  // StructuralType can not be extended.
  // Error : class type required but AnyRef{def call(): Unit; val v: Int} found
  // final class ST2 extends StructuralType { ... }

  // notice that class ST does not extends StructuralType
  val stv1: StructuralType = new ST

  type StructuralType2[+T] = {def apply(i: Int): T}
  // @note In Scala 3, there is a type error.
  // val stv2: StructuralType2[String] = Map[Int, String]()
  // val stv3: StructuralType2[AnyRef] = stv2

  // We can not use `T` as the type of value parameter i. It is disallowed by the spec. See 3.2.7 Compound Types.
  // Error: Parameter type in structural refinement may not refer to an abstract type defined outside that refinement
  //   type StructuralType3[T] = {def apply(i: T): String}
  //  It roughly boils down to the fact that structural types use reflection and
  // the compiler does not know how to look up the method to call if it uses a type defined outside the refinement.
  // see more : http://stackoverflow.com/questions/7830731/parameter-type-in-structural-refinement-may-not-refer-to-an-abstract-type-defin

  // The following is allowed:
  type StructuralType3[T] = {

    // refer to a type definition within the refinement
    type ValueType = Int
    def apply(i: ValueType): T

    // @note In Scala 3, Polymorphic refinement method apply2 without matching type in parent class Object is no longer allowed
    // refer to a type parameter of the method itself
    // def apply2[TM](i: TM): T
  }

  //endregion Structural type

  //region Compound type

  type CloseableAndReadable = Closeable with Readable

  //endregion Compound type

  //region Singleton type

  // p.type is a singleton type which represents just the object denoted by p
  type SingletonType = HigherKindAndTypesExample.type

  // the only instance of a singleton type is the value (except null)
  val singletonTypeInstance1: SingletonType = HigherKindAndTypesExample
  val singletonTypeInstance2: SingletonType | Null = null

  // p.t is a path-dependent type when p is a value (i.e. an object) and t is a type
  type PathDependentType1 = HigherKindAndTypesExample.C
  // p.t is a shorthand for p.type#t (# denotes a type selection and the p.type must be a type not a value)
  type PathDependentType2 = HigherKindAndTypesExample.type#C

  // singleton type can be used for chain calls

  class C {
    protected var x: Int = 0

    // notice this method can only return `this`
    // it can't return `new C`, since this method may be inherited into some sub classes
    def incr(): this.type = { x = x + 1; this }
  }

  final class D extends C {
    def decr(): this.type = { x = x - 1; this }
  }

  // notice that d.incr() returns a value of this.type, thus in this case it is class D instead of class C
  // and we can call decr() (chaining of method calls)
  val d: D = new D
  d.incr().decr()

  //endregion Singleton type

  //region 类型限制 (=:= 和 <:<)

  // A =:= B 表示A类型等同于B类型
  // A <:< B 表示A类型是B类型的子类型
  //
  // 这个看上去很像操作符的 =:= 和 <:<，实际是两个类，它们在Predef里定义：
  //   sealed abstract class =:=[From, To] extends (From => To) with Serializable
  //   sealed abstract class <:<[-From, +To] extends (From => To) with Serializable
  // 因此，`A =:= B` 即为 `=:=[A, B]`

  def testMethod1(): Unit = {
    // 类型限制用在特定方法(specialized methods)的场景，所谓特定，是指方法只针对特定的类型参数才可以运行:
    // 隐式参数ev，它的类型是 `T <:< java.io.Serializable`，表示只有参数类型 T 是 `java.io.Serializable` 的子类型，才符合类型要求
    // ev 这个隐式参数也是由 `Predef` 里的隐式方法产生的
    //   private[this] final val singleton_<:< = new <:<[Any,Any] { def apply(x: Any): Any = x }
    //   implicit def conforms[A]: A <:< A = singleton_<:<.asInstanceOf[A <:< A]
    // 当调用test("hi")，编译器推断出 T 是 `String`，在寻找 `String <:< java.io.Serializable` 类型的隐式参数时，上下文中找不到，
    // 于是通过 `conforms` 隐式方法来产生一个， `conforms` 方法只有一个类型参数，它产生的结果是 `<:<[String, String]` 类型的对象，
    // 但因为 `<:<[-From, +To]` 第一个类型参数是逆变的，第二个类型参数是协变的，所以 `<:<[String, String]` 是 `<:<[String, java.io.Serializable]` 的子类型，满足要求。
    // 而调用 `test(2)` 时，因为隐式方法产生的 `<:<[Int,Int]` 不是 `<:<[Int, java.io.Serializable]` 的子类型，故在编译时刻抛出异常（找不到隐式变量）。
    // 可见这块编译器是利用函数类型的多态机制来实现类型检测的。
    def test1[T](i: T)(implicit ev: T <:< java.io.Serializable): Unit = { print("OK") }

    // is the same as:
    def test1E[T](i: T)(implicit ev: <:<[T, java.io.Serializable]): Unit = { print("OK") }

    test1("hi") // OK
    // test(2)  // error: Cannot prove that Int <:< java.io.Serializable.

    def test2[T1, T2](v1: T1, v2: T2)(implicit ev: T1 =:= T2): Unit = {}

    // 对于 `Type` 类型，在判断之间的关系时也有类似的写法，不过这里调用的是 `Type` 类型的方法
    // import scala.reflect.runtime.universe.typeOf
    // typeOf[List[_]] =:= typeOf[List[AnyRef]] // false
    // typeOf[List[Int]] <:< typeOf[Iterable[Int]] // true

    // <: 与 <:< 的差异

    def foo[A, B <: A](a: A, b: B): (A, B) = (a, b)

    // 传入第一个参数是Int类型，第二个参数是List[Int]，显然这不符合 B <: A 的约束
    // 编译器在做类型推导的时候，为了满足这个约束，会继续向上寻找父类型来匹配是否满足，于是在第一个参数被推导为Any类型的情况下，List[Int] 符合 Any 的子类型。
    val res1: (Any, List[Int]) = foo(1, List(1, 2, 3))

    // Note: 这种行为与 Javac 一致

    def bar[A, B](a: A, b: B)(implicit ev: B <:< A): (A, B) = (a, b)
    // bar(1, List(1, 2, 3)) // error: Cannot prove that List[Int] <:< Int.

    // 通过隐式参数 ev 来证明类型时，类型推断过程不会像上面那样再向上寻找可能满足的情况，而直接报错。
    // 因此用 <: 声明的类型约束，不如用 <:< 严格。

    // 除了上面的类型推导，存在隐式转换的情况下：
    implicit def int2ListInt(x: Int): List[Int] = List(x)

    foo(1, List(1)) // 存在A到B的隐式转换，也可满足，返回 (Int, List[Int])
    // bar(1, List(1))  // 隐式转换并不管用

    // 可以实现约束，要求两个参数的静态类型完全一致
    def typeEqualMethod[A, B](x: A, y: B)(implicit ev: A =:= B): Unit = ()

    typeEqualMethod(1, 1)
    // typeEqualMethod(new AnyRef, Integer.valueOf(0)) // compiler error, Cannot prove that Object =:= Integer.
    // typeEqualMethod(List(1), List(2.0)) // compiler error, Cannot prove that List[Int] =:= List[Double].

    //region 要求多个确界

    // 类型参数要求多个上确界，可以使用 Compound Types （http://docs.scala-lang.org/tutorials/tour/compound-types）
    // 也可以使用 类型限制 (=:= 和 <:<)
    def mulUpperBound1[T <: List[Int] with Closeable](): Unit = {}

    def mulUpperBound2[T]()(implicit ev1: T <:< List[Int], ev2: T <:< Closeable): Unit = {}

    // 类似的，类型参数要求多个下确界:
    // 这里要求 T1、T2 都是 T 的下确界，即 T 同时是 T1 和 T2 的 super type
    def mulLowerBound1[T, T1 <: T, T2 <: T](): Unit = {}

    def mulLowerBound2[T, T1, T2]()(implicit ev1: T1 <:< T, ev2: T2 <:< T): Unit = {}

    // 如果有确定的下确界类型，对于单个下确界可以写
    def singleLowerBound1[T >: List[Int]](): Unit = {}

    def singleLowerBound2[T]()(implicit ev: List[Int] <:< T): Unit = {}

    // 但是如果有多个确定的下确界类型，使用 Compound Types 是有问题的
    // 下面的类型限制要求 T 是 `List[Int] with java.util.ArrayList[Int]` 的父类型，即可以是任一种类型的父类型
    // 而不是要求 T 同时是 `List[Int]` 和 `java.util.ArrayList[Int]` 两种类型的子类型
    def mulLowerBound3[T >: List[Int] with java.util.ArrayList[Int]](v: T): Unit = {}

    mulLowerBound3(List(1))
    mulLowerBound3(new java.util.ArrayList[Int]())

    // 因此，只可以类型限制 (=:= 和 <:<)
    def mulLowerBound4[T](v: T)(implicit ev1: List[Int] <:< T, ev2: java.util.ArrayList[Int] <:< T): Unit = {}
    // mulLowerBound4(List(1)) // Error: Cannot prove that java.util.ArrayList[Int] <:< List[Int].
    // mulLowerBound4(new java.util.ArrayList[Int]()) // Error: Cannot prove that List[Int] <:< java.util.ArrayList[Int].

    def mulLowerBound5[T >: List[Int]]()(implicit ev: java.util.ArrayList[Int] <:< T): Unit = {}

    //endregion 要求多个确界
  }

  //endregion 类型限制

}
