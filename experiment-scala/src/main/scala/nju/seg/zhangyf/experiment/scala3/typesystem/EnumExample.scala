package nju.seg.zhangyf.experiment.scala3.typesystem

import javax.annotation.ParametersAreNonnullByDefault

import ContextualAbstractionExample.Comparator

/**
 * @see [[https://docs.scala-lang.org/scala3/reference/enums/enums.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object EnumExample {

  enum Color:
    case Red, Green, Blue

  enum Color2(val rgb: Int):
    case Red extends Color2(0xFF0000)
    case Green extends Color2(0x00FF00)
    case Blue extends Color2(0x0000FF)
  end Color2 // optional

  // It is possible to add your own definitions to an enum.
  enum Planet(mass: Double, radius: Double):
    private final val G = 6.67300E-11
    def surfaceGravity = G * mass / (radius * radius)
    def surfaceWeight(otherMass: Double) = otherMass * surfaceGravity

    case Mercury extends Planet(3.303e+23, 2.4397e6)
    case Venus   extends Planet(4.869e+24, 6.0518e6)
    // case Earth   extends Planet(5.976e+24, 6.37814e6)
    // case Mars    extends Planet(6.421e+23, 3.3972e6)
    // case Jupiter extends Planet(1.9e+27,   7.1492e7)
    // case Saturn  extends Planet(5.688e+26, 6.0268e7)
    // case Uranus  extends Planet(8.686e+25, 2.5559e7)
    // case Neptune extends Planet(1.024e+26, 2.4746e7)
  end Planet

}
