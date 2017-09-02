package net.petitviolet.logging.meta

import scala.annotation.compileTimeOnly
import scala.collection.immutable.Seq
import scala.meta.Dialect.current
import scala.meta._

/**
 * logging `def` name with its input and output using provided `outF`.
 * @param outF a function for logging message
 */
@compileTimeOnly("logging annotation")
class logging(outF: (String) => Unit = println) extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    defn match {
      case d: Defn.Def =>
        d.copy(body = logging.newBody(this)(d))
      case _ =>
        println(this.structure)
        abort("annotate only `def`")
    }
  }
}

private object logging {
  /**
   * create new method body with logging before and after invoking method
   */
  def newBody(self: Stat)(method: Defn.Def): Term = {
    // define complicated names, because to avoid use duplicate names which are defined by programmer
    val name4log = Term.Name("logging$methodNameAndParamsForLogging")
    val pat4log = Pat.Var.Term(name4log)
    val name4result = Term.Name("logging$resultOfMethod")
    val pat4result = Pat.Var.Term(name4result)

    // let `caller` as a lazy val, for cases of `out` will not be invoked on some environment like production
    q"""
     lazy val $pat4log: String = ${caller(method)}
     ${out(self)}("[start]" + $name4log)
     val $pat4result = ${method.body}
     ${out(self)}("[end]" + $name4log + s" => " + $name4result)
     $name4result
     """
  }

}
