package net.petitviolet.mlogging

package object meta {
  import scala.meta._

  private[meta] def extractLoggingOption(self: Stat): MetaLoggingOption = self match {
    case Term.New(Template(_, Seq(Term.Apply(_,
      _ +: (optName: Term.Arg) +: _)), _, _)) =>
      val option = MetaLoggingOption.valueOf(optName)
      option
    case _ =>
      Full
  }

  private[meta] def out(self: Stat): Term = self match {
    case Term.New(Template(_, Seq(Term.Apply(_,
      (_out: Term.Arg) +: _)), _, _)) =>
      // convert Term.Arg to Term.Name to call `apply`
      // we cannot get Term.Arg as scala function object
      Term.Name(_out.syntax)
    case _ =>
      // use `println` as default
      Term.Name("println")
  }

  private[meta] def showResult(resultName: Term.Name, loggingOption: MetaLoggingOption): Term = {
    loggingOption match {
      case Output | Full =>
        resultName
      case _ =>
        Lit.String("(...)")
    }
  }

  private[meta] def caller(method: Defn.Def, loggingOption: MetaLoggingOption): Term = {
    loggingOption match {
      case Input | Full =>
        q"${Lit.String(method.name.value)} + ${params(method.paramss)}"
      case _ =>
        q"${Lit.String(method.name.value)} + ${Lit.String("(...)")}"
    }
  }

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

  sealed trait MetaLoggingOption
  case object Simple extends MetaLoggingOption
  case object Full extends MetaLoggingOption
  case object Input extends MetaLoggingOption
  case object Output extends MetaLoggingOption

  private[meta] object MetaLoggingOption {
    def valueOf(arg: Term.Arg): MetaLoggingOption = {
      arg.syntax match {
        case "Simple" => Simple
        case "Full"   => Full
        case "Input"  => Input
        case "Output" => Output
        case _        => abort(s"Unknown option: $arg")
      }
    }
  }
}
