package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/**
  * option可以看作是一个容器,容器的size是1或0
  * Size为1的时候就是一个Some[A](x: A),size为0的时候就是一个None
  *
  * @see [[http://segmentfault.com/a/1190000003068853]]
  * @see [[http://www.scala-lang.org/api/2.11.7/index.html#scala.Option]]
  * @author Zhang Yifan
  */
//noinspection ScalaUnusedExpression
@ParametersAreNonnullByDefault
private object OptionExample {

  def test(): Unit = {

    //region Methods of Option

    val a: Option[String] = Some("1024")
    val b: Option[String] = None
    a.map(_.toInt)
    //res0: Option[Int] = Some(1024)
    b.map(_.toInt)
    //res1: Option[Int] = None,不会甩exception
    a.filter(_ == "2048")
    //res2: Option[String] = None
    b.filter(_ == "2048")
    //res3: Option[String] = None
    a.getOrElse("2048")
    //res4: String = 1024
    b.getOrElse("2048")
    //res5: String = 2048
    a.map(_.toInt)
    .map(_ + 1)
    .map(_ / 5)
    .map(_ / 2 == 0) //res6: Option[Boolean] = Some(false)

    //endregion Methods of Option

    //region Option配合其他容器使用

    // option帮助你把错误扼杀在编译阶段
    // flatMap则可以在过滤空值的同时将option恢复为原始数据

    val as: Seq[String | Null] =
      Seq("1", "2", "3", null, "4")
    val bs: Seq[Option[String]] =
      Seq(Some("1"), Some("2"), Some("3"), None, Some("4"))

    as.filter(_ != null).map(_.nn.toInt)
    //res0: Seq[Int] = List(1, 2, 3, 4)
    //如果你忘了检查,编译器是看不出来的,只能在跑崩的时候抛异常
    bs.flatMap(_.map(_.toInt))
    //res1: Seq[Int] = List(1, 2, 3, 4)

    //endregion Option配合其他容器使用

    //region Scala原生容器类对Option有良好支持

    Seq(1, 2, 3).headOption
    //res0: Option[Int] = Some(1)

    Seq(1, 2, 3).find(_ == 5)
    //res1: Option[Int] = None

    Seq(1, 2, 3).lastOption
    //res2: Option[Int] = Some(3)

    Vector(1, 2, 3).reduceLeft(_ + _)
    //res3: Int = 6

    Vector(1, 2, 3).reduceLeftOption(_ + _)
    //res4: Option[Int] = Some(6)
    //在vector为空的时候也能用

    Seq("a", "b", "c", null, "d").map(Option(_))
    //res0: Seq[Option[String]] =
    // List(Some(a), Some(b), Some(c), None, Some(d))
    //原始数据转换成option也很方便

    //endregion Scala原生容器类对Option有良好支持
  }
}
