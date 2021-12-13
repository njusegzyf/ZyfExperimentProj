package nju.seg.zhangyf.experiment.scalalanglib.concurrent

import scala.collection.immutable.IndexedSeq
import scala.concurrent.{ Await, ExecutionContext, ExecutionContextExecutor, Future, Promise }
import scala.concurrent.duration.Duration
import scala.language.unsafeNulls
import scala.util.{ Failure, Random, Success }

import java.io.IOException
import java.nio.file.{ Files, Path }
import javax.annotation.ParametersAreNonnullByDefault

import com.google.common.io.Files as GuavaFiles

/**
 * @author Zhang Yifan
 */
//noinspection ScalaUnusedSymbol
@ParametersAreNonnullByDefault
object FutureCombinatorExample {

  def basicUsage(): Unit = {

    implicit val executor: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
    val f = Future.successful(0.0)

    // onComplete, apply the provided function when this future is completed(either through an exception, or a value)
    // there are also `onSuccess` and `onFailure`
    f onComplete {
      case Success(v)  => println(s"Get value : $v .")
      case Failure(ex) => println(s"Failed for : ${ ex.printStackTrace() }")
    }

    // Note: `onSuccess` and `onFailure` are removed since Scala 2.13.
    // notice : if the PartialFunction passed to `onSuccess` and `onFailure` is not defined in the value or failure of future,
    // the PartialFunction will not be applied.
    //    f onSuccess { case 0.0 => println("Future success.") }

    val f2 = Future.failed(new IllegalArgumentException)
    //    f2 onFailure {
    //      case _: IllegalStateException    => println("Future failed with IllegalStateException")
    //      case _: IllegalArgumentException => println("Future failed with IllegalArgumentException")
    //    }

    // use `ready` and `result` method in `Await` to await
    val readyFuture: Future[Double] = Await.ready(f, Duration.Inf)
    val result: Double = Await.result(f, Duration.Inf)
  }

  def demoCombinators(): Unit = {

    implicit val executor: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

    val p = Promise[List[Int]]()
    val f = p.future
    val p1 = Promise[Option[Double]]()
    val f1 = p1.future
    val p2 = Promise[Option[Double]]()
    val f2 = p2.future

    // map, Creates a new future by applying a function to the successful result of this future.
    val mapFuture: Future[Int] = f map { _.size }

    // filter, Creates a new future by filtering the value of the current future with a predicate.
    // If the current future contains a value which satisfies the predicate, the new future will also hold that value.
    // Otherwise, the resulting future will fail with a NoSuchElementException. If the current future fails, then the resulting future also fails.
    val filterFuture: Future[List[Int]] = f filter { _.size >= 2 }

    // flatMap, Creates a new future by applying a function to the successful result of this future, and returns the result of the function as the new future.
    // If this future is completed with an exception then the new future will also contain this exception.
    // notice: different from `map` which accepts a function of type (T) => S, `flatMap ` accepts a function of type (T) => Future[S]
    // so `flatMap` is more general and powerful than `map`
    val flatMapFuture: Future[Int] = f flatMap { l => Future.successful(l.head) }

    // foreach, Asynchronously processes the value in the future once the value becomes available. Will not be called if the future fails.
    f foreach { l => println(f"${ l.mkString("") }") }

    // collect, Creates a new future by mapping the value of the current future, if the given partial function is defined at that value.
    // If the current future contains a value for which the partial function is defined, the new future will also hold that value.
    // Otherwise, the resulting future will fail with a NoSuchElementException. If the current future fails, then the resulting future also fails.
    val collectFuture: Future[Some[Int]] = f collect { case List(e1, e2, _*) => Some(e1) }

    // zip (join), Zips the values of this and that future, and creates a new future holding the tuple of their results.
    // If this future fails, the resulting future is failed with the throwable stored in this.
    // Otherwise, if that future fails, the resulting future is failed with the throwable stored in that.
    val zipFuture: Future[(List[Int], Option[Double])] = f zip f2

    // andThen, Applies the side-effecting function to the result of this future, and returns a new future with the result of this future.
    // This method allows one to enforce that the callbacks are executed in a specified order.
    // Note that if one of the chained andThen callbacks throws an exception, that exception is not propagated to the subsequent andThen callbacks.
    // Instead, the subsequent andThen callbacks are given the original value of this future.
    val andThenFuture: Future[List[Int]] =
    f andThen {
      case Success(v)  => v.size
      case Failure(ex) => 0 // handle for failure
    }

    // fallbackTo, Creates a new future which holds the result of this future if it was completed successfully,
    // or, if not, the result of the that future if that is completed successfully.
    // If both futures are failed, the resulting future holds the throwable object of the first future.
    // Using this method will not cause concurrent programs to become non-deterministic.
    val fallbackToFuture: Future[List[Int]] = f fallbackTo Future.successful(List(0, 1))

    // other combinators in object `Future` (usually for combining several futures)

    // firstCompletedOf, Returns a new Future to the result of the first future in the list that is completed.
    val firstCompletedFuture: Future[Option[Double]] = Future.firstCompletedOf(List(f1, f2))

    // foldLeft, A non-blocking fold over the specified futures, with the start value of the given zero.
    // The fold is performed on the thread where the last future is completed,
    // the result will be the first failure of any of the futures, or any failure in the actual fold, or the result of the fold.
    val foldFuture: Future[Double] = Future.foldLeft(List(f1, f2))(0.0) { (sum: Double, optV: Option[Double]) => optV.fold(sum) { _ + sum } }

    // traverse, Transforms a TraversableOnce[A] into a Future[TraversableOnce[B]] using the provided function A => Future[B].
    // This is useful for performing a parallel map.
    val traverseFuture: Future[Vector[Int]] = Future.traverse((0 until 10).toVector) { _ => doRpc() }

    // sequence, Transforms a TraversableOnce[Future[A} into a Future[TraversableOnce[A]].
    // Simple version of Future.traverse. Useful for reducing many Futures into a single Future.
    val sequenceFuture: Future[IndexedSeq[Int]] = Future.sequence { (0 until 10) map { _ => doRpc() } }

    // Returns a Future that will hold the optional result of the first Future with a result that matches the predicate.
    val findFuture: Future[Option[Option[Double]]] = Future.find(List(f1, f2)) { _.isDefined }
  }

