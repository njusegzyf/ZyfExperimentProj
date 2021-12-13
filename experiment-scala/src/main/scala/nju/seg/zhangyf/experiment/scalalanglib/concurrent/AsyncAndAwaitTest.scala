/*
package nju.seg.zhangyf.scala.scalalanglib.concurrent

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ ExecutionContextExecutor, Future }

import javax.annotation.ParametersAreNonnullByDefault

/**
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private final class AsyncAndAwaitTest {

  def test(): Unit = {
    import scala.async.Async.{ async, await }
    import scala.concurrent.{ Await, ExecutionContext, Promise }
    import scala.concurrent.duration.Duration
    implicit val executionContext: ExecutionContextExecutor = ExecutionContext.global

    // create 10 promises
    val promises = Vector.fill(10)(Promise[Int]())

    val allResFuture = async {
      // await all promises
      // notice: since for-expression is translated to function application, async and await can't be used with for-expression
      // but we can use them in while loop

      // buffer that collect results
      val resBuffer = new ArrayBuffer[Int](promises.size)

      var i = 0
      while (i < promises.size) {
        resBuffer += await(promises(i).future)
        i += 1
      }

      resBuffer
    }

    // complete all promises
    promises.zipWithIndex.foreach { pair => pair._1.success(pair._2) } // future.success(index)

    // wait all results
    val allRes = Await.result(allResFuture, Duration.Inf)

    // nju.seg.zhangyf.scala.base.testUtil.asserts.AssertionConversions(allRes).containsExactly(0 until 10: _*)

    // we can also do the same thing by transfer futures (using combinator `flatMap` and recursive function)

    // the tasks contains 10 functions that each does work and returns a future
    val tasks: List[() => Future[Int]] = List.tabulate(10) { i => { () => Future.successful(i) } }

    def collect(tasks: List[() => Future[Int]], results: ArrayBuffer[Int] = ArrayBuffer()): Future[ArrayBuffer[Int]] = {
      tasks match {
        case headTask :: tailTasks =>
          // get the first task and execute it, and use flatMap to execute remain tasks after it completed
          headTask() flatMap { result =>
            results += result // append the new result
            collect(tailTasks, results) // recursive call collect to execute the remain tasks
          }
        case _                     => // no more tasks, return results as a future
          Future.successful(results)
      }
    }

    // notice : if use map on `List[() => Future[Int]]` to map each task to a `Future`, then the execution is parallel
    // while the following execution is serial

    val allResFuture2: Future[ArrayBuffer[Int]] = collect(tasks)
    // wait all results
    val allRes2 = Await.result(allResFuture2, Duration.Inf)

    // nju.seg.zhangyf.scala.base.testUtil.asserts.AssertionConversions(allRes2).containsExactly(0 until 10: _*)
  }

}
*/
