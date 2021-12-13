package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
private object BreakExample {

  def func(): Unit = {
    import scala.util.control.Breaks._

    val xs = 1 until 1000

    var sum = 0
    breakable {
      for (x <- xs) {
        if (x < 100) {
          sum = sum + x
        } else {
          break() // Exits the breakable block
        }
      }
    }
    assert(sum == 4950)

    var sum2 = 0
    breakable {
      var index = 0
      while (index < 1000) {
        if (xs(index) < 100) {
          sum2 = sum2 + xs(index)
        } else {
          break() // Exits the breakable block
        }
        index = index + 1
      }
    }
    assert(sum2 == 4950)
  }
}
