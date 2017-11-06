package net.petitviolet.logging.meta_sample

import java.time.LocalDateTime

import net.petitviolet.logging.meta._

import scala.util.Random

object timeLoggingApp extends App {
  val logger = new {
    def info(s: => String): Unit =
      println(s"${LocalDateTime.now()}: $s")
  }

  @timeLogging(logger.info)
  def add(i: Int, j: Int): Int = {
    i + j
  }
  println(add(1, 2))

  @timeLogging(logger.info)
  def add2(i: Int)(j: Int)(): Int = {
    Thread.sleep(100)
    i + j
  }

  println(add2(2)(3))
  val add4: Int => () => Int = add2(4)
  println(add4(5)())

  case class User(name: String) extends AnyVal {
    @timeLogging(println) def greet(s: String = "")(implicit i: Int): String = s"hello $name! * $i"
  }

  @timeLogging
  def create(name: String): User = {
    User(name)
  }
  implicit val i: Int = 100

  println(create("Alice"))
  println(create("Alice").greet())

  @timeLogging(println, Input)
  def input(name: String): User = {
    Thread.sleep(Random.nextInt(200))
    User(name)
  }

  @timeLogging(println, Output)
  def output(name: String): User = {
    Thread.sleep(Random.nextInt(200))
    User(name)
  }

  @timeLogging(println, Simple)
  def simple(name: String): User = {
    Thread.sleep(Random.nextInt(200))
    User(name)
  }

  @timeLogging(println, Full)
  def full(name: String): User = {
    Thread.sleep(Random.nextInt(200))
    User(name)
  }

  println("================")

  simple("simple")
  input("input")
  output("output")
  full("full")
}
