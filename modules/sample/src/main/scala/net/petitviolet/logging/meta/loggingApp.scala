package net.petitviolet.logging.meta

import java.time.LocalDateTime

object printingApp extends App {
  @printing
  private def heavy(n: Int): Int = {
    Thread.sleep(n)
    n
  }

  heavy(100)

  @printing
  def add(i: Int, j: Int): Int = {
    i + j
  }

  println(add(1, 2))
}

object loggingApp extends App {
  val logger = new {
    def info(s: => String): Unit =
      println(s"${LocalDateTime.now()}: $s")
  }

  @logging(logger.info)
  def add(i: Int, j: Int): Int = {
    i + j
  }
  println(add(1, 2))

  @logging(logger.info)
  def add2(i: Int)(j: Int)(): Int = {
    i + j
  }

  println(add(1, 2))
  println(add2(2)(3))
  val add4: Int => () => Int = add2(4)
  println(add4(5)())

  case class User(name: String) extends AnyVal {
    @logging(println) def greet(s: String = "")(implicit i: Int): String = s"hello $name! * $i"
  }

  @logging
  def create(name: String): User = {
    User(name)
  }
  implicit val i: Int = 100

  println(create("Alice"))
  println(create("Alice").greet())
}
