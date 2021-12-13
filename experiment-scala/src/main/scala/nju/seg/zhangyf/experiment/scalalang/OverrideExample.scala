package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
private object OverrideExample {

  /**
    * In scala, overriding a val is the same as override a method, which means vals and vars are also resolved at run time.
    * Instead, in Java, we can only hide variables.
    */
  def overrideVal(): Unit = {

    class CA {

      val x: Int = 1
      var y: Int = 10

      def z: Int = 10

      def z_=(v: Int): Unit = {}

      def mx: Long = 1L
    }

    class CB extends CA {

      override val x: Int = 2
      // override def x : Int = 2 // can not override val as method

      // Error, cannot override a mutable variable by method or var
      // override var y : Int = 20
      // override def y : Int = 20
      // override def y_=(v : Int ) : Unit = {}

      /** var `z` is expand to two methods `z` and `z_=`, which override methods in class CA */
      override var z: Int = 10

      /** also can override getter method as val */
      override val mx: Long = 2L
    }

    val b: CA = new CB

    println(b.x) // print 2
  }

  def overrideMethods(): Unit = {

    class CA

    trait TA extends CA {def me(): Unit = println("TA") }

    trait TB extends CA {def me(): Unit = println("TB") }

    class CC extends CA with TA with TB {

      // by default, method me in class CC is the method me in TB (the method defined in the rightmost trait or class)
      // that is to say, the method in TB override the method in TA
      override def me(): Unit = super[TA].me() // but we can override and call super[TA] to refer to a concert trait
    }
  }
}
