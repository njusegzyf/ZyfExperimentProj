/*
package nju.seg.zhangyf.scala.scalalangexamples

import scala.language.unsafeNulls
import javax.script.ScriptContext

/**
  * @author Zhang Yifan
  */
private object ScalaScriptExample {

  def main(args: Array[String]): Unit = {
    import javax.script.{ Bindings, ScriptEngine, ScriptEngineManager, SimpleBindings }

    val m = new ScriptEngineManager()
    val engine: ScriptEngine = m.getEngineByName("scala")

    // 设置需要的属性
    val settings = engine.asInstanceOf[scala.tools.nsc.interpreter.IMain].settings
    settings.usejavacp.value = true //使用程序的class path作为engine的class path

    if (this.isTestEvalForeach) {
      engine.put("m", Integer.valueOf(10))
      engine.eval("1 to m.asInstanceOf[Int] foreach println")
      engine.eval( raw"""val count = 20
                         |(m.asInstanceOf[Int] to m.asInstanceOf[Int] + 20) foreach println""".stripMargin)

      // Error, can't get m
      //      val m = engine.get("m")
      //      System.out.println(raw"The type of m is ${m.getClass.getName}")
    }

    if (this.isTestBindings) {
      // val bindings: Bindings = new SimpleBindings()
      val bindings: Bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE)

      engine.eval("val function1 = (z : Int) => z + 1", bindings)

      // Error, can't get function1
      //      val function1FormBindings = bindings.get("function1")
      //      Assert.assertTrue(function1FormBindings != null)
      //      Assert.assertTrue(function1FormBindings.isInstanceOf[Function1[_, _]])
    }

    if (this.isTestEvalOutside) {
      val function = engine.eval("(z : Int) => z + 1")
      System.out.println(raw"The type of function eval is ${ function.getClass.getName }")
      System.out.println(raw"The object of function eval is an instance of Function1 : ${ function.isInstanceOf[Function1[_, _]] }")
      // System.out.println(raw"The object of function eval is an instance of Function1 : ${ function.isInstanceOf[(_) => _] }")

      val functionInstance: Int => Int = function.asInstanceOf[Int => Int]

      // execute the function
      Assert.assertEquals(functionInstance(10), 10 + 1)
    }

    if (this.isTestBindings) {
      val bindings = new SimpleBindings()

      engine.eval("val start = 10", bindings)
      engine.eval("val end = 20", bindings)
      engine.eval("val func : (Int => Unit) = (z : Int) => println(z)", bindings)
      engine.eval("(start to end) foreach func", bindings)
    }

    if (this.isTestUsingCustomClass) {
      val bindings = new SimpleBindings()

      //      import scala.collection.JavaConversions
      //      import nju.seg.zhangyf.powerCollections.Iterables
      //      val iter = JavaConversions.iterableAsScalaIterable(Iterables.IntRangeForward(0,10))
      //      iter foreach println

      engine.eval( raw"""import scala.collection.JavaConversions
                         |import nju.seg.zhangyf.powerCollections.Iterables
                         |val iter = JavaConversions.iterableAsScalaIterable(Iterables.IntRangeForward(0,10))
                         |iter foreach println
                         |""".stripMargin,
                   bindings)
    }
  }

  val isTestEvalForeach: Boolean = true
  val isTestBindings: Boolean = true
  val isTestEvalOutside: Boolean = true
  val isTestEvalInside: Boolean = true
  val isTestUsingCustomClass: Boolean = true

}
*/
