package nju.seg.zhangyf.experiment.scala3.dropped

import javax.annotation.ParametersAreNonnullByDefault

/**
 *  @see [[https://docs.scala-lang.org/scala3/reference/overview.html]]
 *
 * @author Zhang Yifan
 */
@ParametersAreNonnullByDefault
private object DroppedConstructs {

  /*
    These constructs are proposed to be dropped without a new construct replacing them.
    The motivation for dropping these constructs is to simplify the language and its implementation.
      DelayedInit,
      Existential types,
      Procedure syntax,
      Class shadowing,
      XML literals,
      Symbol literals,
      Auto application,
      Weak conformance,
      Compound types (replaced by Intersection types),
      Auto tupling (implemented, but not merged).

    The date when these constructs are dropped varies. The current status is:
    Not implemented at all:
      DelayedInit, existential types, weak conformance.
    Supported under -source 3.0-migration:
      procedure syntax, class shadowing, symbol literals, auto application, auto tupling in a restricted form.
    Supported in 3.0, to be deprecated and phased out later:
      XML literals, compound types.
  */

}
