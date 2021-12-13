package nju.seg.zhangyf.experiment.scala3.typesystem

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @see [[https://docs.scala-lang.org/scala3/reference/other-new-features/explicit-nulls.html]]
 * @note Binary Compatibility: Our strategy for binary compatibility with Scala binaries that predate explicit nulls
 *       and new libraries compiled without `-Yexplicit-nulls` is to leave the types unchanged and be compatible but unsound.
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object ExplicitNullExample {

  def demoBasic(): Unit = {
    // Explicit nulls is an opt-in feature that modifies the Scala type system, which makes reference types (anything that extends AnyRef) non-nullable.
    // This means the following code will no longer typecheck:

    // val x: String = null // error: found `Null`, but required `String`
    val x: String | Null = null
  }

  // TODO

}
