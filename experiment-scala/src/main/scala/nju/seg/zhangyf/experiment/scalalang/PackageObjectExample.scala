package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/**
  * @see [[http://www.scala-lang.org/docu/files/packageobjects/packageobjects.html Package object in Scala]]
  * @see [[https://my.oschina.net/aiguozhe/blog/35202 浅谈Scala 2.8的包对象（package object) ]]
  *
  * @author Zhang Yifan
  */
@ParametersAreNonnullByDefault
private object PackageObjectExample {

  //region An example from Play-scala module

  /*
    import play.data.validation._
    import javax.persistence

    package play {

      package db {
      import annotation.target.field

      package object jpa{
          //enums
          val CascadeType = CascadeTypeWrapper
          val LockModeType = LockModeTypeWrapper
          val FetchType = FetchTypeWrapper
          //classes
          type Table = persistence.Table
          type Entity = persistence.Entity
          type Inheritance = persistence.Inheritance

          //javax.persistence field
          type  AttributeOverrides = persistence.AttributeOverrides @field
          type  Basic = persistence.Basic  @field
          type  Column = persistence.Column @field
      // ...
    }
   */

  //endregion An example

}
