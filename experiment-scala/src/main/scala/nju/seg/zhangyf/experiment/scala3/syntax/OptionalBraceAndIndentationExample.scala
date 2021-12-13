package nju.seg.zhangyf.experiment.scala3.syntax

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @see [[https://docs.scala-lang.org/scala3/reference/other-new-features/indentation.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object OptionalBraceAndIndentationExample {

  def demoOptionalBraces(): Unit = {
    // The compiler will insert <indent> or <outdent> tokens at certain line breaks.
    // Grammatically, pairs of <indent> and <outdent> tokens have the same effect as pairs of braces { and }.

    val number: Int = 10
    val numberStr: String =
      number match
        case 1  => "one"
        case 10 => "ten"

  }

}
