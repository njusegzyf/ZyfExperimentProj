package nju.seg.zhangyf.experiment.scala3.typesystem

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @see [[https://docs.scala-lang.org/scala3/book/types-union.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object UnionTypeExample {

  final case class Username(name: String)

  final case class Password(hash: BigInt)

  def help(id: Username | Password): String = id match {
    case Username(name) => name
    case Password(hash) => hash.toString()
    // case _: String      => ??? // Compile error since `String` can not match
  }

}
