package nju.seg.zhangyf.experiment.scala3.syntax

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @see [[https://docs.scala-lang.org/scala3/reference/other-new-features/control-syntax.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object SyntaxChangeExample {

  /**
   * @see [[https://docs.scala-lang.org/scala3/reference/other-new-features/control-syntax.html]]
   */
  def demoControlSyntax(): Unit = {

    // Scala 3 has a new “quiet” syntax for control expressions that does not rely on enclosing the condition in parentheses,
    // and also allows to drop parentheses or braces around the generators of a for-expression. Examples:

    var x = 0
    var xStr =
      if x < 0 then "negative"
      else if x == 0 then "zero"
           else "positive"

    while x >= 0 do x = x - 1

    for x <- Seq(x, x) if x > 0
    yield x * x

    for
      x <- Seq(x, x)
      y <- Seq(x, x, x)
    do
      println(x + y)

    // try body
    // catch case ex: IOException => handle

    // The rules in detail are:
    //   The condition of an if-expression can be written without enclosing parentheses if it is followed by a then.
    //   The condition of a while-loop can be written without enclosing parentheses if it is followed by a do.
    //   The enumerators of a for-expression can be written without enclosing parentheses or braces if they are followed by a yield or do.
    //   A do in a for-expression expresses a for-loop.
    //   A catch can be followed by a single case on the same line. If there are multiple cases, these have to appear within braces (just like in Scala 2) or an indented block.
  }

  /**
   * @see [[https://docs.scala-lang.org/scala3/reference/other-new-features/creator-applications.html]]
   */
  def demoOptionalNewKeyword() : Unit = {

    // Scala case classes generate apply methods, so that values of case classes can be created using simple function application, without needing to write new.
    // Scala 3 generalizes this scheme to all concrete classes.
    //
    final class StringContainer(s: String) {
      def this() = this("")
    }

    // This works since a companion object with two apply methods is generated together with the class. The object looks like this:
    //object StringContainer:
    //  inline def apply(s: String): StringContainer = new StringContainer(s)
    //  inline def apply(): StringContainer = new StringContainer()

    val s1 = StringContainer("abc")  // old: new StringContainer("abc")
    val s2 = StringContainer()       // old: new StringContainer()
  }

}
