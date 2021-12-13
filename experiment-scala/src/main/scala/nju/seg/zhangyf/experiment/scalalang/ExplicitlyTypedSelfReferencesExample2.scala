package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @see http://docs.scala-lang.org/tutorials/tour/explicitly-typed-self-references.html
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
private object ExplicitlyTypedSelfReferencesExample2 {

  trait SubjectObserver {

    type S <: Subject
    type O <: Observer

    abstract class Subject {
      self: S => // self: S is Explicitly Typed Self Reference, and it is taken as the type of `this` (and `self` is the same as `this`) inside the class

      private var observers: List[O] = List()
      def subscribe(obs: O): Unit = observers = obs :: this.observers
      def publish(): Unit = for (obs <- this.observers) obs.notify(this)
    }

    trait Observer {def notify(sub: S): Unit }

  }

  object SensorReader extends SubjectObserver {

    override type S = Sensor
    override type O = Display

    final class Sensor(val label: String) extends Subject {
      var value: Double = 0.0
      def changeValue(v: Double): Unit = {
        value = v
        publish()
      }
    }

    final class Display extends Observer {
      def println(s: String): Unit = System.out.nn.println(s)
      override def notify(sub: Sensor): Unit = this.println(sub.label + " has value " + sub.value)
    }

  }

  object Test {

    import SensorReader._

    val s1: Sensor = new Sensor("sensor1")
    val s2: Sensor = new Sensor("sensor2")

    def main(args: Array[String]): Unit = {
      val d1 = new Display
      val d2 = new Display
      s1.subscribe(d1)
      s1.subscribe(d2)
      s2.subscribe(d1)
      s1.changeValue(2)
      s2.changeValue(3)
    }
  }

}
