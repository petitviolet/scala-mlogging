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
   * construct log string for method parameters
   */
  private def params(paramss: Seq[Seq[Term.Param]]): Term = {
    def empty: Term = Lit.String("")
    paramss.map { params: Seq[Term.Param] =>
      params
        .map { p => Term.Name(p.name.value) }
        .reduceLeftOption { (accN: Term, n: Term) =>
          // connect with ', ' parameters in the same parenthesis
          q"$accN + ${Lit.String(", ")} + $n"
        }.getOrElse { empty }
    }.reduceLeftOption { (acc: Term, t: Term) =>
      // connect with close and open parenthesis
      q"$acc + ${Lit.String(")(")} + $t"
    }.fold(empty) { t =>
      // surround with open and close parenthesis
      q"${Lit.String("(")} + $t + ${Lit.String(")")}"
    }
  }

  /**
   * create new method body with logging before and after invoking method
   */
  def newBody(self: Stat)(method: Defn.Def): Term = {
    val out: Term = self match {
      case Term.New(Template(_, Seq(Term.Apply(_, Seq(_out: Term.Arg))), _, _)) =>
        // convert Term.Arg to Term.Name to call `apply`
        // we cannot get Term.Arg as scala function object
        Term.Name(_out.syntax)
      case _ =>
        // use `println` as default
        Term.Name("println")
    }
    val caller: Term = q"${Lit.String(method.name.value)} + ${params(method.paramss)}"
    // define complicated names, because to avoid use duplicate names which are defined by programmer
    val name4log = Term.Name("logging$methodNameAndParamsForLogging")
    val pat4log = Pat.Var.Term(name4log)
    val name4result = Term.Name("logging$resultOfMethod")
    val pat4result = Pat.Var.Term(name4result)

    // let `caller` as a lazy val, for cases of `out` will not be invoked on some environment like production
    q"""
     lazy val $pat4log: String = $caller
     $out("[start]" + $name4log)
     val $pat4result = ${method.body}
     $out("[end]" + $name4log + s" => " + $name4result)
     $name4result
     """
  }

}
