package tdauth.futuresandpromises.combinators

import tdauth.futuresandpromises.Executor
import tdauth.futuresandpromises.Future
import tdauth.futuresandpromises.Try
import tdauth.futuresandpromises.standardlibrary.ScalaFPExecutor
import tdauth.futuresandpromises.standardlibrary.ScalaFPPromise
import tdauth.futuresandpromises.standardlibrary.ScalaFPUtil

class CombinatorsUtil extends ScalaFPUtil {

  override def async[T](ex: Executor, f: () => T): Future[T] = {
    val executionContext: scala.concurrent.ExecutionContext = ex.asInstanceOf[ScalaFPExecutor].executionContext
    val future: scala.concurrent.Future[T] = scala.concurrent.Future {
      f()
    }(executionContext)

    new CombinatorsFuture[T](future)
  }

  /**
   * The implementation is based on the following code:
   * <pre>
   * ((f0.first(f1)).first(f2)) ... first(n)
   * </pre>
   * This is done n times. Each time the successful future is removed from the input vector, so only the left futures can be added.
   * Therefore, the original indices have to be stored in a map and reproduced whenever a Try value is added to the resulting vector.
   *
   * Except for the error checking in the beginning, this implementation does not require the use of promises, unline {@link Util#firstN}.
   */
  def firstNWithFirst[T](c: Vector[Future[T]], n: Integer): Future[FirstNResultType[T]] = firstNWithFirstInternal[T](c, n, Vector(), (0 to c.size).map { i => (i, i) }.toMap)

  /**
   * @param indexMap Stores the current indices as keys and the original indices as values. This map is required for accessing the original index of a future when its Try instance is added to the result.
   */
  private def firstNWithFirstInternal[T](c: Vector[Future[T]], n: Integer, resultVector: FirstNResultType[T], indexMap: Map[Int, Int]): Future[FirstNResultType[T]] = {
    if (c.size < n) {
      val p = new ScalaFPPromise[FirstNResultType[T]]()
      p.tryFailure(new RuntimeException("Not enough futures"))

      return p.future()
    }

    var result = c(0).then((t: Try[T]) => (0, t))

    1 to c.size - 1 foreach { i => result = result.first(c(i).then((t: Try[T]) => (i, t))) }

    result.then((t: Try[Tuple2[Int, Try[T]]]) => {
      val r = t.get()
      val index = r._1
      val realIndex = indexMap(index)
      val newResultVector = resultVector :+ (realIndex, r._2)

      if (n > 1) {
        // Remove the element with the given index, so it cannot be added to the completed futures anymore.
        val newC = c.patch(index, Nil, 1)
        /*
         * Remove the current index from the indexMap since the element cannot be used for the result anymore.
         * All current indices which are bigger than the removed index must by decreased to stay valid.
         * Indices which are smaller than the removed index can stay as they are.
         */
        val newIndexMap = (indexMap - index).map {
          case (key, value) => {
            if (key > index) {
              (key - 1, value)
            } else {
              (key, value)
            }
          }
        }
        /*
         * Call this method recursively to add the remaining n futures.
         */
        firstNWithFirstInternal[T](newC, n - 1, newResultVector, newIndexMap).get
      } else {
        newResultVector
      }
    })
  }

  // TODO Implement firstSucc in the same way.
}

object CombinatorsUtil {
  private val util: CombinatorsUtil = new CombinatorsUtil

  def async[T](ex: Executor, f: () => T): Future[T] = {
    util.async[T](ex, f)
  }

  def firstN[T](c: Vector[Future[T]], n: Integer): Future[ScalaFPUtil#FirstNResultType[T]] = {
    util.firstN[T](c, n)
  }

  def firstNWithFirst[T](c: Vector[Future[T]], n: Integer): Future[ScalaFPUtil#FirstNResultType[T]] = {
    util.firstNWithFirst[T](c, n)
  }

  def firstNSucc[T](c: Vector[Future[T]], n: Integer): Future[ScalaFPUtil#FirstNSuccResultType[T]] = {
    util.firstNSucc[T](c, n)
  }
}