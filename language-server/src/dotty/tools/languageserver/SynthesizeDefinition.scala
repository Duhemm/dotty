package dotty.tools.languageserver

import dotty.tools.dotc.ast.untpd
import dotty.tools.dotc.ast.tpd
import dotty.tools.dotc.core.Contexts.Context
import dotty.tools.dotc.core.Flags._
import dotty.tools.dotc.core.Names.Name
import dotty.tools.dotc.core.Symbols.{Symbol, TermSymbol}
import dotty.tools.dotc.core.Types._
import dotty.tools.dotc.printing.{DecompilerPrinter, PlainPrinter, RefinedPrinter, ReplPrinter}

object SynthesizeDefinition {

  private def undefined(implicit ctx: Context) = tpd.ref(ctx.definitions.Predef_undefinedR)
  private def printer(implicit ctx: Context) = new OverridePrinter(ctx)

  def definitionsFor(syms: List[Symbol])(implicit ctx: Context): String = {
    val buffer = new StringBuilder
    syms.filter(_.exists).foreach(definitionFor(_, buffer))
    // println(buffer)
    buffer.toString
  }

  def definitionFor(symbol: Symbol, buffer: StringBuilder)(implicit ctx: Context): Unit = {
    if (!symbol.is(allOf(Mutable, Accessor))) { // No override for var setters
      val tree =
        if (symbol.is(Method)) makeDef(symbol.copy(flags = symbol.flags | Override).asTerm)
        else if (symbol.is(Mutable)) makeVal(symbol.asTerm) // Dont' add `override` for var`s
        else makeVal(symbol.copy(flags = symbol.flags | Override).asTerm)

      // buffer.append(tree.show)
      buffer.append(printer.toText(tree).mkString(200, false))
      buffer.append(System.lineSeparator)
    }
  }

  def makeDef(symbol: TermSymbol)(implicit ctx: Context): tpd.Tree = {
    val tparams = Nil
    symbol.paramRef
    val vparamss = symbol.info match {
      case mtpe: MethodType =>
        println("mtpe = " + mtpe.show)
        println("-----> " + mtpe)
        mtpe.paramNamess.zip(mtpe.paramInfoss).map { (paramsNames, paramsTypes) =>
          paramsNames.zip(paramsTypes).map { (name, tpe) =>
            println("tpe = " + tpe.show)
            ctx.newSymbol(owner = symbol,
                          name = name,
                          flags = TermParam,
                          info = tpe)
          }
        }
      case _ =>
        Nil
    }
    val tpt = symbol.info.finalResultType
    val rhs = undefined
    tpd.DefDef(symbol, tparams, vparamss, tpt, rhs)
  }

  def makeVal(symbol: TermSymbol)(implicit ctx: Context): tpd.Tree = {
    tpd.ValDef(symbol, undefined)
  }
}

private class OverridePrinter(_ctx: Context) extends ReplPrinter(_ctx) {
}
