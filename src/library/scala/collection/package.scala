package scala

/**
 * Contains the base traits and objects needed to use and extend Scala's collection library.
 *
 * == Guide ==
 *
 * A detailed guide for the collections library is avaialble
 * at [[http://www.scala-lang.org/docu/files/collections-api]].
 *
 * == Using Collections ==
 *
 * It is convienient to treat all collections as either
 * a [[scala.collection.Traversable]] or [[scala.collection.Iterable]], as
 * these traits define the vast majority of operations
 * on a collection.
 *
 * Collections can, of course, be treated as specifically as needed, and
 * the library is designed to ensure that
 * the methods that transform collections will return a collection of the same
 * type: {{{
 * scala> val array = Array(1,2,3,4,5,6)
 * array: Array[Int] = Array(1, 2, 3, 4, 5, 6)
 *
 * scala> array map { _.toString }
 * res0: Array[java.lang.String] = Array(1, 2, 3, 4, 5, 6)
 *
 * scala> val list = List(1,2,3,4,5,6)
 * list: List[Int] = List(1, 2, 3, 4, 5, 6)
 *
 * scala> list map { _.toString }
 * res1: List[java.lang.String] = List(1, 2, 3, 4, 5, 6)
 *
 * }}}
 *
 * == Creating Collections ==
 *
 * The most common way to create a collection is to use the companion objects as factories.
 * Of these, the three most common
 * are [[scala.collection.immutable.Seq]], [[scala.collection.immutable.Set]], and [[scala.collection.immutable.Map]].  Their
 * companion objects are all available
 * as type aliases the either the [[scala]] package or in `scala.Predef`, and can be used
 * like so:
 * {{{
 * scala> val seq = Seq(1,2,3,4,1)
 * seq: Seq[Int] = List(1, 2, 3, 4, 1)
 *
 * scala> val set = Set(1,2,3,4,1)
 * set: scala.collection.immutable.Set[Int] = Set(1, 2, 3, 4)
 *
 * scala> val map = Map(1 -> "one",2 -> "two", 3 -> "three",2 -> "too")
 * map: scala.collection.immutable.Map[Int,java.lang.String] = Map((1,one), (2,too), (3,three))
 * }}}
 *
 * It is also typical to use the [[scala.collection.immutable]] collections over those
 * in [[scala.collection.mutable]]; The types aliased in the [[scala]] package and
 * the `scala.Predef` object are the immutable versions.
 *
 * Also note that the collections library was carefully designed to include several implementations of
 * each of the three basic collection types. These implementations have specific performance
 * characteristics which are described
 * in [[http://www.scala-lang.org/docu/files/collections-api the guide]].
 *
 * === Converting between Java Collections ===
 *
 * The `JavaConversions` object provides implicit defs that will allow mostly seamless integration
 * between Java Collections-based APIs and the Scala collections library.
 *
 */
package object collection {
  import scala.collection.generic.CanBuildFrom // can't refer to CanBuild here

  /** Provides a CanBuildFrom instance that builds a specific target collection (`To') irrespective of the original collection (`From').
   */
  def breakOut[From, T, To](implicit b : CanBuildFrom[Nothing, T, To]) =
    new CanBuildFrom[From, T, To] { // TODO: could we just return b instead?
      def apply(from: From) = b.apply() ; def apply() = b.apply()
    }
}