  def demoCreateFutures(): Unit = {

    implicit val executor: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

    // create a promise and get its future
    val p = Promise[Int]()
    val f1 = p.future
    // set the promise to complete the future
    p.complete(Success(0))
    // p.complete(Failure(new Exception))
    // or
    //    p.success(0)
    //    p.failure(new Exception)

    // Starts an asynchronous computation and returns a Future object with the result of that computation.
    // The result becomes available once the asynchronous computation is completed.
    val f2 = Future { doSomeLongRunWork() } // Future.apply { doSomeLongRunWork() }

    // directly create futures
    val fSucc = Future.successful(1)
    val fFail = Future.failed(new IOException)
  }

  /** @see [[http://twitter.github.io/effectivescala/index-cn.html#%E5%B9%B6%E5%8F%91-Future]] */
  def testFlatMap(): Unit = {

    implicit val executor: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

    def collect(results: List[Int] = Nil): Future[List[Int]] =
    // def doRpc(): Future[Int]
      doRpc() flatMap { result =>
        if (results.length < 9) collect(result :: results)
        else Future.successful(results)
      }

    //    collect() onSuccess { case results: List[Int] => println(f"Got results ${ results.mkString("") }\n") }

    // notice : `onSuccess` accepts a `PartialFunction` (which is a sub type of `Function`), so the following is error :
    // collect() onSuccess { (results : List[Int]) => println(f"Got results ${results.mkString("")}\n") }
  }

  def doRpc(): Future[Int] = {
    val p = Promise[Int]()
    p.success(1)
    p.future
  }

  def doSomeLongRunWork(): Int = 0

  def testAsyncReadAndWriteWithTempFile1()(implicit executor: ExecutionContext): Future[Boolean] = {
    val tempFileCount = 10
    val writeContents: IndexedSeq[Array[Byte]] = (0 until tempFileCount) map { _ =>
      val writeArray = new Array[Byte](128)
      Random.nextBytes(writeArray)
      writeArray
    }

    val resultFuture: Future[Boolean] =
      Future { Files.createTempDirectory("") }
        .flatMap { (tempDir: Path) => // do staff if we create the temp dir successfully
          val resultFutures: IndexedSeq[Future[Boolean]] =
            Range(0, tempFileCount)
              .map { index => (index, Files.createTempFile(tempDir, "", "")) } // create temp files in the temp dir
              .map { (pair: (Int, Path)) => // write content
                val (index, tempFile) = pair
                try {
                  // GuavaFiles.asByteSink(tempFile.toFile).write(writeContents(index))
                  Future(pair) // returns a `Future`
                } catch {
                  case ex: Throwable => // if we failed to write content, delete the temp file and rethrew to fail the future
                    Files.delete(tempFile)
                    throw ex
                }
              }.map { (future: Future[(Int, Path)]) => // read and check content
              future.map { (pair: (Int, Path)) =>
                val (index, tempFile) = pair
                try {
                  val readContent = "Contene" // GuavaFiles.asByteSource(tempFile.toFile).read()
                  Files.delete(tempFile) // delete the temp file

                  // Note : `==` on `Array` calls `equals` to do reference equality comparison,
                  // so use `java.util.Arrays.equals` and `deepEquals` to compare two arrays' contents
                  // java.util.Arrays.equals(readContent, writeContents(index)) // return whether the `readContent` is equal to `writeContents(index)`
                  // java.util.Arrays.deepEquals(readContent, writeContents(index))

                  // or we can use `===` provided by Scalactic:
                  // import org.scalactic.TypeCheckedTripleEquals._
                  // readContent === writeContents(index) // return whether the `readContent` is equal to `writeContents(index)`

                  true
                } catch {
                  case ex: Throwable => // if we failed to read content, delete the temp file and rethrew to fail the future
                    Files.delete(tempFile)
                    throw ex
                }
              }
            }

          Future.sequence(resultFutures) // creates a `Future` that awaits all results
                .map { (results: IndexedSeq[Boolean]) => // check results
                  Files.delete(tempDir) // delete the temp file, we can also do it using `onComplete` on the returned `Future`
                  results.count { res => res } == tempFileCount
                }
        }

    resultFuture
  }

