package nju.seg.zhangyf.experiment.scala212

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @author Zhang Yifan
  */
//noinspection ScalaUnusedSymbol
@ParametersAreNonnullByDefault
private object LibraryImprovementsExample {

  /** Either is now right-biased. */
  def testEither(): Unit = {
    // Either now supports operations like map, flatMap, contains, toOption, and so forth, which operate on the right-hand side.
    // The .left and .right methods may be deprecated in favor of .swap in a later release.
    // The changes are source-compatible with existing code (except in the presence of conflicting extension methods).
    // This change has allowed other libraries, such as cats to standardize on Either.

    val e0: Either[String, Double] = Right(1.0)

    val e1: Either[String, BigDecimal] = e0 map { BigDecimal(_) }
    // before we should project and then map
    // val e2: Either[String, BigDecimal] = e0.right map { BigDecimal(_) }

    // to map on the left, we can swap and then map
    val e3: Either[Double, Int] = e0.swap map Integer.parseInt

    // `flatMap` binds the given function across `Right`.
    // Either[+A, +B], def flatMap[A1 >: A, B1](f: B => Either[A1, B1]): Either[A1, B1]
    val e4: Either[String, BigDecimal] = e0 flatMap { (d: Double) => Right(BigDecimal(d)) }

    assert(e0 contains 1.0)
    val opt: Option[BigDecimal] = e4.toOption
  }

}
