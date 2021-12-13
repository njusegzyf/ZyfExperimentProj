package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/** Type level programming examples in [[https://www.infoq.com/presentations/scala-patterns-types]].
  *
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
private object TypeLevelProgrammingExample {

  sealed trait Nat

  trait Succ[P <: Nat] extends Nat

  trait Zero extends Nat

  type One = Succ[Zero]
  type Two = Succ[One]
  type Three = Succ[Two]

  final case class Sized[TC, TN <: Nat](content: TC)

  def sized[T](): Sized[List[T], Zero] = Sized(List.empty)
  def sized[T](x: T): Sized[List[T], One] = Sized(List(x))
  def sized[T](x1: T, x2: T): Sized[List[T], Two] = Sized(List(x1, x2))
  def sized[T](x1: T, x2: T, x3: T): Sized[List[T], Three] = Sized(List(x1, x2, x3))

  def test(): Unit = {

    // `=:=` is a class defined in Predef, and if an implicit `A =:= B` (or =:=[A, B]) can be found, it means A and B are same type.
    implicitly[One =:= Succ[Zero]]
    implicitly[Two =:= Succ[Succ[Zero]]]
    implicitly[Three =:= Succ[Succ[One]]]
    implicitly[Succ[One] =:= Succ[Succ[Zero]]]

    // error: Cannot prove that Succ[Zero] =:= Two.
    // implicitly[Succ[Zero] =:= Two]

    def csv[N <: Nat](hdrs: Sized[List[String], N], rows: List[Sized[List[String], N]]): String = ???

    csv(sized("Date1"),
        List(sized("Mon"),
             sized("Tue")))

    csv(sized("Date1", "Data2"),
        List(sized("Mon", "Low"),
             sized("Tue", "High")))

    //    // compile error, type mismatch
    //    csv(sized("Date"),
    //        List(sized("Mon", "Low"),
    //             sized("Tue", "High")))
  }

}
