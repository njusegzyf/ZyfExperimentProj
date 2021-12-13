package nju.seg.zhangyf.experiment.scala3.typesystem

import java.util.Comparator
import javax.annotation.ParametersAreNonnullByDefault

/**
 * @see [[https://docs.scala-lang.org/scala3/book/ca-contextual-abstractions-intro.html]]
 * @see [[https://docs.scala-lang.org/scala3/book/ca-given-using-clauses.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private[scala3] object ContextualAbstractionExample {

  trait Comparator[T] {
    def compare(a: T, b: T): Int
  }

  given Comparator[Float] with {
    def compare(a: Float, b: Float): Int = a.compareTo(b)
  }

  given Comparator[Double] = new Comparator[Double] {
    def compare(a: Double, b: Double): Int = a.compareTo(b)
  }

  given intComparator: Comparator[Int] = (a, b) => a.compareTo(b)

  // given instances are imported by types

  import ContextualAbstractionExample.{ given Comparator[Int] } // import one given instance by type
  import ContextualAbstractionExample.{ given } // a given selector brings all givens(including those resulting from extensions) into scope
  import ContextualAbstractionExample.{ given Comparator[?] }
  // since givens can be anonymous, it’s not always practical to import them by their name
  import ContextualAbstractionExample.{ intComparator2 }

  // The wildcard selector * brings all definitions other than givens or extensions into scope,
  // whereas a given selector brings all givens—including those resulting from extensions—into scope.
  import ContextualAbstractionExample.*
  // These rules have two main benefits:
  // It’s more clear where givens in scope are coming from. In particular, it’s not possible to hide imported givens in a long list of other wildcard imports.
  // It enables importing all givens without importing anything else.
  // This is particularly important since givens can be anonymous, so the usual use of named imports is not practical.
  // @see [[https://docs.scala-lang.org/scala3/book/packaging-imports.html]]

  // Note: `import xxx._` still brings all definitions including givens and extensions into scope
  import ContextualAbstractionExample._

  def isEqual[T](a: T, b: T)(using cmp: Comparator[T]): Boolean = cmp.compare(a, b) == 0

  def testIsEqual(): Unit = {
    assert(isEqual(0, 0))
  }

  // previous implementation: implicit object and implicit paratmeter
  implicit object intComparator2 extends Comparator[Int] {
    def compare(a: Int, b: Int): Int = a.compareTo(b)
  }

  def isEqual2[T](a: T, b: T)(implicit cmp: Comparator[T]): Boolean = cmp.compare(a, b) == 0

}
