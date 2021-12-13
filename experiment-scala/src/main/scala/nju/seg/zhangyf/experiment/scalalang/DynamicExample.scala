package nju.seg.zhangyf.experiment.scalalang

import scala.collection.mutable
import scala.language.dynamics

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @author Zhang Yifan
  * @see [[http://docs.scala-lang.org/sips/completed/type-dynamic.html SIP-17 Type Dynamic]]
  */
@ParametersAreNonnullByDefault
private object DynamicExample {

  // 通过 import scala.language.dynamics 开启 Dynamic 功能，然后让需要实现动态方法的 Roman 继承 Dynamic trait 以获得 Dynamic 特性
  import scala.language.dynamics

  // notice : Dynamic is just an empty trait. All the heavy lifting is done by the compiler.

  /** @see [[http://www.blog.project13.pl/index.php/coding/1580/scala-2-10-class-ohmy-extends-dynamic/]] */
  def exp1(): Unit = {

    //region applyDynamic

    object d extends Dynamic {

      // the signature of applyDynamic takes the method name and it’s arguments.
      def applyDynamic(methodName: String)(args: Any*): Unit = {
        println( s"""|methodName: $methodName,
                     |args: ${ args.mkString(",") }""".stripMargin)
      }
    }

    //endregion applyDynamic

    //region applyDynamicNamed

    object json extends Dynamic {

      def applyDynamicNamed(name: String)(args: (String, Any)*): Unit = {
        val firstArg: (String, Any) = args.head
        println( s"""|Creating a $name, for:
                     |  "${ firstArg._1 }": "${ firstArg._2 }" """.stripMargin)
      }
    }

    // then can write code like:
    json.node(nickname = "ktoso")
    // output :
    //
    // Creating a node, for:
    //   "nickname": "ktoso"

    //endregion applyDynamicNamed

    //region selectDynamic

    // d.name trigger a compilation failure
    // Such methods (without ()) are treated special, as they would usualy represent fields

    object json2 extends Dynamic {

      def selectDynamic(name: String): String = s"I have $name!"
    }

    json2.bananas // will return "I have bananas!"

    //endregion applyDynamicNamed

    //region updateDynamic

    object magicBox extends Dynamic {

      private val box: mutable.Map[String, Any] = mutable.Map[String, Any]()

      def updateDynamic(name: String)(value: Any): Unit = { box(name) = value }

      def selectDynamic(name: String): Any = box(name)
    }

    magicBox.bananaField = "banana"
    magicBox.bananaField

    //endregion updateDynamic

    // see more : http://blog.csdn.net/bdmh/article/details/50147301
    // http://unmi.cc/scala-2-10-0-new-feature-dynamic/?utm_source=tuicool&utm_medium=referral
  }

  /**
    * 简单实现了一道非常著名的 Coding Kata 练习题，要实现的逻辑是把 I, II, IX 之类的罗马数字转换成阿拉伯数字.
    *
    * @see [[http://blog.csdn.net/bdmh/article/details/50147301]]
    */
  object Roman extends Dynamic {

    def selectDynamic(name: String): Int = {

      import scala.language.unsafeNulls
      val s = name.replaceAll("IV", "IIII").nn
              .replaceAll("IX", "VIIII").nn
              .replaceAll("XL", "XXXX").nn
              .replaceAll("XC", "LXXXX").nn

      s.count(_ == 'I') +
      s.count(_ == 'V') * 5 +
      s.count(_ == 'X') * 10 +
      s.count(_ == 'L') * 50 +
      s.count(_ == 'C') * 100
    }
  }

  def exp2(args: Array[String]): Unit = {
    println(Roman.VI) //  6
    println(Roman.X) // 10
    println(Roman.XII) // 12
    println(Roman.XV) // 90
  }

}
