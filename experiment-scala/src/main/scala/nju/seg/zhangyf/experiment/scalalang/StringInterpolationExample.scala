package nju.seg.zhangyf.experiment.scalalang

import javax.annotation.ParametersAreNonnullByDefault

/** String Interpolation allows users to embed variable references directly in processed string literals.
 *
 * For example, the literal `s"Hello, $name"` is a processed string literal.
 * This means that the compiler does some additional work to this literal.
 * A processed string literal is denoted by a set of characters preceding the `"`.
 *
 * @see [[http://docs.scala-lang.org/overviews/core/string-interpolation.html]]
 * @author Zhang Yifan
 */
//noinspection SpellCheckingInspection,ScalaUnusedSymbol
@ParametersAreNonnullByDefault
private object StringInterpolationExample {

  /** Scala provides three string interpolation methods out of the box: s, f and raw. */
  def buildInInterpolators(): Unit = {

    //region s String interpolator

    // Prepending s to any string literal allows the usage of variables directly in the string.

    val name = "James"
    assert(s"Hello, $name" == "Hello, James")

    // String interpolators can also take arbitrary expressions.
    //  Any arbitrary expression can be embedded in `${}`.
    assert(s"1 + 1 = ${ (1 + 1).toString }" == "1 + 1 = 2")

    //endregion s String interpolator

    //region f String interpolator

    // Prepending f to any string literal allows the creation of simple formatted strings, similar to printf in other languages.
    // When using the f interpolator, all variable references should be followed by a printf-style format string, like %d.

    val height = 1.9d
    assert(f"$name%s is $height%2.2f meters tall" == "James is 1.90 meters tall")

    // The f interpolator is typesafe.
    // If you try to pass a format string that only works for integers but pass a double, the compiler will issue an error.
    // shapeless.test.illTyped { f"$height%4d" }
    //   error: type mismatch;
    //   found   : Double
    //   required: Int

    // The f interpolator makes use of the string format utilities available from Java.
    // The formats allowed after the % character are outlined in the Formatter javadoc.
    // (http://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html#detail)
    // If there is no % character after a variable definition a formatter of %s (String) is assumed.

    //endregion f String interpolator

    //region raw Interpolator

    // The raw interpolator is similar to the s interpolator except that it performs no escaping of literals within the string.
    // The raw interpolator is useful when you want to avoid having expressions like \n turn into a return character.
    println(raw"a\nb") // print "a\nb"

    //endregion raw Interpolator
  }

  // @note scala.util.parsing.json is deprecated since Scala 3.
  // import scala.util.parsing.json.{ JSON, JSONObject }

  //  /** In Scala, all processed string literals are simple code transformations.
  //    *
  //    * Anytime the compiler encounters a string literal of the form: id"string content"
  //    * it transforms it into a method call (id) on an instance of StringContext.
  //    * This method can also be available on implicit scope.
  //    * To define our own string interpolation, we simply need to create an implicit class that adds a new method to StringContext.
  //    */
  //  @deprecated
  //  def customStringLiterals(): Unit = {
  //    // In this example, we’re attempting to create a JSON literal syntax using string interpolation.
  //    // The JsonHelper implicit class must be in scope to use this syntax.
  //
  //    // Note: We can extends AnyVal to prevent runtime instantiation.
  //    // But value classes may not be a local class.
  //    implicit class JsonHelper(val sc: StringContext) /* extends AnyVal */ {
  //
  //      def json(args: Any*): JSONObject = {
  //        // Each of the string portions of the processed string are exposed in the StringContext’s parts member.
  //        // Each of the expression values is passed into the json method’s args parameter.
  //        // The json method takes this and generates a big string which it then parses into JSON.
  //        // A more sophisticated implementation could avoid having to generate this string
  //        // and simply construct the JSON directly from the raw strings and expression values.
  //        val strings = sc.parts.iterator
  //        val expressions = args.iterator
  //        val buf = new StringBuffer(strings.next())
  //        while (strings.hasNext) {
  //          buf append expressions.next()
  //          buf append strings.next()
  //        }
  //        assert(strings.isEmpty && expressions.isEmpty)
  //        // note : `JSON.parseFull` requires the all keys and values in the JSON string must be surrounded with `"`
  //        val json: Option[Any] = JSON.parseFull(buf.toString)
  //        val map: Map[String, Any] = json.get.asInstanceOf[Map[String, Any]]
  //        JSONObject(map)
  //      }
  //
  //    }
  //
  //    val name = "James"
  //    val id = 10
  //    // the result of such a formatted string literal `json"""{ "name": "$name", "id": "$id" }"""` would not be a string, but a JSONObject.
  //    val res: JSONObject = json"""{ "name": "$name", "id": "$id" }"""
  //    // The compiler rewrites the expression to the following expression:
  //    val res2: JSONObject = new StringContext("{ \"name\": \"", "\", \"id\": \"", "\" }").json(name, id)
  //    // The implicit class is then used to rewrite it to the following:
  //    val res3: JSONObject = new JsonHelper(new StringContext("{ \"name\": \"", "\", \"id\": \"", "\" }")).json(name, id)
  //  }

}