  def testAsyncReadAndWriteWithTempFile2()(implicit executor: ExecutionContext): Future[Boolean] = {
    // do the same thing as `asyncReadAndWriteWithTempFileTest`,
    // but pass failures through the future chain
    val tempFileCount = 10
    val writeContents = Range(0, tempFileCount) map { _ =>
      val writeArray = new Array[Byte](128)
      Random.nextBytes(writeArray)
      writeArray
    }

    /** The message type. */
    sealed trait Status {
      def file: Path
    }
    case class Ready(override val file: Path, index: Int) extends Status
    case class Failed(override val file: Path, error: Throwable) extends Status

    val resultFuture: Future[Boolean] =
      Future { Files.createTempDirectory("") }
        .flatMap { (tempDir: Path) => // do staff if we create the temp dir successfully
          val resultFutures: IndexedSeq[Future[Boolean]] =
            Range(0, tempFileCount)
              .map { index => (index, Files.createTempFile(tempDir, "", "")) } // create temp files in the temp dir
              .map { (pair: (Int, Path)) => // write content
                val (index, tempFile) = pair
                try {
                  // GuavaFiles.asByteSink(tempFile.toFile).write(writeContents(index))
                  // returns a `Future` contains the temp file and the index
                  // the `Ready(tempFile, index` is upcast to `Status`, so that the compiler can infer the return type as `Status`
                  Future(Ready(tempFile, index).asInstanceOf[Status])
                } catch {
                  case ex: Throwable => // if we failed to write content, returns a `Future` that contains the file and the error
                    Future(Failed(tempFile, ex).asInstanceOf[Status])
                }
              }.map { (future: Future[Status]) =>
              future.map { (s: Status) =>
                // read and check content
                val res = s match {
                  case Ready(tempFile, index) =>
                    try {
                      // val readContent = GuavaFiles.asByteSource(tempFile.toFile).read()
                      // java.util.Arrays.equals(readContent, writeContents(index)) // return whether the `readContent` is equal to `writeContents(index)`
                      true
                    } catch {
                      case ex: Throwable => false // if we failed to read content
                    }
                  case failure: Failed        => false
                }

                // always delete the temp file in the single place
                Files.delete(s.file)

                res
              }
            }

          Future.sequence(resultFutures) // creates a `Future` that awaits all results
                .map { (results: IndexedSeq[Boolean]) => // check results
                  Files.delete(tempDir) // delete the temp file, we can also do it using `onComplete` on the returned `Future`
                  results.count { res => res } == tempFileCount
                }
        }

    resultFuture
  }

  /*

  /** @see [[org.scalatest.CompleteLastly]] */
  @throws[IOException]("If failed to create a temp file.")
  def testCompleteLastly[T](work: Path => Future[T])(implicit executionContext: ExecutionContext): (Future[T], Path) = {
    // Trait that provides a complete-lastly construct, which ensures cleanup code in lastly is executed whether the code passed to complete completes abruptly
    // with an exception or successfully results in a Future, FutureOutcome, or other type with an implicit Futuristic instance.
    //
    // If the future-producing code passed to complete throws an exception, the cleanup code passed to lastly will be executed immediately,
    // and the same exception will be rethrown, unless the code passed to lastly also completes abruptly with an exception.
    // In that case, complete-lastly will complete abruptly with the exception thrown by the code passed to lastly (this mimics the behavior of finally).
    //
    // Otherwise, if the code passed to complete successfully returns a Future (or other "futuristic" type),
    // complete-lastly will register the cleanup code to be performed once the future completes and return a new future that
    // will complete once the original future completes and the subsequent cleanup code has completed execution.
    // The future returned by complete-lastly will have the same result as the original future passed to complete, unless the cleanup code throws an exception.
    // If the cleanup code passed to lastly throws an exception, the future returned by lastly will fail with that exception.

    import org.scalatest.CompleteLastly.complete
    import org.scalatest.enablers.Futuristic.futuristicNatureOfFutureOf
    // or extends the `CompleteLastly` to get `complete`

    val tempFilePath: Path = Files.createTempFile("", "")
    val res: Future[T] =
      complete {
        work(tempFilePath)
      } lastly {
        // cleanup, ensure that the temp file is deleted
        Files.delete(tempFilePath)
      }
    // return the result future and the path of the temp file for test
    (res, tempFilePath)
  }

  */

}
