package nju.seg.zhangyf.experiment.scalalang

import scala.util.{ Failure, Success, Try }

import javax.annotation.ParametersAreNonnullByDefault

/**
  * 用try-catch的模式,异常必须在抛出的时候马上处理.
  * 然而在分布式计算中,我们很可能希望将异常集中到一起处理,来避免需要到每台机器上单独看错误日志的窘态.
  * Try实例可以序列化,并且在机器间传送.
  *
  * @see [[http://segmentfault.com/a/1190000003068853]]
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
private object TryExample {

  def test(): Unit = {

    // 传统异常处理
    val res = try {
      1024 / 0
    } catch {
      case e: Throwable => e.printStackTrace()
    }

    val seq = Seq(0, 1, 2, 3, 4)
    //seq: Seq[Int] = List(0, 1, 2, 3, 4)

    val seqTry =
      seq.map(x => Try {
        20 / x
      })
    //seqTry: Seq[scala.util.Try[Int]] = List(Failure(java.lang.ArithmeticException: devide by zero),Success(20), Success(10), Success(6), Success(5))

    val succSeq = seqTry.flatMap(_.toOption)
    //succSeq: Seq[Int] = List(20, 10, 6, 5) Try可以转换成Option
    val succSeq2: Seq[Int] =
      seqTry.collect {
        // catch block as function
        case Success(x) => x
      }
    //succSeq2: Seq[Int] = List(20, 10, 6, 5) 和上一个是一样的
    val failSeq: Seq[Throwable] =
      seqTry.collect {
        case Failure(e) => e
      }
    //failSeq: Seq[Throwable] = List(java.lang.ArithmeticException: devide by zero)
  }

}
