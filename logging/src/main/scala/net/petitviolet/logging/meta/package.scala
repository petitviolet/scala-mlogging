package net.petitviolet.logging

package object meta {
  import scala.meta._

  def out(self: Stat): Term = self match {
    case Term.New(Template(_, Seq(Term.Apply(_, Seq(_out: Term.Arg))), _, _)) =>
      // convert Term.Arg to Term.Name to call `apply`
      // we cannot get Term.Arg as scala function object
      Term.Name(_out.syntax)
    case _ =>
      // use `println` as default
      Term.Name("println")
  }

  def caller(method: Defn.Def): Term = q"${Lit.String(method.name.value)} + ${params(method.paramss)}"

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
}
