package nju.seg.zhangyf.experiment.scala3.dropped

import javax.annotation.ParametersAreNonnullByDefault

/* Package objects will be dropped. They are still available in Scala 3.0, but will be deprecated and removed afterwards.
 * Package objects are no longer needed since all kinds of definitions can now be written at the top-level.
 *
 * @see [[https://docs.scala-lang.org/scala3/reference/dropped-features/package-objects.html]]
 */

private[scala3] type Labelled[T] = (String, T)

private[scala3] val a: Labelled[Int] = ("count", 1)

private[scala3] def b = a._2

implicit private[scala3] class TestImplicitClass(input: Int) {}
