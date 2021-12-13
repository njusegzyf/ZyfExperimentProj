package nju.seg.zhangyf.experiment.scala213

import scala.collection.{ immutable, mutable }

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @see [[https://github.com/scala/scala/releases/tag/v2.13.0]]
  * @see [[https://docs.scala-lang.org/overviews/collections-2.13/introduction.html]]
  * @see [[https://docs.scala-lang.org/overviews/core/collections-migration-213.html]]
  *
  * @author Zhang Yifan
  */
//noinspection ScalaUnusedSymbol,ScalaUnusedExpression
@ParametersAreNonnullByDefault
object CollectionExample extends App {

  // with org.scalatest.Assertions
  // Note: `org.scalatest.Assertions` in `scalactic_2.13.0-M5/3.1.0-RC1` does not compile with the following error:
  //    [ERROR] error: java.lang.NoSuchMethodError: scala.collection.SetOps.$plus$plus(Lscala/collection/Iterable;)Lscala/collection/SetOps;
  //    [INFO] 	at org.scalactic.BooleanMacro.<init>(BooleanMacro.scala:78)
  //    ......

  /**
    * @see [[https://docs.scala-lang.org/overviews/collections-2.13/overview.html]]
    */
  def demoMutableAndImmutableCollections(): Unit = {

    // A mutable collection can be updated or extended in place. This means you can change, add, or remove elements of a collection as a side effect.
    // Immutable collections, by contrast, never change. You have still operations that simulate additions, removals, or updates,
    // but those operations will in each case return a new collection and leave the old collection unchanged.

    // Most collection classes needed by client code exist in three variants,
    // which are located in packages scala.collection, scala.collection.immutable, and scala.collection.mutable, respectively.
    // Each variant has different characteristics with respect to mutability.
    // A collection in package scala.collection.immutable is guaranteed to be immutable for everyone. Such a collection will never change after it is created.
    // A collection in package scala.collection.mutable is known to have some operations that change the collection in place.

    // A collection in package scala.collection can be either mutable or immutable.
    // For instance, collection.IndexedSeq[T] is a superclass of both collection.immutable.IndexedSeq[T] and collection.mutable.IndexedSeq[T].
    // Generally, the root collections in package scala.collection support transformation operations affecting the whole collection,
    // the immutable collections in package scala.collection.immutable typically add operations for adding or removing single values,
    // and the mutable collections in package scala.collection.mutable typically add some side-effecting modification operations to the root interface.

    val generalIndexSeq: IndexedSeq[Int] = IndexedSeq()
    val immutableIndexSeq: immutable.IndexedSeq[Int] = immutable.IndexedSeq()
    val mutableIndexSeq: mutable.IndexedSeq[Int] = mutable.IndexedSeq()

    // By default, Scala always picks immutable collections.
    // For instance, if you just write Set without any prefix or without having imported Set from somewhere, you get an immutable set.
    val defaultSet: Set[Int] = Set()
    assert(defaultSet.isInstanceOf[immutable.Set[Int]])

    // A useful convention if you want to use both mutable and immutable versions of collections is to import just the package collection.mutable.
    //    import scala.collection.mutable
    // Then a word like Set without a prefix still refers to an immutable collection, whereas mutable.Set refers to the mutable counterpart.

    // The last package in the collection hierarchy is scala.collection.generic.
    // This package contains building blocks for abstracting over concrete collections.
  }

  /**
    * @see [[https://docs.scala-lang.org/overviews/collections-2.13/creating-collections-from-scratch.html]]
    */
  def demoCreatingCollectionsFromScratch(): Unit = {
    // You have syntax List(1, 2, 3) to create a list of three integers and Map('A' -> 1, 'C' -> 2) to create a map with two bindings.
    // This is actually a universal feature of Scala collections. You can take any collection name and follow it by a list of elements in parentheses.
    // The result will be a new collection with the given elements. Here are some more examples:

    val (dog, cat, bird) = (null, null, null)

    Iterable() // An empty collection
    Iterable.empty
    List() // The empty list
    List(1.0, 2.0) // A list with elements 1.0, 2.0
    Vector(1.0, 2.0) // A vector with elements 1.0, 2.0
    Iterator(1, 2, 3) // An iterator returning three integers.
    Set(dog, cat, bird) // A set of three animals
    immutable.HashSet(dog, cat, bird) // A hash set of the same animals
    Map('a' -> 7, 'b' -> 0) // A map from characters to integers

    // “Under the covers” each of the above lines is a call to the apply method of some object. For instance, `List(1.0, 2.0)` expands to
    List.apply(1.0, 2.0) // `List(1.0, 2.0)`

    // So this is a call to the apply method of the companion object of the List class.
    // That method takes an arbitrary number of arguments and constructs a list from them.
    // Every collection class in the Scala library has a companion object with such an apply method.
    // It does not matter whether the collection class represents a concrete implementation, like List, LazyList or Vector,
    // or whether it is an abstract base class such as Seq, Set or Iterable.
    // In the latter case, calling apply will produce some default implementation of the abstract base class.
    // Examples:

    List(1, 2, 3) // List[Int] = List(1, 2, 3)
    Iterable(1, 2, 3) // Iterable[Int] = List(1, 2, 3)
    mutable.Iterable(1, 2, 3) // mutable.Iterable[Int] = mutable.ArrayBuffer(1, 2, 3)

    // Besides apply, every collection companion object also defines a member empty, which returns an empty collection.

    // The operations provided by collection companion objects are summarized in the following table. In short, there’s
    //    concat, which concatenates an arbitrary number of collections together,
    //    fill and tabulate, which generate single or multi-dimensional collections of given dimensions initialized by some expression or tabulating function,
    //    range, which generates integer collections with some constant step length, and
    //    iterate and unfold, which generates the collection resulting from repeated application of a function to a start element or state.

    /* Factory Methods for Sequences
        C.empty 	The empty collection.
        C(x, y, z) 	A collection consisting of elements x, y, z.
        C.concat(xs, ys, zs) 	The collection obtained by concatenating the elements of xs, ys, zs.
        C.fill(n){e} 	A collection of length n where each element is computed by expression e.
        C.fill(m, n){e} 	A collection of collections of dimension m×n where each element is computed by expression e. (exists also in higher dimensions).
        C.tabulate(n){f} 	A collection of length n where the element at each index i is computed by f(i).
        C.tabulate(m, n){f} 	A collection of collections of dimension m×n where the element at each index (i, j) is computed by f(i, j). (exists also in higher dimensions).
        C.range(start, end) 	The collection of integers start … end-1.
        C.range(start, end, step) 	The collection of integers starting with start and progressing by step increments up to, and excluding, the end value.
        C.iterate(x, n)(f) 	The collection of length n with elements x, f(x), f(f(x)), …
        C.unfold(init)(f) 	A collection that uses a function f to compute its next element and state, starting from the init state.
     */
  }

  /**
    * @see [[https://docs.scala-lang.org/overviews/collections-2.13/conversions-between-java-and-scala-collections.html]]
    */
  def demoConversionsBetweenJavaAndScalaCollections(): Unit = {

    // Like Scala, Java also has a rich collections library. There are many similarities between the two.
    // For instance, both libraries know iterators, iterables, sets, maps, and sequences. But there are also important differences.
    // In particular, the Scala libraries put much more emphasis on immutable collections, and provide many more operations that transform a collection into a new one.

    // Sometimes you might need to pass from one collection framework to the other.
    // For instance, you might want to access an existing Java collection as if it were a Scala collection.
    // Or you might want to pass one of Scala’s collections to a Java method that expects its Java counterpart.
    // It is quite easy to do this, because Scala offers implicit conversions between all the major collection types in the JavaConverters object.
    // In particular, you will find bidirectional conversions between the following types.
    /*
        Iterator               <=>     java.util.Iterator
        Iterator               <=>     java.util.Enumeration
        Iterable               <=>     java.lang.Iterable
        Iterable               <=>     java.util.Collection
        mutable.Buffer         <=>     java.util.List
        mutable.Set            <=>     java.util.Set
        mutable.Map            <=>     java.util.Map / java.util.Dictionary (obsolete)
        mutable.ConcurrentMap  <=>     java.util.concurrent.ConcurrentMap
     */

    // To enable these conversions, simply import them from the JavaConverters object:

    // import scala.collection.JavaConverters._ // deprecated in Scala 2.13
    import scala.jdk.CollectionConverters._

    // This enables conversions between Scala collections and their corresponding Java collections by way of extension methods called `asScala` and `asJava`:
    val buffer: mutable.ArrayBuffer[Int] = mutable.ArrayBuffer(1, 2, 3)
    val jul: java.util.List[Int] = buffer.asJava
    val bufferConvertBack: mutable.Buffer[Int] = jul.asScala
    val jm: java.util.Map[String, Int] = immutable.HashMap("abc" -> 1, "hello" -> 2).asJava

    // Internally, these conversion work by setting up a “wrapper” object that forwards all operations to the underlying collection object.
    // So collections are never copied when converting between Java and Scala.
    // An interesting property is that if you do a round-trip conversion from, say a Java type to its corresponding Scala type,
    // and back to the same Java type, you end up with the identical collection object you have started with.
    assert(buffer eq bufferConvertBack)

    // Certain other Scala collections can also be converted to Java, but do not have a conversion back to the original Scala type:
    //    Seq           =>    java.util.List
    //    mutable.Seq   =>    java.util.List
    //    Set           =>    java.util.Set
    //    Map           =>    java.util.Map

    // Because Java does not distinguish between mutable and immutable collections in their type, a conversion from, say,
    // scala.immutable.List will yield a java.util.List, where all mutation operations throw an “UnsupportedOperationException”.

    val jul2 = immutable.List(1, 2, 3).asJava
    try {
      jul2.add(1)
      throw new IllegalStateException
    } catch {
      case e: UnsupportedOperationException => // we should get this runtime exception
      case _: Throwable                     => throw new IllegalStateException
    }

  }

  //region App

  this.demoMutableAndImmutableCollections()
  this.demoCreatingCollectionsFromScratch()
  this.demoConversionsBetweenJavaAndScalaCollections()

  //endregion App

}
