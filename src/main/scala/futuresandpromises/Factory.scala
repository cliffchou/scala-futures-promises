package tdauth.futuresandpromises

/**
 * The factory allows concrete implementation of the traits to create instances of the concrete types.
 * These are required for the derived methods in the traits.
 */
trait Factory {
  // This method is required since Promise is defined as trait:
  def createPromise[T]: Promise[T]

  // These two methods are required since Try is defined as trait:
  def createTryFromValue[T](v: T): Try[T]
  def createTryFromException[T](e: Throwable): Try[T]
}