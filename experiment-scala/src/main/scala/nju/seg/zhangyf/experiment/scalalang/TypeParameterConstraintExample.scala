// TODO: Fix for Scala 2.13

//package nju.seg.zhangyf.scala.scalalangexamples
//
//import scala.collection.IterableLike
//import scala.collection.generic.CanBuildFrom
//import scala.language.higherKinds
//
//import javax.annotation.ParametersAreNonnullByDefault
//
///**
//  * @author Zhang Yifan
//  */
//@ParametersAreNonnullByDefault
//private object TypeParameterConstraintExample {
//
//  def test(): Unit = {
//
//    /**
//      * @tparam CC   the type constructor representing the collection class
//      * @tparam Repr the actual type of the collection instance
//      */
//    final class RightCollectionOps[+E, CC[+X] <: IndexedSeq[X], +Repr <: CC[E] with IterableLike[E, Repr]]
//    (val instance: Repr) {
//      def map[To, That](f: E => To)(implicit bf: CanBuildFrom[Repr, To, That]): That = {
//        this.instance.map[To, That](f)
//      }
//    }
//
//    def testRightCollectionOps(): Unit = {
//      // `Vector` extends `IndexSeq`, so we want the `map` function returns a `Vector` instead of an `IndexSeq` instance.
//      val co = new RightCollectionOps[String, Vector, Vector[String]](Vector("1", "2"))
//      val intVector: Vector[Int] = co.map { Integer.parseInt }
//    }
//
//    /** @note The type of instance is wrong, it should be `Repr <: CC[T] with IterableLike[T, Repr]` instead of `CC[E]`.
//      *
//      * @tparam CC the type constructor representing the collection class
//      */
//    final class WrongCollectionOps[E, CC[+X] <: IndexedSeq[X]](val instance: CC[E]) {
//
//      /** @tparam Repr the actual type of the collection instance */
//      def mapOtherInstance[T, Repr <: CC[T] with IterableLike[T, Repr], To, That]
//      (col: Repr, f: T => To)
//      (implicit bf: CanBuildFrom[Repr, To, That])
//      : That = {
//        col.map[To, That](f)
//      }
//
//      // From `CC[X] <: IndexedSeq[X]`, `IndexSeq[+A] extends IndexedSeqLike[A, IndexedSeq[A]]`,
//      // `IndexedSeqLike[+A, +Repr] extends SeqLike[A, Repr]` and `SeqLike[+A, +Repr] extends IterableLike[A, Repr]`
//      // we only know that `CC[X] <: IterableLike[X, IndexSeq[X]]`.
//      // Since the map function is defined as `def map[B, That](f: A => B)(implicit bf: CanBuildFrom[Repr, B, That]): That`,
//      // and the compiler can only find an implicit `CanBuildFrom[IndexSeq[A], B, IndexSeq[B]]`,
//      // direct call map on a `CC[X]` instance can only returns an `IndexSeq[B]` instance.
//      // But we can use `implicit ev: CC[E] <:< IterableLike[E, CC[E]]` to let the compiler prove that `CC[E]` is a sub type of `IterableLike[E, CC[E]]`,
//      // so that we can safely cast the type of the collection instance to `IterableLike[E, CC[E]]`, then the map function returns a `CC` instance.
//      // We also use `implicit bf: CanBuildFrom[CC[E], To, That]` to let the compiler prove that we can build the target collection instance.
//
//      def map[To, That](f: E => To)
//                       (implicit ev: CC[E] <:< IterableLike[E, CC[E]], bf: CanBuildFrom[CC[E], To, That])
//      : That = {
//        val wrongTypeMapRes: IndexedSeq[To] = this.instance.map(f)
//        val rightTypeMapRes: That = this.instance.asInstanceOf[IterableLike[E, CC[E]]].map[To, That] { f }
//
//        rightTypeMapRes
//      }
//    }
//
//    def testWrongCollectionOps(): Unit = {
//      // `Vector` extends `IndexSeq`, so we want the `map` function returns a `Vector` instead of an `IndexSeq` instance.
//      val co = new WrongCollectionOps[String, Vector](Vector("1", "2"))
//      val intVector: Vector[Int] = co.map { Integer.parseInt }
//    }
//  }
//
//  // Also see Scala collection types.
//
//  trait MyIterable[+Element] extends Any {
//    def map[B](f: Element => B): MyIterable[B]
//    def chainedCall(): MyIterable[Element]
//    def companion: Any = ???
//  }
//
//  /**
//    * @tparam CC The type constructor representing the collection class.
//    *            This type is a higher-order and covariant type.
//    *            It will also be the return type of operations.
//    *
//    * @see [[scala.collection.generic.GenericTraversableTemplate]] and [[scala.collection.GenTraversable]]
//    */
//  trait MyIterableLike[+Element, +CC[+X] <: MyIterable[X]] extends MyIterable[Element] {
//    // Note we can not declare the self type to be `CC[Element]`, which will force `CC` to be actual type of the class the implements this trait.
//    // this : CC[Element] =>
//
//    override def map[B](f: Element => B): CC[B]
//
//    /** @note The return type is `CC[Element]` which is more specific than `MyIterableLike[Element, CC]`. */
//    def chainedCall(): CC[Element]
//  }
//
//  trait MySeq[+Element] extends MyIterable[Element]
//
//  trait MySeqLike[+Element, +CC[+X] <: MySeq[X]] extends MySeq[Element] with MyIterableLike[Element, CC]
//
//  abstract class AbstractSeq[+Element, +CC[+X] <: MySeq[X]] extends MySeqLike[Element, CC] {
//
//    def simpleMap[A >: Element](f: Element => A): CC[A] = this.map[A](f)
//
//    // Error, since `Element` is covariant, it can not occur in the contravariant return type of `f`.
//    // Otherwise, we can call a function of type `Int => Any` on an `Iterable[Int]` and gets an Iterable[Int].
//    // def simpleMap2(f : Element => Element) : CC[Element] = this.map[Element](f)
//  }
//
//  /**
//    * @tparam Repr The type of the sequence itself.
//    *
//    * @see [[scala.collection.LinearSeqOptimized]]
//    */
//  trait SeqOptimized[+Element, +Repr <: SeqOptimized[Element, Repr]] extends MyIterable[Element] {
//    this: Repr =>
//    // forces the `Repr` to be the actual type of the mixed class
//
//    override def chainedCall(): Repr = this
//  }
//
//  final class MyFinalSeq[+Element](val content: Seq[Element])
//    extends AbstractSeq[Element, MyFinalSeq]
//            with SeqOptimized[Element, MyFinalSeq[Element]] {
//    override def map[B](f: (Element) => B): MyFinalSeq[B] = ???
//  }
//
//  // Note : Since in `MyIterableLike[+Element, +CC[+X] <: MyIterableLike[Element, X]]` the type `CC` is covariant,
//  // we can provide a weak `CC` (WeakAbstractSeq) in an abstract class, and provide a stronger `CC`(WeakSeq) in the sub class.
//  // This means the `CC` can be override in sub classes.
//
//  abstract class WeakAbstractSeq[+Element] extends MyIterableLike[Element, WeakAbstractSeq]
//
//  class WeakSeq[+Element] extends WeakAbstractSeq[Element] with MyIterableLike[Element, WeakSeq] {
//    override def map[B](f: (Element) => B): WeakSeq[B] = ???
//    override def chainedCall(): WeakSeq[Element] = ???
//  }
//
//  def test2(): Unit = {
//    val mySeq: MyFinalSeq[Int] = new MyFinalSeq[Int](Seq(1, 2, 3, 4))
//    mySeq.chainedCall()
//    .chainedCall()
//    .map(_ + 1)
//  }
//}
