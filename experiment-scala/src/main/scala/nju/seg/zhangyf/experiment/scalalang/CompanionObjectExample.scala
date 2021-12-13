package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

// import shapeless.test.illTyped

/**
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
private object CompanionObjectExample {

  class A {

    private class ClaInAClass

    private var valueClaInAClass: ClaInAClass = _

    // Note: Even though we can access private members in the companion object, we still need to qualify or import them as usual

    // `import A.ClaInAObj` and the use `ClaInAObj`, or just use `A.ClaInAObj`.
    private type TypeClaInAObj = A.ClaInAObj
    private val valueInClassA: Int = A.valueInObjA

    private var valueTypeClaInAObj: TypeClaInAObj = new TypeClaInAObj // or new A.ClaInAObj
  }

  object A {

    private class ClaInAObj

    private val valueInObjA: Int = 0

    def test(): Unit = {

      val a1: A = new A
      val a2: A = new A

      // class `a1.ClaInAClass` and `a2.ClaInAClass` are both path dependent types, thus the following code is error:
      // illTyped { "a1.valueClaInAClass = a2.valueClaInAClass" }
      // Error: type mismatch;
      // found   : a2.ClaInAClass
      // required: a1.ClaInAClass

      // but we can use the type of the inner class `A#ClaInAClass`, which is super type of
      var valueClaInAClass: A#ClaInAClass = a1.valueClaInAClass
      valueClaInAClass = a2.valueClaInAClass

      // on the other hand, `a1.TypeClaInAObj` and `a2.TypeClaInAObj` are both alias of `A.ClaInAObj`
      a1.valueTypeClaInAObj = a2.valueTypeClaInAObj
    }
  }

}
