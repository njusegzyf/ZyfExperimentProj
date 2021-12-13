package nju.seg.zhangyf.experiment.scalalanglib.concurrent

/**
 * @author Zhang Yifan
 */
object StmExample {

  def main(args: Array[String]): Unit = {
    // https://github.com/nbronson/scala-stm
    // http://nbronson.github.io/scala-stm

    import scala.concurrent.stm._

    val x: Ref[Int] = Ref(0) // allocate a Ref[Int]
    val y: Ref[String] = Ref.make[String]() // type-specific default
    val z: Ref.View[Int] = x.single

    atomic { implicit txn: InTxn =>
      val i = x() // read, val i = x.apply()
      y() = "x was " + i // write, y.update("x was " + i)
      val eq: Boolean = atomic { implicit txn => // nested atomic
        x() == z() // both Ref and Ref.View can be used inside atomic
      }
      assert(eq)
      y.set(y.get + ", long-form access")
    }

    // Ref.single returns an instance of Ref.View, which acts just like the original Ref except that it can also be accessed outside an atomic block.
    // Each method on Ref.View acts like a single-operation transaction, hence the name.
    // Ref.View provides several methods that perform both a read and a write, such as swap, compareAndSet and transform.
    // If an atomic block only accesses a single Ref, it might be more concise and more efficient to use a Ref.View.
    println("y was '" + y.single() + "'")
    println("z was " + z())

    (atomic { implicit txn =>
      y() = y() + ", first alternative"
      if (x getWith { _ > 0 }) // read via a function
        retry // try alternatives or block
    }
     orAtomic { implicit txn =>
      y() = y() + ", second alternative"
     })

    val prev = z.swap(10) // atomic swap
    val success = z.compareAndSet(10, 11) // atomic compare-and-set
    z.transform { _ max 20 } // atomic transformation
    val pre = y.single // .getAndTransform { _.toUpperCase }
    val post = y.single.transformAndGet { _.filterNot { _ == ' ' } }
  }

}
