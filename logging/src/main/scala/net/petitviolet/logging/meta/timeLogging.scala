package net.petitviolet.logging.meta

import scala.annotation.{ StaticAnnotation, compileTimeOnly }
import scala.meta._

@compileTimeOnly("timeLogging not expanded")
class timeLogging(outF: (String) => Unit = println, option: MetaLoggingOption = Full) extends StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    defn match {
      case d: Defn.Def =>
        d.copy(body = timeLogging.newBody(this)(d))
      case _ =>
        abort("annotate only function!")
    }
  }
}

private object timeLogging {
  /**
   * create new method body with logging before and after invoking method
   */
  def newBody(self: Stat)(method: Defn.Def): Term = {
    // define complicated names, because to avoid use duplicate names which are defined by programmer
    val loggingOption = extractLoggingOption(self)
    val time = q"System.currentTimeMillis"
    val name4log = Term.Name("timeLogging$methodNameAndParamsForLogging")
    val pat4log = Pat.Var.Term(name4log)
    val name4result = Term.Name("timeLogging$resultOfMethod")
    val pat4result = Pat.Var.Term(name4result)

    // names for tracking method invocation time
    val name4Start = Term.Name("timeLogging$startTime")
    val pat4Start = Pat.Var.Term(name4Start)
    val name4End = Term.Name("timeLogging$endTime")
    val pat4End = Pat.Var.Term(name4End)

    // let `caller` as a lazy val, for cases of `out` will not be invoked on some environment like production
    q"""
     lazy val $pat4log: String = ${caller(method, loggingOption)}
     ${out(self)}("[start]" + $name4log)
     val $pat4Start = $time
     val $pat4result = ${method.body}
     val $pat4End = $time
     ${out(self)}("[end][" + ($name4End - $name4Start) + " ms]" + $name4log + s" => " +
        ${showResult(name4result, loggingOption)})
     $name4result
     """
  }
}
