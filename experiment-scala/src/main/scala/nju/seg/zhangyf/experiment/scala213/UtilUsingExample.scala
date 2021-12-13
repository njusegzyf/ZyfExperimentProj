package nju.seg.zhangyf.experiment.scala213

import scala.util.Try

import java.io.{ BufferedReader, FileReader }
import java.nio.file.Path

/**
 * @author Zhang Yifan
 */
private object UtilUsingExample {

  /**
   * @see [[scala.util.Using]]
   * @see [[scala.util.Using.Releasable]]
   * @see [[https://www.baeldung.com/scala/try-with-resources]]
   */
  def demoUtilUsingExample(): Unit = {

    import scala.util.Using

    // `Releasable` is a type class, and there is predefined implicit `Releasable` for `AutoCloseables`,
    // so it works like try-with-resources statement in Java.

    val firstLine: Try[String | Null] =
      Using(new BufferedReader(new FileReader("Z:/test.txt"))) { reader =>
        reader.readLine()
      }

    val encodingResults: Try[(String | Null, String | Null)] =
      Using.Manager { use =>
        val reader1 = use(new FileReader("Z:/test1.txt"))
        val reader2 = use(new FileReader("Z:/test2.txt"))
        (reader1.getEncoding, reader2.getEncoding)
      }
  }

}
