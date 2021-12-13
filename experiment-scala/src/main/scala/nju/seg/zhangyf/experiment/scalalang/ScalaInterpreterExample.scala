//package nju.seg.zhangyf.scala.scalalangexamples
//
///**
//  * @see [[scala.tools.nsc.interpreter.IMain]]
//  *
//  * @author Zhang Yifan
//  */
//private object ScalaInterpreterExample {
//
//  def main(args: Array[String]): Unit = {
//
//    // 创建设置, Embedded Scala REPL inherits parent classpath
//    // see : http://stackoverflow.com/questions/4121567/embedded-scala-repl-inherits-parent-classpath
//    val usedSettings = new scala.tools.nsc.Settings
//    usedSettings.usejavacp.value = true
//
//    //创建一个 Scala 解析器
//    // Note: `IMain` is an interpreter for Scala code.
//    import scala.tools.nsc.interpreter.IMain
//    val interpreter: IMain = ???
//    // Note: Error since Scala 2.13: new IMain(settings = usedSettings)
//
//    //解析器导入 Java 的 SimpleDateFormat 类, 别名为 SDF
//    interpreter.interpret("import java.text.{SimpleDateFormat => SDF}")
//
//    //绑定变量, 名称为 date, 类型为 java.util.Date, 并将新创建的 Date 实例作为参数传入
//    interpreter.bind("date", "java.util.Date", new java.util.Date)
//
//    //执行 Scala 代码, 返回格式化日期
//    val date: String = interpreter.interpret("""new SDF("yyyy-MM-dd").format(date)""").asInstanceOf[String]
//
//    System.out.println(raw"The data is : $date")
//
//    interpreter.close()
//  }
//
//}
