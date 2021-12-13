package nju.seg.zhangyf.experiment.scala3

import javax.annotation.ParametersAreNonnullByDefault

import nju.seg.zhangyf.experiment.scala3.typesystem.ContextualAbstractionExample.Comparator

/**
 * @see [[https://docs.scala-lang.org/scala3/book/ca-extension-methods.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object ExtensionMethodExample {

  extension (intComparator: Comparator[Int])
    def reverseCompare(a: Int, b: Int): Int = intComparator.compare(b, a)

  extension[T] (comparator: Comparator[T]) {
    def reverseCompare2(a: T, b: T): Int = comparator.compare(b, a)
    def greater(a: T, b: T): Boolean = comparator.compare(a, b) > 0
  }

  import ExtensionMethodExample.reverseCompare2
  import nju.seg.zhangyf.experiment.scala3.typesystem.ContextualAbstractionExample.Comparator
  // import extension method

  // previous implementation: implicit classes
  implicit class RichComparator[T](comparator: Comparator[T]) {
    def reverseCompare(a: T, b: T): Int = this.comparator.compare(b, a)
  }

}
