/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2010, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */



package scala.collection
package immutable

import generic._
import mutable.{ Builder, ListBuffer }
import annotation.tailrec

/** `Queue` objects implement data structures that allow to
 *  insert and retrieve elements in a first-in-first-out (FIFO) manner.
 *
 *  @author  Erik Stenman
 *  @version 1.0, 08/07/2003
 *  @since   1
 *  @define Coll immutable.Queue
 *  @define coll immutable queue
 *  @define mayNotTerminateInf
 *  @define willNotTerminateInf
 */
@serializable
@SerialVersionUID(-7622936493364270175L)
class Queue[+A] protected(protected val in: List[A], protected val out: List[A])
            extends LinearSeq[A]
            with GenericTraversableTemplate[A, Queue]
            with LinearSeqLike[A, Queue[A]] {

  override def companion: GenericCompanion[Queue] = Queue

  /** Returns the `n`-th element of this queue.
   *  The first element is at position 0.
   *
   *  @param  n index of the element to return
   *  @return   the element at position `n` in this queue.
   *  @throws Predef.NoSuchElementException if the queue is too short.
   */
  override def apply(n: Int): A = {
    val len = out.length
    if (n < len) out.apply(n)
    else {
      val m = n - len
      if (m < in.length) in.reverse.apply(m)
      else throw new NoSuchElementException("index out of range")
    }
  }

  /** Returns the elements in the list as an iterator
   */
  override def iterator: Iterator[A] = (out ::: in.reverse).iterator

  /** Checks if the queue is empty.
   *
   *  @return true, iff there is no element in the queue.
   */
  override def isEmpty: Boolean = in.isEmpty && out.isEmpty

  override def head: A =
    if (out.nonEmpty) out.head
    else if (in.nonEmpty) in.last
    else throw new NoSuchElementException("head on empty queue")

  override def tail: Queue[A] =
    if (out.nonEmpty) new Queue(in, out.tail)
    else if (in.nonEmpty) new Queue(Nil, in.reverse.tail)
    else throw new NoSuchElementException("tail on empty queue")

  /** Returns the length of the queue.
   */
  override def length = in.length + out.length

  /** Creates a new queue with element added at the end
   *  of the old queue.
   *
   *  @param  elem        the element to insert
   */
  @deprecated("Use the method <code>enqueue</code> from now on.")
  def +[B >: A](elem: B) = enqueue(elem)

  /** Creates a new queue with element added at the end
   *  of the old queue.
   *
   *  @param  elem        the element to insert
   */
  def enqueue[B >: A](elem: B) = new Queue(elem :: in, out)

  /** Returns a new queue with all all elements provided by
   *  an <code>Iterable</code> object added at the end of
   *  the queue.
   *  The elements are prepended in the order they
   *  are given out by the iterator.
   *
   *  @param  iter        an iterable object
   */
  @deprecated("Use the method <code>enqueue</code> from now on.")
  def +[B >: A](iter: Iterable[B]) = enqueue(iter)

  /** Returns a new queue with all elements provided by
   *  an <code>Iterable</code> object added at the end of
   *  the queue.
   *  The elements are prepended in the order they
   *  are given out by the iterator.
   *
   *  @param  iter        an iterable object
   */
  def enqueue[B >: A](iter: Iterable[B]) =
    new Queue(iter.toList.reverse ::: in, out)

  /** Returns a tuple with the first element in the queue,
   *  and a new queue with this element removed.
   *
   *  @throws Predef.NoSuchElementException
   *  @return the first element of the queue.
   */
  def dequeue: (A, Queue[A]) = out match {
    case Nil if !in.isEmpty => val rev = in.reverse ; (rev.head, new Queue(Nil, rev.tail))
    case x :: xs            => (x, new Queue(in, xs))
    case _                  => throw new NoSuchElementException("dequeue on empty queue")
  }

  /** Returns the first element in the queue, or throws an error if there
   *  is no element contained in the queue.
   *
   *  @throws Predef.NoSuchElementException
   *  @return the first element.
   */
  def front: A = head

  /** Returns a string representation of this queue.
   */
  override def toString() = mkString("Queue(", ", ", ")")
}

/** $factoryInfo
 *  @define Coll immutable.Queue
 *  @define coll immutable queue
 */
object Queue extends SeqFactory[Queue] {
  /** $genericCanBuildFromInfo */
  implicit def canBuildFrom[A]: CanBuildFrom[Coll, A, Queue[A]] = new GenericCanBuildFrom[A]
  def newBuilder[A]: Builder[A, Queue[A]] = new ListBuffer[A] mapResult (x => new Queue[A](Nil, x.toList))
  override def empty[A]: Queue[A] = new Queue[A](Nil, Nil)
  override def apply[A](xs: A*): Queue[A] = new Queue[A](Nil, xs.toList)

  @deprecated("Use Queue.empty instead")
  val Empty: Queue[Nothing] = Queue()
}
