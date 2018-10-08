package tdauth.futuresandpromises.twitter

import java.util.concurrent.Executors

import tdauth.futuresandpromises.AbstractPromiseTest
import tdauth.futuresandpromises.Promise
import tdauth.futuresandpromises.JavaExecutor

class PromiseTest extends AbstractPromiseTest {
  override def getTestName: String = "TwitterPromise"
  override def getPromise: Promise[Int] = new TwitterPromise[Int](executor)

  private val executor = new JavaExecutor(Executors.newSingleThreadExecutor())
}