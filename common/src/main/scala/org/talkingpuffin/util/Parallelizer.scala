package org.talkingpuffin.util

import scala.collection.JavaConversions._
import java.util.concurrent.Executors
import java.util.{ArrayList, Collections}
import java.text.NumberFormat

object Parallelizer extends Loggable {
  /**
   * Runs, in the number of threads requested, the function f, giving it each A of args, returning a List[T]
   */
  def run[T,A](numThreads: Int, args: Iterable[A], f: (A) => T, threadName: String = "Parallel"): Iterable[T] = {
    val timings = Collections.synchronizedList(new ArrayList[Long])
    val pool = Executors.newFixedThreadPool(numThreads, NamedThreadFactory(threadName))
    val result: Iterable[T] = args.map(arg => pool.submit(Threads.callable {
      val startTime = System.currentTimeMillis
      val result = f(arg)
      timings.add(System.currentTimeMillis - startTime)
      result
    })).map(_.get).toList
    pool.shutdown()
    logStats(timings)
    result
  }

  private def calcStdDev(timings: java.util.List[Long], mean: Double): Double = {
    val difSq = timings.map(timing => {
      val dif = timing - mean
      dif * dif
    })
    math.sqrt(difSq.sum / timings.size)
  }

  private def logStats[A, T](timings: java.util.List[Long]) {
    val fmt = NumberFormat.getInstance
    val mean = timings.sum.toDouble / timings.size
    debug(timings.sorted.map(timing => fmt.format(timing)).toList.mkString(", "))
    debug("Mean: " + fmt.format(mean) + ", Std dev: " + fmt.format(calcStdDev(timings, mean)))
  }

}
